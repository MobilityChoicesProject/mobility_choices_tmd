package sample.modul;

import at.fhv.jn.googleMaps.Marker;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.transportClassifier.common.BinaryCollectionSearcher;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.segmentsplitting.WalkingSplitter;
import at.fhv.transportClassifier.segmentsplitting.WalkingSplitter.SplitType;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import sample.RouteSelectorAndSaver;

/**
 * Created by Johannes on 19.04.2017.
 */
public class WalkingSplitterModul implements Modul {



  private VBox node;
  private CheckBox showRouteCheckBox;
  private ModulContext context;
  private     TextField smoothValue = new TextField();
  private  RouteSelectorAndSaver routeSelectorAndSaver = new RouteSelectorAndSaver();
  private  List<Marker> markers = new LinkedList<>();
  private ConfigService configService;

  @Override
  public void init(ModulContext context) {
    this.context = context;
    smoothValue.setText("4");

    showRouteCheckBox = new CheckBox("Show Route");
    showRouteCheckBox.setOnAction(event -> {
      showRouteCheckboxValueChanged(showRouteCheckBox.isSelected());
    });
    node = new VBox();
    Button smooth = new Button("Split");
    smooth.setDisable(true);
    routeSelectorAndSaver.hasSelectedRouteProperty.addListener((observable, oldValue, newValue) -> {

      smooth.setDisable(!newValue);
    });

    smooth.setOnAction(event -> {
      smoothList();
    });

    Node routeSelector = routeSelectorAndSaver.getNode(context);
    this.node.getChildren().add(0, smoothValue);
    this.node.getChildren().add(1, smooth);
    this.node.getChildren().add(2, routeSelector);
    this.node.getChildren().add(3, showRouteCheckBox);
  }

  private void smoothList(){



    for (Marker marker : markers) {
      marker.dispose();
    }
    markers.clear();

    Tracking tracking = routeSelectorAndSaver.getSelectedTracking();

    WalkingSplitter speedThresholdSplitter = new WalkingSplitter(configService);
    List<Tuple<LocalDateTime, SplitType>> tuples = speedThresholdSplitter.splitDetail1(tracking);

    for (Tuple<LocalDateTime, SplitType> tuple : tuples) {
      LocalDateTime keyTime = tuple.getItem1();
      BinaryCollectionSearcher<IExtendedGpsPoint,LocalDateTime> binaryCollectionSearcher = new BinaryCollectionSearcher();
      int i = binaryCollectionSearcher
          .find(tracking.getGpsPoints(), keyTime, (item, localDateTime) -> {
            long duration = Duration.between(item.getMostAccurateTime(), keyTime).toMillis();
            if(duration == 0){
              return 0;
            }
            return (int) -(duration /Math.abs(duration));
          });
      IExtendedGpsPoint gpsPoint = tracking.getGpsPoints().get(i);
      Marker marker = context.getGoogleMapsController().createMarker(new SimpleDataPoint(gpsPoint.getLatitude(),gpsPoint.getLongitude()));
      marker.setLabel(tuple.getItem2().name());
      markers.add(marker);
    }


  }

  private void showRouteCheckboxValueChanged(boolean show) {

    for (Marker marker : markers) {
      if(show){
        marker.show();
      }else{
        marker.hide();
      }
    }


  }

  @Override
  public void trackingChanged() {

    showRouteCheckBox.setSelected(false);


  }

  @Override
  public void activate() {


  }

  @Override
  public void deActivate() {

  }

  @Override
  public Node getNode() {
    return node;
  }

  @Override
  public String getName() {
    return "Walking Splitter";
  }


}