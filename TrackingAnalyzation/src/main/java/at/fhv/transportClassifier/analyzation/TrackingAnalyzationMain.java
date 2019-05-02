package at.fhv.transportClassifier.analyzation;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.HibernateUtil;
import at.fhv.transportClassifier.dal.daos.HibernateLeightweightAccelerationValueDao;
import at.fhv.transportClassifier.dal.daos.HibernateLeightweightTrackingDao;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateBoundingBoxRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateDatapointsRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingInfoRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentBagRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.HibernateTrackingSegmentTypeRepository;
import at.fhv.transportClassifier.dal.databaseRepositories.TrackingInfoTypeRepository;
import at.fhv.transportClassifier.dal.interfaces.LeightweightAccelerationValueDao;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.builder.SimpleAllTracking;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.hibernate.Session;

/**
 * Created by Johannes on 09.03.2017.
 */
public class TrackingAnalyzationMain {


    private Session session;
    private HibernateLeightweightTrackingDao leightweightTrackingDao;


    private static List<LeightweightTracking> onlyV4(List<LeightweightTracking> all) {

        List<LeightweightTracking> v4Trackings = new ArrayList<>();
        for (LeightweightTracking leightweightTracking : all) {
            int version = -1;
            for (TrackingInfo trackingInfo : leightweightTracking.getTrackingInfos()) {
                if (trackingInfo.getInfoName().equals(Constants.FH_GPS_LOGGER_VERSION)) {
                    String infoValue = trackingInfo.getInfoValue();
                    version = Integer.parseInt(infoValue);
                    break;
                }

            }
            if (version == 4) {
                v4Trackings.add(leightweightTracking);
            }
        }
        return v4Trackings;
    }



