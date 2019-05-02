import at.fhv.tmd.common.Tuple;
import at.fhv.transportClassifier.FhGpsLoggerImporterManager;
import at.fhv.transportClassifier.Importer1;
import at.fhv.transportClassifier.ImporterV2;
import at.fhv.transportClassifier.ImporterV4;
import at.fhv.transportClassifier.MobiTrackerFileContainer;
import at.fhv.transportClassifier.MobiTrackerImporter;
import at.fhv.transportClassifier.MyTracksImporter;
import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.HibernateUtil;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateBoundingBoxRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateDatapointsRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingInfoRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentBagRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentTypeRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.TrackingInfoTypeRepository;
import at.fhv.transportClassifier.dal.interfaces.Spezification;
import at.fhv.transportClassifier.dal.interfaces.TrackingInfoFilenameSpezification;
import at.fhv.transportClassifier.dal.interfaces.TrackingRepository;
import at.fhv.transportClassifier.scheffknechtgpx.GpxImporter;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.hibernate.Session;

/**
 * Created by Johannes on 14.02.2017.
 */
public class Importer {

  private static int deviceSystemTimeNotSequenqal = 0;
  private static int sensorTimeNotSequenqual = 0;
  private static int counter = 0;
  TrackingRepository trackingRepository;
  HibernateTrackingInfoRepository trackingInfoRepository;
  Session session;
  org.hibernate.SessionFactory sessionFactory;
  FhGpsLoggerImporterManager fhGpsLoggerImporter = new FhGpsLoggerImporterManager();
  HashSet<String> alreadyImportedFiles = new HashSet<String>();

  public static void main(String args[]) {
    Importer importer = new Importer();
    importer.init();
    importer.initAlreadyImportedFileNames();

//        importer.importGpsFilesFromMobyTracker();
//
    importer.importGPXFiles();

    importer.fhGpsLoggerImporter.addImporter(new ImporterV4());
    importer.fhGpsLoggerImporter.addImporter(new ImporterV2());
    importer.fhGpsLoggerImporter.addImporter(new Importer1());

//        importer.importFromGpxFiles();
//        importer.importFhGpsLogger("D:\\\\Studium\\\\Master\\\\Masterarbeit\\\\fhgpsLoggerTracks\\\\v2");
//        importer.importFhGpsLogger("D:\\\\Studium\\\\Master\\\\Masterarbeit\\\\Tracks\\\\fhgpsLoggerTracks");

    importer.session.close();
    importer.sessionFactory.close();
    System.exit(0);
  }

  private static void test(Tracking tracking) {

    IExtendedGpsPoint lastGpsPoint = null;
    int counter3 = 0;
    for (IExtendedGpsPoint gpsPoint : tracking.getGpsPoints()) {

      if (lastGpsPoint != null) {

        if (lastGpsPoint.getDeviceSavingSystemTime() != null) {
          if (!lastGpsPoint.getDeviceSavingSystemTime()
              .isBefore(gpsPoint.getDeviceSavingSystemTime())) {
            deviceSystemTimeNotSequenqal++;
            System.out.println("deviceTime");

          }
        }

        if (!lastGpsPoint.getSensorTime().isBefore(gpsPoint.getSensorTime())) {
          sensorTimeNotSequenqual++;
          System.out.println("sensorTime");

        }


      }

      lastGpsPoint = gpsPoint;
      counter3++;

    }

    System.out.println(counter++);


  }

