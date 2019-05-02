package sample.modul;

import at.fhv.jn.googleMaps.Marker;
import at.fhv.transportdetector.trackingtypes.Tracking;
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
public class DensityClusterPointModul implements Modul {



  private VBox node;
  private CheckBox showRouteCheckBox;
  private ModulContext context;
  private     TextField smoothValue = new TextField();
  private  RouteSelectorAndSaver routeSelectorAndSaver = new RouteSelectorAndSaver();
  private  List<Marker> markers = new LinkedList<>();

  @Override
  public void init(ModulContext context) {
    this.context = context;
    smoothValue.setText("4");

    showRouteCheckBox = new CheckBox("Show Route");
    showRouteCheckBox.setOnAction(event -> {
      showRouteCheckboxValueChanged(showRouteCheckBox.isSelected());
    });
    node = new VBox();
    Button button = new Button("Find Cluster");
    button.setDisable(true);
    routeSelectorAndSaver.hasSelectedRouteProperty.addListener((observable, oldValue, newValue) -> {

      button.setDisable(!newValue);
    });

    button.setOnAction(event -> {
      smoothList();
    });

    Node routeSelector = routeSelectorAndSaver.getNode(context);
    this.node.getChildren().add(0, smoothValue);
    this.node.getChildren().add(1, button);
    this.node.getChildren().add(2, routeSelector);
    this.node.getChildren().add(3, showRouteCheckBox);
  }

  private void smoothList(){

    for (Marker marker : markers) {
      marker.dispose();
    }
    markers.clear();

    Tracking tracking = routeSelectorAndSaver.getSelectedTracking();

//    DensityClusterFinder finder = new DensityClusterFinder();
//    List<DensityCluster> densityClusters = finder.find(tracking.getGpsPoints());
//    for (DensityCluster densityCluster : densityClusters) {
//      GpsPoint startPoint = densityCluster.getStartPoint();
//      Marker marker = context.getGoogleMapsController().createMarker(new SimpleDataPoint(startPoint.getLatitude(),startPoint.getLongitude()));
//      marker.setLabel("Start");
//
//      GpsPoint endPoint = densityCluster.getEndPoint();
//      Marker marker1 = context.getGoogleMapsController().createMarker(new SimpleDataPoint(endPoint.getLatitude(),endPoint.getLongitude()));
//      marker1.setLabel("End");
//
//      markers.plus(marker);
//      markers.plus(marker1);
//
//
//    }
//




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
    return "DensityCluster";
  }


}