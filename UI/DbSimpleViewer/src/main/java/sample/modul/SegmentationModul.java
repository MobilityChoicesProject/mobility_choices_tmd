package sample.modul;

import at.fhv.jn.googleMaps.Marker;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.segmentClassification.util.Helper;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.segmentsplitting.Segment;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportClassifier.segmentsplitting.SegmentationService;
import at.fhv.transportClassifier.segmentsplitting.SegmentationServiceImp;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.xychart.ChartController;
import at.fhv.xychart.SimpleObserver;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import sample.GpsPointUiWrapper;
import sample.Route.GpsListViewUi;
import sample.RouteSelectorAndSaver;
import sample.SegmentViewer;

/**
 * Created by Johannes on 19.04.2017.
 */
public class SegmentationModul implements Modul{

  private ObservableList<GpsPointUiWrapper> gpsPointItems =  FXCollections.observableArrayList();

  private VBox node;
  private CheckBox showRouteCheckBox;
  private Marker marker;
  private ChartController chartController;
  private ModulContext context;
  private SimpleObserver<Double> chartControllerPositionChangedEventHandler;
  private GpsListViewUi gpsListViewUi;

  private RouteSelectorAndSaver routeSelectorAndSaver = new RouteSelectorAndSaver();
  private SegmentViewer segmentViewer = new SegmentViewer();
  private ConfigService configService;


  @Override
  public void init(ModulContext context) {
    this.context = context;
    gpsListViewUi = new GpsListViewUi(context.getGoogleMapsController());
    segmentViewer.init(context.getGoogleMapsController());


    Button createSegmentsButton = new Button("Create Segments");
    createSegmentsButton.setOnAction(event -> {
        createSegment();
    });

    routeSelectorAndSaver.hasSelectedRouteProperty.addListener((observable, oldValue, newValue) -> {

      createSegmentsButton.setDisable(!newValue);
    });


    showRouteCheckBox = new CheckBox("Show Route");
    showRouteCheckBox.setOnAction(event -> {
      showRouteCheckboxValueChanged(showRouteCheckBox.isSelected());
    });

    Node routeSelector = routeSelectorAndSaver.getNode(context);

    HBox hBox = new HBox();
    hBox.getChildren().add(segmentViewer.segmentListView);

    node = new VBox();
    node.getChildren().add(0,createSegmentsButton);
    node.getChildren().add(1,routeSelector);
    node.getChildren().add(2,showRouteCheckBox);
//    node.getChildren().plus(3,gpsListViewUi.getNode());
    node.getChildren().add(3,hBox);
    node.getChildren().add(4,new Label("Test"));


  }

  private List<Marker> markers = new LinkedList<>();

  private void createSegment() {

    for (Marker marker1 : markers) {
      marker1.dispose();
    }
    markers.clear();

    Tracking selectedTracking = routeSelectorAndSaver.getSelectedTracking();

    List<IGpsPoint> gpsPoints = (List<IGpsPoint>)(List<?>) selectedTracking.getGpsPoints();

    CoordinateInterpolator coordinateInterpolator = Helper.filterAndCreateCoordinateInterpolator(selectedTracking,configService);
    SegmentationService segmentationService = new SegmentationServiceImp();

    List<Segment> segments = segmentationService
        .splitIntoSegments(coordinateInterpolator, selectedTracking.getStartTimestamp(),
            selectedTracking.getEndTimestamp());

    for (Segment segment : segments) {
      if(segment.getPreType() != SegmentPreType.NonClassifiable){

        IGpsPoint startPoint = coordinateInterpolator.getCoordinate(segment.getStartTime());

        Marker marker = context.getGoogleMapsController()
            .createMarker(new SimpleDataPoint(startPoint.getLatitude(), startPoint.getLongitude()));
        marker.setLabel(segment.getPreType().name());
        markers.add(marker);
      }

    }

    segmentViewer.setSegments(gpsPoints,segments);

  }

  private void showRouteCheckboxValueChanged(boolean show) {
      if (show) {
        segmentViewer.show();
        for (Marker marker1 : markers) {
          marker1.show();
        }
      } else {
        segmentViewer.hide();
        for (Marker marker1 : markers) {
          marker1.hide();
        }
      }



  }

  @Override
  public void trackingChanged() {

    for (Marker marker1 : markers) {
      marker1.dispose();
    }
    segmentViewer.setSegments(null,null);

    markers.clear();
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
    return "Segmentation";
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
