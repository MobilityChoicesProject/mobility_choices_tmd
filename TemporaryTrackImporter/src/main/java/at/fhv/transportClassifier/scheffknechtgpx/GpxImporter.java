package at.fhv.transportClassifier.scheffknechtgpx;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportClassifier.GpxImporterBase;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleGpsPoint;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import org.xml.sax.SAXException;

/**
 * Created by Johannes on 16.02.2017.
 */
public class GpxImporter  extends GpxImporterBase{


//    protected TrackingRepository hibernateTrackingRepository;
//    protected TrackingInfoRepository hibernateTrackingInfoRepository;

    public HashSet<String> savedTrackingNames = new HashSet<>();

//    public GpxImporter(TrackingRepository hibernateTrackingRepository, TrackingInfoRepository hibernateTrackingInfoRepository) {
//        this.hibernateTrackingRepository = hibernateTrackingRepository;
//        this.hibernateTrackingInfoRepository = hibernateTrackingInfoRepository;
//    }

    public  List<Tuple<String,File>> getFiles(String folderStr){
        File folder = new File(folderStr);
        File[] files = folder.listFiles();

        List<Tuple<String,File>>  returnFiles = new ArrayList<>();
        for (File file : files) {
            String name = file.getName();

            if(name.startsWith("mytracks")){
                returnFiles.add(new Tuple<>(Constants.ORIGIN_SCHEFFKNECHT_MYTRACKS,file));
            }
            else if(name.startsWith("GPX")){
                returnFiles.add(new Tuple<>(Constants.ORIGIN_SCHEFFKNECHT_GPX,file));
            }
            else if(name.contains("_rc")){
                returnFiles.add(new Tuple<>(Constants.ORIGIN_MobiTracker,file));
            }else if(name.endsWith(".gpx")) {
                returnFiles.add(new Tuple<>(Constants.ORIGIN_MobilityChoices,file));
            }else{
                    System.out.print("invalid file: "+name );
            }

        }
        return returnFiles;

    }




    public Tracking getTracking(File file, String origin) throws Exception {

        SimpleTrackingBuilder simpleTrackingBuilder =new SimpleTrackingBuilder();
        XmlHandler parse = null;
        try {

            parse = parse(file);
            List<SimpleGpsPoint> gpsPoints = parse.getGpsPoints();
            TransportType transportType = parse.getTransportType();
            LocalDateTime startTime = parse.getStartTime();
            LocalDateTime endTime = parse.getEndTime();


            for (SimpleGpsPoint gpsPoint : gpsPoints) {
                if(gpsPoint.getAccuracy()==null){
                    simpleTrackingBuilder.addGpsPoint(gpsPoint.getLatitude(),gpsPoint.getLongitude(),null,-1.0,null,gpsPoint.getSensorTime(),null);
                }else{
                    simpleTrackingBuilder.addGpsPoint(gpsPoint.getLatitude(),gpsPoint.getLongitude(),null,null,null,gpsPoint.getSensorTime(),null);

                }
            }
            if(transportType == null){
                transportType = TransportType.OTHER;
            }
            simpleTrackingBuilder.addTrackingSegment(startTime,endTime,transportType,0);
            simpleTrackingBuilder.addTrackingInfo(Constants.FILENAME,file.getName());
            simpleTrackingBuilder.addTrackingInfo(Constants.ORIGIN,origin);

            simpleTrackingBuilder.setStartTimestamp(startTime);
            simpleTrackingBuilder.setEndTimestamp(endTime);

            return simpleTrackingBuilder.build();


        } catch (Exception e) {
            throw new Exception("Error during import",e);
         }
    }

//    public void importFile(File folder){
//
//
//        Spezification<TrackingInfo> spezification = hibernateTrackingInfoRepository.getSpezification(TrackingInfoFilenameSpezification.class.getName());
//        List<TrackingInfo> query = hibernateTrackingInfoRepository.query(spezification);
//        for (TrackingInfo trackingInfo : query) {
//            savedTrackingNames.plus(trackingInfo.getInfoValue());
//        }
//
//        File[] files = folder.listFiles();
//
//        List<File> notSavedFiles = removeAlreadySavedFiles(files);
//        List<File> myTracks = new ArrayList<>();
//        List<File> tracks = new ArrayList<>();
//        List<File> segments = new ArrayList<>();
//        for (File file : notSavedFiles) {
//            char firstChar = file.getName().toCharArray()[0];
//            if(Character.isDigit(firstChar)){
//                segments.plus(file);
//            }else if(firstChar == 'G'){
//                tracks.plus(file);
//            }else if(firstChar == 'm'){
//                myTracks.plus(file);
//            }
//        }
//
//        Map<String,List<File>> map = new TreeMap<>();
//
//        for (File segment : segments) {
//            String name = segment.getName();
//            int firstUnderscore = name.indexOf('_');
//            int secondUnderscore = name.indexOf('_',firstUnderscore+1);
//            String restName = name.substring(secondUnderscore,name.length()-1);
//            List<File> files1;
//            if(map.containsKey(restName)){
//                 files1 = map.get(restName);
//            }else{
//                files1 = new ArrayList<>();
//                map.put(restName,files1);
//            }
//            files1.plus(segment);
//
//        }
//        Collection<List<File>> values = map.values();
//
//        List<Tracking> newTrackings = new ArrayList<>();
//        for (List<File> value : values) {
//            try {
//                Tracking tracking = createTrackingFromSegments(value);
//                hibernateTrackingRepository.plus(tracking);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (SAXException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (File myTrack : myTracks) {
//            SimpleTrackingBuilder trackingBuilder = new SimpleTrackingBuilder();
//            try {
//                parseFile(myTrack, trackingBuilder);
//                trackingBuilder.addTrackingInfo(Constants.FILENAME,myTrack.getName());
//                trackingBuilder.addTrackingInfo(Constants.ORIGIN,Constants.ORIGIN_SCHEFFKNECHT_MYTRACKS);
//                Tracking build = trackingBuilder.build();
//                hibernateTrackingRepository.plus(build);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (SAXException e) {
//                e.printStackTrace();
//            }
//        }
//
//        for (File track : tracks) {
//            SimpleTrackingBuilder trackingBuilder = new SimpleTrackingBuilder();
//            try {
//                parseFile(track, trackingBuilder);
//
//                trackingBuilder.addTrackingInfo(Constants.FILENAME,track.getName());
//                trackingBuilder.addTrackingInfo(Constants.ORIGIN,Constants.ORIGIN_SCHEFFKNECHT_GPX);
//                Tracking build = trackingBuilder.build();
//                hibernateTrackingRepository.plus(build);
//            } catch (IOException e) {
//                e.printStackTrace();
//            } catch (SAXException e) {
//                e.printStackTrace();
//            }
//        }
//    }

