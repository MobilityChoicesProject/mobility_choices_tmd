package sample.modul;

import at.fhv.jn.googleMaps.Marker;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.transportClassifier.dal.interfaces.TrackingRepository;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.xychart.ChartController;
import at.fhv.xychart.SimpleObserver;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sample.GpsPointUiWrapper;
import sample.Route.EditableGpsListViewUi;

/**
 * Created by Johannes on 19.04.2017.
 */
public class GoogleMapsRouteViewerModul implements Modul{

  private ObservableList<GpsPointUiWrapper> gpsPointItems =  FXCollections.observableArrayList();

  private VBox node;
  private CheckBox showRouteCheckBox;
  private Marker marker;
  private ChartController chartController;
  private ModulContext context;
  private SimpleObserver<Double> chartControllerPositionChangedEventHandler;
  private EditableGpsListViewUi gpsListViewUi;
  private TrackingRepository trackingRepository;

  public GoogleMapsRouteViewerModul(TrackingRepository trackingRepository){
    this.trackingRepository = trackingRepository;
  }

  @Override
  public void init(ModulContext context) {
    this.context = context;
    chartController = context.getChartController();
    gpsListViewUi = new EditableGpsListViewUi(context.getGoogleMapsController(),trackingRepository);


    showRouteCheckBox = new CheckBox("Show Route");
    showRouteCheckBox.setOnAction(event -> {
        showRouteCheckboxValueChanged(showRouteCheckBox.isSelected());
    });


    node = new VBox();
    node.getChildren().add(0,showRouteCheckBox);
    node.getChildren().add(1,gpsListViewUi.getNode());


    chartControllerPositionChangedEventHandler = new SimpleObserver<Double>() {
      @Override
      public void onUpdate(Observable o, Double arg) {
        LocalDateTime startTime = gpsPointItems.get(0).getGpsPoint().getSensorTime();
        int i = 0;
        long seconds = 0;
        for (GpsPointUiWrapper gpsPointItem : gpsPointItems) {
          LocalDateTime currentTime = gpsPointItem.getGpsPoint().getSensorTime();
          Duration between = Duration.between(startTime, currentTime);
          seconds = between.toMillis() / 1000;
          if (seconds > arg) {
            gpsListViewUi.jumpToGpsPoint(i);
            return;
          }
          i++;
        }
        gpsListViewUi.jumpToGpsPoint(i - 1);

        long seconds1 = seconds;


      }
    };





  }

  private void showRouteCheckboxValueChanged(boolean show) {
      if (show) {
        gpsListViewUi.show();
      } else {
        gpsListViewUi.hide();
      }



  }

  @Override
  public void trackingChanged() {
    Tracking mainTracking = context.getMainTracking();

    gpsListViewUi.initRoute(mainTracking,Color.RED,Color.PURPLE,context);
    gpsListViewUi.zoomTo();
    showRouteCheckBox.setSelected(true);


  }

  @Override
  public void activate() {
    chartController.addPositionClickObserver(chartControllerPositionChangedEventHandler);
  }

  @Override
  public void deActivate() {
    chartController.deletePositionClickObserver(chartControllerPositionChangedEventHandler);
  }

  @Override
  public Node getNode() {
    return node;
  }

  @Override
  public String getName() {
    return "Simple Route View";
  }



  private void showMarker(GpsPointUiWrapper gpsPointUiWrapper) {
    IExtendedGpsPoint gpsPoint = gpsPointUiWrapper.getGpsPoint();

    if(gpsPoint == null){
      if(marker!= null){
        marker.dispose();
        marker= null;
      }
    }else{
      SimpleDataPoint simpleDataPoint = new SimpleDataPoint(gpsPoint.getLatitude(), gpsPoint.getLongitude());
      if(marker == null){
        marker =  context.getGoogleMapsController().createMarker(simpleDataPoint);
      }
      marker.setPosition(simpleDataPoint);

    }


    if(gpsPoint != null){
      LocalDateTime time = gpsPoint.getSensorTime();
      LocalDateTime deviceSavingSystemTime = gpsPoint.getDeviceSavingSystemTime();

      long millisSinceStart = gpsPointUiWrapper.durationSinceStartAndDeviceSystemTime().toMillis();
      chartController.setPosition(millisSinceStart/1000);
    }



  }



}
