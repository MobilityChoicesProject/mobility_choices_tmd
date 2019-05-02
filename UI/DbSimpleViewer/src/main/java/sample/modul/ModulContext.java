package sample.modul;

import at.fhv.jn.googleMaps.GoogleMapsController;
import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import at.fhv.xychart.ChartController;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

/**
 * Created by Johannes on 19.04.2017.
 */
public class ModulContext {

  private Tracking mainTracking;
  private GoogleMapsController googleMapsController;
  private ChartController chartController;

  private ObservableList<Tuple<String,Tracking>> trackingList =  FXCollections.observableArrayList();
  private LeightweightTracking leightweighTracking;
  private ListView<LeightweightTracking> listView;


  public GoogleMapsController getGoogleMapsController() {
    return googleMapsController;
  }

  public ChartController getChartController() {
    return chartController;
  }

  public void setChartController(ChartController chartController) {
    this.chartController = chartController;
  }

  public void setGoogleMapsController(GoogleMapsController googleMapsController) {
    this.googleMapsController = googleMapsController;
  }

  public Tracking getMainTracking() {
    return mainTracking;
  }

  public void setMainTracking(Tracking mainTracking) {
    this.mainTracking = mainTracking;
    trackingList.clear();
    trackingList.add(new Tuple<>("main",mainTracking));
  }

  public void addTracking(String name, Tracking tracking){
    trackingList.add(new Tuple<>(name,tracking));
  }

  public Tuple<String,Tracking> getLatestTracking(){
    return trackingList.get(trackingList.size()-1);
  }

  public ObservableList<Tuple<String, Tracking>> getTrackingList() {
    return trackingList;
  }

  public void setLeightweighTracking(LeightweightTracking leightweighTracking) {
    this.leightweighTracking = leightweighTracking;
  }

  public LeightweightTracking getLeightweighTracking() {
    return leightweighTracking;
  }

  public void setListView(ListView<LeightweightTracking> listView) {
    this.listView = listView;
  }

  public void refreshListView(){
    listView.refresh();
  }
}
