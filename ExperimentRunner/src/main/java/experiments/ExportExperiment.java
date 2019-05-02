package experiments;

import at.fhv.filters.SameSequelTransportModeMergeFilter;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import helper.PropertyHelper;
import helper.TrackingIdNamePairIterator;
import helper.exporter.TrackingExportException;
import helper.exporter.TrackingExportService;
import helper.exporter.TrajectoryExporterService;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import java.io.*;

/**
 * Created by Johannes on 26.08.2017.
 */
public class ExportExperiment {


  public void doit(LeightweightTrackingDao leightweightTrackingDao) throws FileNotFoundException {

    List<LeightweightTracking> all = leightweightTrackingDao.getAll();

    Tracking gpsTracking = leightweightTrackingDao.getGpsTracking(all.get(0));

    TrackingExportService trackingExporterService = new TrackingExportService();

    Set<String> set = createSet();
    Set<Long> goodTrackingIdes = new HashSet<>();

    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);
    File trainingSetFile = new File(dataFolder, Constants.trainingSet);
    File testSetFile = new File(dataFolder,Constants.testSet);
    List<TrackingIdNamePair> trainingSet = TrackingIdNamePairFileReaderHelper.load(trainingSetFile.getPath());
    List<TrackingIdNamePair> testSet = TrackingIdNamePairFileReaderHelper.load(testSetFile.getPath());

    trainingSet.addAll(testSet);

    TrackingIdNamePairIterator allGoodTrackings = new TrackingIdNamePairIterator(trainingSet,leightweightTrackingDao);
    String folderStr = PropertyHelper.getValue(Constants.trackingExportFolder);

    while (allGoodTrackings.hasNext()) {

      Tracking next = allGoodTrackings.next();
      Long id = next.getId();
      goodTrackingIdes.add(id);
      exportFile(trackingExporterService, set, folderStr, next);
    }

    folderStr = folderStr+"badTrackings/";
    File folder = new File(folderStr);
    folder.mkdirs();
    for (LeightweightTracking leightweightTracking : all) {
      int trackingId = leightweightTracking.getTrackingId();
      long trackingIdLong = trackingId;
      if (goodTrackingIdes.contains(trackingIdLong)) {

      }else{
        Tracking next = leightweightTrackingDao.getGpsTracking(trackingId);
        exportFile(trackingExporterService, set, folderStr, next);
      }
    }



  }
  SameSequelTransportModeMergeFilter modeMergeFilter = new SameSequelTransportModeMergeFilter();

  protected void exportFile(TrackingExportService trackingExporterService, Set<String> set,
      String folderStr, Tracking next) throws FileNotFoundException {
    next = modeMergeFilter.filter(next);
    String name = getName(next, set);
    File file = new File(folderStr,name);
    FileOutputStream fileOutputStream = new FileOutputStream(file.getAbsolutePath()+".gpx");
    try {
      trackingExporterService.write(next,fileOutputStream);
      System.out.println("saved: "+file.getAbsolutePath());
      set.add(file.getName());
    } catch (TrackingExportException e) {
      e.printStackTrace();
    }
  }

  private Set<String> createSet(){

    String folderStr = PropertyHelper.getValue(Constants.trackingExportFolder);
    Set<String> names= new HashSet<String>();
    final File folder = new File(folderStr);
    folder.mkdirs();
    File[] files = folder.listFiles();
    if(files== null){
      return new HashSet<>();
    }
    for (File file : files) {
      String name = file.getName();
      int fileTypeIndex = name.lastIndexOf('.');
      name = name.substring(0, fileTypeIndex);
      names.add(name);
    }
    return names;
  }


  private String getName(Tracking tracking,Set<String> names){
    String name = getTransportTypeString(tracking);
    String originalName= name;
    int counter= 0;
    while(names.contains(name)){
      name = originalName+"_"+ ++counter;
    }
    return name;
  }



  private String getTransportTypeString(Tracking tracking){
    List<TrackingSegment> segments = tracking.getLatestTrackingSegmentBag().getSegments();

    StringBuilder stringBuilder = new StringBuilder();
    for (TrackingSegment segment : segments) {
      stringBuilder.append(segment.getTransportType().name()+"_");
    }

    stringBuilder.deleteCharAt(stringBuilder.length()-1);
    return stringBuilder.toString();
  }















}
