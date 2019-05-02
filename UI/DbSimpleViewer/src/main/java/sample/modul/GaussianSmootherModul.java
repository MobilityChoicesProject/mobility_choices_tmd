package sample.modul;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.tmd.smoothing.CoordinateInterpolatorFactory;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import java.util.List;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sample.GpsPointUiWrapper;
import sample.Route.GpsListViewUi;
import sample.RouteSelectorAndSaver;
import sample.StaticConfigService;
import sample.TrackingUtil;

/**
 * Created by Johannes on 19.04.2017.
 */
public class GaussianSmootherModul implements Modul {



  private VBox node;
  private CheckBox showRouteCheckBox;
  private ModulContext context;
  private     TextField smoothValue = new TextField();
  private  RouteSelectorAndSaver routeSelectorAndSaver = new RouteSelectorAndSaver();

  private GpsListViewUi gpsListViewUi;
  private ConfigService configService = StaticConfigService.getInstance();

  @Override
  public void init(ModulContext context) {
    this.context = context;
    smoothValue.setText("4");
    gpsListViewUi = new GpsListViewUi(context.getGoogleMapsController());

    showRouteCheckBox = new CheckBox("Show Route");
    showRouteCheckBox.setOnAction(event -> {
      showRouteCheckboxValueChanged(showRouteCheckBox.isSelected());
    });
    node = new VBox();
    Button smooth = new Button("Smooth");
    smooth.setDisable(true);
    routeSelectorAndSaver.hasSelectedRouteProperty.addListener((observable, oldValue, newValue) -> {

      smooth.setDisable(!newValue);
    });

    smooth.setOnAction(event -> {
      smoothList();
    });

    Node routeSelector = routeSelectorAndSaver.getNode(context);
    ListView<GpsPointUiWrapper> gpsListViewUiNode = gpsListViewUi.getNode();
    this.node.getChildren().add(0, smoothValue);
    this.node.getChildren().add(1, smooth);
    this.node.getChildren().add(2, routeSelector);
    this.node.getChildren().add(3, showRouteCheckBox);
    this.node.getChildren().add(4, gpsListViewUiNode);
  }

  private void smoothList(){

    if(gpsListViewUi.isInitialized()){
      gpsListViewUi.destroy();
    }

    Tracking tracking = routeSelectorAndSaver.getSelectedTracking();
    String text = smoothValue.getText();
    double smoothduration = Double.parseDouble(text);


    List<IExtendedGpsPoint> gpsPoints = tracking.getGpsPoints();
    List<IGpsPoint> coordinates = (List<IGpsPoint>)(List<?>) gpsPoints;
    CoordinateInterpolator coordinateInterpolator = CoordinateInterpolatorFactory
        .create(CoordinateInterpolatorFactory.Optimized,coordinates,configService);
    coordinateInterpolator.setKernelBandwidth(smoothduration);

    List<IGpsPoint> coordinates1 = coordinateInterpolator.getCoordinates();

    List<IExtendedGpsPoint> gpsPoint = (List<IExtendedGpsPoint>) (List<?>) coordinates1;

    Tracking smoothedTracking = TrackingUtil.cloneTrackingWith(tracking, gpsPoint);

    gpsListViewUi.initRoute(smoothedTracking,Color.RED,Color.BLUE);
    routeSelectorAndSaver.setTracking(smoothedTracking,"gausianSmother"+smoothduration);



  }

  private void showRouteCheckboxValueChanged(boolean show) {



    if (gpsListViewUi.isInitialized()) {
      if (show) {

        gpsListViewUi.show();

      } else {
        gpsListViewUi.hide();
      }
    }
  }

  @Override
  public void trackingChanged() {

    gpsListViewUi.destroy();

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
    return "Gausian Smother";
  }


}