    public static void main(String[] args){

        TrackingAnalyzationMain trackingAnalyzationMain = new TrackingAnalyzationMain();
        trackingAnalyzationMain.initHibernate();
        List<LeightweightTracking> all = trackingAnalyzationMain.leightweightTrackingDao.getAll();

//        all = onlyV4(all);

        int i = 0;
        List<TrackingAnalyzationResult> results = new ArrayList<>();
        for (LeightweightTracking leightweightTracking : all) {

            if(!leightweightTracking.isAcceleratorDataAvailable()){
                continue;
            }
            Tracking fullTracking = trackingAnalyzationMain.leightweightTrackingDao.getFullTracking(leightweightTracking);
            Analyzator analyzator = new Analyzator();
            TrackingAnalyzationResult result = analyzator.analyze((SimpleAllTracking) fullTracking);
            results.add(result);
            i++;

        }

        BoundingBox boundingBox = new SimpleBoundingBox(46.818689,8.410677,48.442455,10.271454);

        int segments=0;
        int gpsSemgents=0;
        int acSegments=0;

        int acchanges=0;
        int gpsChanges=0;


        int goodAccChanges=0;
        int goodgpsChanges=0;
        int goodSegments=0;
        int goodGpsSemgents=0;
        int goodAcSegments=0;

        int outsideOfBoundingBox=0;

        List<TrackingAnalyzationResult> perfectResults = new ArrayList<>();
        List<Long> perfectResultsIdsOnly = new ArrayList<>();

        int v4 = 0;
        int perfectv4=0;

        int v4_noAcData =0;
        int v4_allAcData =0;

        for (TrackingAnalyzationResult result : results) {
            if(result.trackingVersion == 4 ){
                if(result.totalNoAcStateDataPercentage == 0){
                    v4_allAcData++;
                }else{
                    v4_noAcData++;

                }
            }


            int noGpsSize = result.noAccAtSegmentChange.size();
            int noAcSize = result.noGpsAtSegmentChange.size();
            if ( noGpsSize== 0) {
                acSegments++;
            } if ( noAcSize== 0) {
                gpsSemgents++;
            }

            if(noGpsSize==0 && noAcSize  ==0){
                segments++;
            }


            boolean gpsNotBefore2SekOfstart = result.durationStartAndGpsSensorStart.toMillis() > -2000;
            boolean acNotBefore2SekOfstart = result.durationStartAndAcSensorStart.toMillis() > -2000;
            boolean gpsNotLongerActiveThan5Sec = result.durationEndAndGpsSensorEnd.toMillis() < 5000;
            boolean acNotLongerActiveThan5Sec = result.durationEndAndAcSensorEnd.toMillis() < 5000;
            boolean isInBoudningBox = boundingBox.contains(result.boundingbox);
            if(!isInBoudningBox){
                outsideOfBoundingBox++;
            }

            if(result.trackingVersion == 4 && isInBoudningBox){
                v4++;
                perfectResults.add(result);
            }


            if(gpsNotBefore2SekOfstart && acNotBefore2SekOfstart && gpsNotLongerActiveThan5Sec && acNotLongerActiveThan5Sec&&isInBoudningBox){
                if ( noGpsSize== 0) {
                    goodAcSegments++;
                } if ( noAcSize== 0) {
                    goodGpsSemgents++;
                }

                if(noGpsSize==0 && noAcSize  ==0){
                    goodSegments++;
                    perfectResults.add(result);
                    perfectResultsIdsOnly.add(result.startimestamp);

                    if(result.trackingVersion == 4){
                        perfectv4++;
                    }

                }

                goodAccChanges +=result.acSegmentChanges.size();
                goodgpsChanges +=result.gpsSegmentChanges.size();
            }

            acchanges +=result.acSegmentChanges.size();
            gpsChanges +=result.gpsSegmentChanges.size();


            List<TrackingAnalyzationResult> goodOnes = new ArrayList<>();
            List<TrackingAnalyzationResult> badOnes = new ArrayList<>();
            String phoneType = "Nexus 4";
            for (TrackingAnalyzationResult trackingAnalyzationResult : results) {

                if(trackingAnalyzationResult.phoneType.equals(phoneType) ){
                    if(trackingAnalyzationResult.totalNoAcStateDataPercentage  == 0){
                        goodOnes.add(trackingAnalyzationResult);
                    }else{
                        badOnes.add(trackingAnalyzationResult);

                    }
                }

            }
            int size = badOnes.size();
        }



        GsonBuilder builder = new GsonBuilder();
        Gson gson = builder.setPrettyPrinting().create();
        String perfectResultsString = gson.toJson(perfectResults);
        String perfectResultsIdsString = gson.toJson(perfectResultsIdsOnly);

        try {
            Files.write(Paths.get("D:\\Projects\\Masterarbeit\\DataContainer\\analyzation\\perfectResults"),perfectResultsString.getBytes());
            Files.write(Paths.get("D:\\Projects\\Masterarbeit\\DataContainer\\analyzation\\perfectResultsIds"),perfectResultsIdsString.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }


    }

    private void initHibernate(){

        org.hibernate.SessionFactory sessionFactory = HibernateUtil.getSessionFactory();
        session = sessionFactory.openSession();
        HibernateSessionMananger hibernateSessionMananger = new HibernateSessionMananger(session);
        LeightweightAccelerationValueDao leightweightAccelerationValueDao = new HibernateLeightweightAccelerationValueDao(hibernateSessionMananger);

        TrackingInfoTypeRepository trackingInfoTypeRepository = new TrackingInfoTypeRepository(hibernateSessionMananger);
        HibernateTrackingInfoRepository trackingInfoRepository = new HibernateTrackingInfoRepository(hibernateSessionMananger,trackingInfoTypeRepository);
        HibernateDatapointsRepository hibernateDatapointsRepository = new HibernateDatapointsRepository(hibernateSessionMananger);
        HibernateTrackingSegmentTypeRepository hibernateTrackingSegmentTypeRepository = new HibernateTrackingSegmentTypeRepository(hibernateSessionMananger);
        HibernateBoundingBoxRepository hibernateBoundingBoxRepository = new HibernateBoundingBoxRepository(hibernateSessionMananger);
        HibernateTrackingSegmentRepository hibernateTrackingSegmentRepository = new HibernateTrackingSegmentRepository(hibernateSessionMananger,hibernateBoundingBoxRepository,hibernateTrackingSegmentTypeRepository);
        HibernateTrackingSegmentBagRepository hibernateTrackingSegmentBagRepository = new HibernateTrackingSegmentBagRepository(hibernateSessionMananger,hibernateTrackingSegmentRepository);
        HibernateTrackingRepository hibernateTrackingRepository = new HibernateTrackingRepository(hibernateSessionMananger,trackingInfoRepository,hibernateTrackingSegmentBagRepository,hibernateBoundingBoxRepository,hibernateDatapointsRepository);

        leightweightTrackingDao = new HibernateLeightweightTrackingDao(hibernateSessionMananger,leightweightAccelerationValueDao,hibernateTrackingRepository);

    }






}
