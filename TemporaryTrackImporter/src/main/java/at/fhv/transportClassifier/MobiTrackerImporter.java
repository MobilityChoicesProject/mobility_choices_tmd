package at.fhv.transportClassifier;

import at.fhv.transportClassifier.scheffknechtgpx.XmlHandler;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleGpsPoint;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.SAXException;

/**
 * Created by Johannes on 19.05.2017.
 */
public class MobiTrackerImporter extends GpxImporterBase{



  public List<MobiTrackerFileContainer> getMobiTrackerFiles(String folderStr){
    File folder= new File(folderStr);
    File[] files = folder.listFiles();

    List<MobiTrackerFileContainer> mobiTrackerFiles = new ArrayList<>();

    for (File file : files) {

      if (!MobiTrackerFileContainer.isMobiTrackerFile(file)) {
        continue;
      }

      // wont work, since there are segments, which are not after the one before.
//      boolean foundContainer = false;
//      for (MobiTrackerFileContainer mobiTrackerFile : mobiTrackerFiles) {
//        if (mobiTrackerFile.isSameTracking(file)) {
//          foundContainer = true;
//          mobiTrackerFile.addFile(file);
//        }
//      }

//      if(!foundContainer){
        MobiTrackerFileContainer mobiTrackerFileContainer = new MobiTrackerFileContainer(file);
        mobiTrackerFiles.add(mobiTrackerFileContainer);
//      }
    }
    return mobiTrackerFiles;
  }


  public Tracking importTracking(MobiTrackerFileContainer mobiTrackerFileContainer){
    List<File> allFiles = mobiTrackerFileContainer.getAllFiles();
    SimpleTrackingBuilder simpleTrackingBuilder = new SimpleTrackingBuilder();

    LocalDateTime trackingStartTime = null;
    LocalDateTime trackingEndtime = null;

    try {
    for (File file : allFiles) {
      XmlHandler parse = parse(file);

      List<SimpleGpsPoint> gpsPoints = parse.getGpsPoints();
      TransportType transportType = parse.getTransportType();
      LocalDateTime startTime = parse.getStartTime();
      LocalDateTime endTime = parse.getEndTime();

      if(trackingStartTime == null){
        trackingStartTime =startTime;
      }
      trackingEndtime = endTime;

      for (SimpleGpsPoint gpsPoint : gpsPoints) {
        simpleTrackingBuilder.addGpsPoint(gpsPoint.getLatitude(),gpsPoint.getLongitude(),null,null,null,gpsPoint.getSensorTime(),null);
      }
      simpleTrackingBuilder.addTrackingSegment(startTime,endTime,transportType,0);
      simpleTrackingBuilder.addTrackingInfo(Constants.FILENAME,file.getName());

    }

    simpleTrackingBuilder.addTrackingInfo(Constants.ORIGIN,Constants.ORIGIN_MobiTracker);

    simpleTrackingBuilder.setStartTimestamp(trackingStartTime);
    simpleTrackingBuilder.setEndTimestamp(trackingEndtime);

    } catch (IOException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }

     return simpleTrackingBuilder.build();
  }






}
