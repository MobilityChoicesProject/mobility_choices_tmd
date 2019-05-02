package sample.modul;

import at.fhv.filters.PositionJumpSpeedFilter;
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
public class SpeedFilterModul implements Modul {


  ConfigService  configService=StaticConfigService.getInstance();


  private VBox node;
  private CheckBox showRouteCheckBox;
  private ModulContext context;
  private     TextField smoothValue = new TextField();
  private  RouteSelectorAndSaver routeSelectorAndSaver = new RouteSelectorAndSaver();


  private GpsListViewUi gpsListViewUi;

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
    Button filterbutton = new Button("Filter");
    filterbutton.setDisable(true);
    routeSelectorAndSaver.hasSelectedRouteProperty.addListener((observable, oldValue, newValue) -> {

      filterbutton.setDisable(!newValue);
    });

    filterbutton.setOnAction(event -> {
      filterList();
    });

    Node routeSelector = routeSelectorAndSaver.getNode(context);
    ListView<GpsPointUiWrapper> gpsListViewUiNode = gpsListViewUi.getNode();

    this.node.getChildren().add( filterbutton);
    this.node.getChildren().add( routeSelector);
    this.node.getChildren().add( showRouteCheckBox);
    this.node.getChildren().add( gpsListViewUiNode);
  }

  private void filterList(){

    if(gpsListViewUi.isInitialized()){
      gpsListViewUi.destroy();
    }

    Tracking tracking = routeSelectorAndSaver.getSelectedTracking();
    PositionJumpSpeedFilter filter = new PositionJumpSpeedFilter(configService);

    List<IExtendedGpsPoint> gpsPoint = filter.filterGpsPoints(tracking.getGpsPoints());
    Tracking filteredList = TrackingUtil.cloneTrackingWith(tracking, gpsPoint);
    gpsListViewUi.initRoute(filteredList,Color.AQUA,Color.BLUE);
    routeSelectorAndSaver.setTracking(filteredList,"speedFilter");

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
    return "Speed Filter";
  }


}