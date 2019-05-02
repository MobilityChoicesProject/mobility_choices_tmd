package sample;

import at.fhv.jn.googleMaps.GoogleMapsController;
import at.fhv.jn.googleMaps.Marker;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.transportClassifier.common.BinaryCollectionSearcher;
import at.fhv.transportClassifier.segmentsplitting.SegmentSplitter;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.builder.SimpleAllTracking;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 26.03.2017.
 */
public class SplitterViewer {

  BinaryCollectionSearcher<IExtendedGpsPoint,LocalDateTime> binaryCollectionSearcher = new BinaryCollectionSearcher<>();

  private SegmentSplitter segmentSplitter = new SegmentSplitter();
  private GoogleMapsController googleMapsController;
  private List<Marker>  markers = new ArrayList<>();

  public void init(GoogleMapsController controller){
    this.googleMapsController = controller;
  }


  public void show(SimpleAllTracking tracking){

    for (Marker marker : markers) {
      marker.dispose();
    }

    markers.clear();

    if(tracking == null){
      return;
    }

    List<LocalDateTime> localDateTimes = segmentSplitter
        .splitGpsSpeedThreshold(tracking.getGpsPoints(),new Speed(11.0));



    for (LocalDateTime localDateTime : localDateTimes) {


      int index= binaryCollectionSearcher.find(tracking.getGpsPoints(),localDateTime,(item, dateTime) -> {
        return (int) Math.signum(Duration.between(dateTime,item.getSensorTime()).toMillis()) ;
      });

      if(index <0){
        index = -index+1;
      }
      if(index>= tracking.getGpsPoints().size()){
        continue;
      }
      IExtendedGpsPoint calculatedPoint = tracking.getGpsPoints().get(index);
      Marker marker = googleMapsController.createMarker(
          new SimpleDataPoint(calculatedPoint.getLatitude(), calculatedPoint.getLongitude()));
      marker.show();
      marker.setLabel("A");
      markers.add(marker);

    }




  }





}
