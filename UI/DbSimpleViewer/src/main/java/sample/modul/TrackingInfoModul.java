package sample.modul;

import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.ListView;
import javafx.scene.layout.VBox;
import sample.Route.GpsListViewUi;
import sample.TrackingInfoCellController;

/**
 * Created by Johannes on 19.04.2017.
 */
public class TrackingInfoModul implements Modul {



  private ModulContext context;
  private Node node;

  private GpsListViewUi gpsListViewUi;
  private ObservableList<TrackingInfo> trackingInfos = FXCollections.observableArrayList();
  private ListView<TrackingInfo> trackingInfoListView = new ListView<>();

  @Override
  public void init(ModulContext context) {
    this.context = context;
    trackingInfoListView.setItems(trackingInfos);
    trackingInfoListView.setCellFactory(param -> new TrackingInfoCellController());

    VBox vBox = new VBox();
    vBox.getChildren().add(trackingInfoListView);
    node= vBox;

  }




  @Override
  public void trackingChanged() {

    trackingInfos.clear();

    Tracking mainTracking = context.getMainTracking();
    for (TrackingInfo trackingInfo : mainTracking.getTrackingInfos()) {
      trackingInfos.add(trackingInfo);
    }

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
    return "Tracking Infos";
  }


}