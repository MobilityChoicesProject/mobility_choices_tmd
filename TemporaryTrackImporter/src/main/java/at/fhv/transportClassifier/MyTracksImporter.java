package at.fhv.transportClassifier;

import at.fhv.transportClassifier.scheffknechtgpx.XmlHandler;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleGpsPoint;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingBuilder;
import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 19.05.2017.
 */
public class MyTracksImporter extends GpxImporterBase {



  public  List<File> getFiles(String folderStr){
    File folder = new File(folderStr);
    File[] files = folder.listFiles();
    List<File> myTracks = new ArrayList<>();
    for (File file : files) {
      String name = file.getName();
      if(name.startsWith("myTracks")|| name.startsWith("GPX")){
        myTracks.add(file);
      }
    }

    return myTracks;
  }


  public Tracking importTracking(File file) {

    SimpleTrackingBuilder simpleTrackingBuilder = new SimpleTrackingBuilder();

    LocalDateTime trackingStartTime = null;
    LocalDateTime trackingEndtime = null;

    try {
      XmlHandler parse = parse(file);

      List<SimpleGpsPoint> gpsPoints = parse.getGpsPoints();
      TransportType transportType = parse.getTransportType();
      LocalDateTime startTime = parse.getStartTime();
      LocalDateTime endTime = parse.getEndTime();

      if (trackingStartTime == null) {
        trackingStartTime = startTime;
      }
      trackingEndtime = endTime;

      for (SimpleGpsPoint gpsPoint : gpsPoints) {
        simpleTrackingBuilder
            .addGpsPoint(gpsPoint.getLatitude(), gpsPoint.getLongitude(), null, null, null,
                gpsPoint.getSensorTime(), null);

        simpleTrackingBuilder.addTrackingSegment(startTime, endTime, transportType, 0);
        simpleTrackingBuilder.addTrackingInfo(Constants.FILENAME, file.getName());

      }
      simpleTrackingBuilder.addTrackingInfo(Constants.ORIGIN,Constants.ORIGIN_SCHEFFKNECHT_MYTRACKS);
      simpleTrackingBuilder.setStartTimestamp(trackingStartTime);
      simpleTrackingBuilder.setEndTimestamp(trackingEndtime);

    } catch (Exception ex) {
      throw new RuntimeException("",ex);
    }

    return simpleTrackingBuilder.build();
  }

}