  private void init() {
    sessionFactory = HibernateUtil.getSessionFactory();
    session = sessionFactory.openSession();
    HibernateSessionMananger hibernateSessionMananger = new HibernateSessionMananger(session);
    TrackingInfoTypeRepository trackingInfoTypeRepository = new TrackingInfoTypeRepository(
        hibernateSessionMananger);
    trackingInfoRepository = new HibernateTrackingInfoRepository(hibernateSessionMananger,
        trackingInfoTypeRepository);
    HibernateDatapointsRepository hibernateDatapointsRepository = new HibernateDatapointsRepository(
        hibernateSessionMananger);
    HibernateTrackingSegmentTypeRepository hibernateTrackingSegmentTypeRepository = new HibernateTrackingSegmentTypeRepository(
        hibernateSessionMananger);
    HibernateBoundingBoxRepository hibernateBoundingBoxRepository = new HibernateBoundingBoxRepository(
        hibernateSessionMananger);
    HibernateTrackingSegmentRepository hibernateTrackingSegmentRepository = new HibernateTrackingSegmentRepository(
        hibernateSessionMananger, hibernateBoundingBoxRepository,
        hibernateTrackingSegmentTypeRepository);
    HibernateTrackingSegmentBagRepository hibernateTrackingSegmentBagRepository = new HibernateTrackingSegmentBagRepository(
        hibernateSessionMananger, hibernateTrackingSegmentRepository);
    HibernateTrackingRepository hibernateTrackingRepository = new HibernateTrackingRepository(
        hibernateSessionMananger, trackingInfoRepository, hibernateTrackingSegmentBagRepository,
        hibernateBoundingBoxRepository, hibernateDatapointsRepository);

    trackingRepository = hibernateTrackingRepository;


  }

  private void initAlreadyImportedFileNames() {
    Spezification<TrackingInfo> spezification = trackingInfoRepository
        .getSpezification(TrackingInfoFilenameSpezification.class.getName());

    List<TrackingInfo> queryResult = trackingInfoRepository.query(spezification);
    for (TrackingInfo trackingInfo : queryResult) {
      if (trackingInfo.getInfoName().equals(Constants.FILENAME)) {
        alreadyImportedFiles.add(trackingInfo.getInfoValue());
      }
    }
  }

  private boolean notImportedAndFile(File trackingFiles) {
    if (!trackingFiles.isFile()) {
      return false;
    }
    String name = trackingFiles.getName();

    return !alreadyImportedFiles.contains(name);
  }

  private void importFhGpsLogger(String dir) {
    File file = new File(dir);
    File[] files = file.listFiles();

    Spezification<TrackingInfo> spezification = trackingInfoRepository
        .getSpezification(TrackingInfoFilenameSpezification.class.getName());

    List<TrackingInfo> queryResult = trackingInfoRepository.query(spezification);

    for (File trackingFiles : files) {
      if (!trackingFiles.isFile()) {
        continue;
      }
      String name = trackingFiles.getName();

//            if (containsName(name,queryResult)) {
//                continue;
//            }

      Tracking tracking = null;
      try {
        tracking = fhGpsLoggerImporter.loadTracking(trackingFiles);
      } catch (IOException e) {
        e.printStackTrace();
      }
      if (tracking != null) {
//                trackingRepository.plus(tracking);
        test(tracking);


      }
    }
  }

  private void importGPXFiles() {
    GpxImporter gpxImporter = new GpxImporter();
    gpxImporter.init();
    List<Tuple<String, File>> files = gpxImporter
        .getFiles("D:\\Studium\\Master\\Masterarbeit\\Scheffknecht\\Data\\GPS data");

    for (Tuple<String, File> file : files) {

      if (notImportedAndFile(file.getItem2())) {
        try {
          Tracking tracking = gpxImporter.getTracking(file.getItem2(), file.getItem1());
          String trackingInfo = tracking.getTrackingInfo(Constants.FILENAME);
          int b = 4;
                    trackingRepository.add(tracking);
        } catch (Exception e) {
          e.printStackTrace();
        }


      }
    }

  }