    private void parseFile(File track, SimpleTrackingBuilder trackingBuilder) throws IOException, SAXException {
        XmlHandler parse = parse(track);
        trackingBuilder.setStartTimestamp(parse.getStartTime());
        trackingBuilder.addTrackingSegment(parse.getStartTime(),parse.getEndTime(),parse.getTransportType(),0);
        for (SimpleGpsPoint simpleGpsPoint : parse.getGpsPoints()) {
//            trackingBuilder.addGpsPoint(simpleGpsPoint.getLatitude(),simpleGpsPoint.getLongitude(),simpleGpsPoint.getSensorTime());
        }
    }


    private List<File> removeAlreadySavedFiles(File[] files) {
        List<File> newFiles = new ArrayList<>();
        for(int i = 0; i < files.length;i++){
            File file = files[i];
            if (savedTrackingNames.contains(file.getName())) {
            }else{
                newFiles.add(file);
            }
        }
        return newFiles;


    }

    private Tracking createTrackingFromSegments(List<File> value) throws IOException, SAXException {

        value.sort((o1, o2) -> {
            char c1 = o1.getName().toCharArray()[0];
            char c2 = o2.getName().toCharArray()[0];
            return c1-c2;

        });

        SimpleTrackingBuilder  trackingBuilder = new SimpleTrackingBuilder();
        boolean startTimeSet = false;
        for (File file : value) {
            XmlHandler parse = parse(file);
            if(!startTimeSet){
                trackingBuilder.setStartTimestamp(parse.getStartTime());
                startTimeSet=true;
            }
            LocalDateTime startTime = parse.getStartTime();
            LocalDateTime endTime = parse.getEndTime();
            List<SimpleGpsPoint> gpsPoints = parse.getGpsPoints();
            TransportType transportType = parse.getTransportType();
            trackingBuilder.addTrackingSegment(startTime,endTime,transportType,0);
            for (SimpleGpsPoint gpsPoint : gpsPoints) {
//                trackingBuilder.addGpsPoint(gpsPoint.getLatitude(),gpsPoint.getLongitude(),gpsPoint.getSensorTime());
            }
            trackingBuilder.addTrackingInfo(Constants.FILENAME,file.getName());
        }
        trackingBuilder.addTrackingInfo(Constants.ORIGIN,Constants.SCHEFFKNECHT_SEGMENT);
        return trackingBuilder.build();
    }




}