  private void importGpsFilesFromMobyTracker() {
    GpxImporter gpxImporter = new GpxImporter();
    gpxImporter.init();
    List<Tuple<String, File>> files = gpxImporter
        .getFiles("D:\\Studium\\Master\\Masterarbeit\\Tracks\\für mobitrack gesammelte\\Kairos");
    List<Tuple<String, File>> files1 = gpxImporter
        .getFiles("D:\\Studium\\Master\\Masterarbeit\\Tracks\\für mobitrack gesammelte\\TF01\\TMD");
    files.addAll(files1);

    Tracking tracking;
    for (Tuple<String, File> file : files) {

//            if(notImportedAndFile(file.getItem2())){
      if (true) {

        try {
          tracking = gpxImporter.getTracking(file.getItem2(), file.getItem1());

          checkGpsDateTimes(tracking);

          trackingRepository.add(tracking);
        } catch (Exception e) {
          e.printStackTrace();
        }
      }
    }


  }

  private void checkGpsDateTimes(Tracking tracking) {

    DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd/MM/YYYY_HH:mm:ss");
    for (TrackingInfo trackingInfo : tracking.getTrackingInfos()) {
      if (trackingInfo.getInfoName().equals(Constants.FILENAME)) {
        String infoValue = trackingInfo.getInfoValue();
        if (infoValue.equals("24-DEZ-12 00_02_36_Rad.gpx")) {
          int b = 5;
        }

      }
    }
    Duration maxDuration = Duration.ofDays(1);

    LocalDateTime time = null;
    for (IExtendedGpsPoint gpsPoint : tracking.getGpsPoints()) {

      if (time == null) {
        time = gpsPoint.getMostAccurateTime();
      } else {

        Duration between = Duration.between(time, gpsPoint.getMostAccurateTime()).abs();

        if (between.compareTo(maxDuration) > 0) {
          throw new RuntimeException("tracking takes to many days:" + tracking.getStartTimestamp()
              .format(dateTimeFormatter) + "           " + getFileName(
              tracking.getTrackingInfos()));
        }


      }


    }


  }

  private String getFileName(List<TrackingInfo> trackingInfos) {
    for (TrackingInfo trackingInfo : trackingInfos) {
      if (trackingInfo.getInfoName().equals(Constants.FILENAME)) {
        return trackingInfo.getInfoValue();
      }
    }
    return null;
  }


  private void importMobiTrackerFiles() {

    MobiTrackerImporter mobiTrackerImporter = new MobiTrackerImporter();
    mobiTrackerImporter.init();
    List<MobiTrackerFileContainer> mobiTrackerFiles = mobiTrackerImporter
        .getMobiTrackerFiles("D:\\Studium\\Master\\Masterarbeit\\Scheffknecht\\Data\\GPS data");

    List<Tracking> trackings = new ArrayList<>();
    for (MobiTrackerFileContainer mobiTrackerFile : mobiTrackerFiles) {
      Tracking tracking = mobiTrackerImporter.importTracking(mobiTrackerFile);
      trackings.add(tracking);
      trackingRepository.add(tracking);
    }
  }


  private void importMyTracks() {
    MyTracksImporter myTracksImporter = new MyTracksImporter();
    myTracksImporter.init();
    List<File> files = myTracksImporter
        .getFiles("D:\\Studium\\Master\\Masterarbeit\\Scheffknecht\\Data\\GPS data");
    for (File file : files) {
      if (!notImportedAndFile(file)) {
        continue;
      }
      Tracking tracking = myTracksImporter.importTracking(file);
      trackingRepository.add(tracking);
    }
  }

//    private void importFromGpxFiles(){
//        File folder = new File("D:\\Studium\\Master\\Masterarbeit\\Scheffknecht\\Data\\GPS data\\");
//        GpxImporter gpxImporter = new GpxImporter(trackingRepository,trackingInfoRepository);
//        gpxImporter.init();
//        gpxImporter.importFile(folder);
//    }


  private boolean containsName(String name, List<TrackingInfo> trackingInfos) {
    for (TrackingInfo trackingInfo : trackingInfos) {
      if (trackingInfo.getInfoValue().equals(name)) {
        return true;
      }
    }
    return false;
  }

  public List<String> getFiles() {

    return null;

  }


}
