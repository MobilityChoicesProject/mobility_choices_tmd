package sample.Route;

import at.fhv.jn.googleMaps.GoogleMapsController;
import at.fhv.jn.googleMaps.Marker;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import sample.GpsPointListCell;
import sample.GpsPointUiWrapper;

/**
 * Created by Johannes on 19.04.2017.
 */
public class GpsListViewUi {


  public ObservableList<GpsPointUiWrapper> gpsPointItems =  FXCollections.observableArrayList();
  private ListView<GpsPointUiWrapper> gpsPointListView = new ListView<>();
  private Marker marker;
  private GoogleMapsController googleMapsController;
  private ChangeListener<GpsPointUiWrapper> gpsListViewSelectedPropertyChangedEventHandler;
  private GoogleMapsRoute googleMapsRoute;

  private boolean isInitialized = false;

  public boolean isInitialized() {
    return isInitialized;
  }


  public GpsListViewUi( GoogleMapsController googleMapsController){
    this.googleMapsController = googleMapsController;
    gpsPointListView.setCellFactory(param -> {
      return new GpsPointListCell();
    });

    gpsPointListView.setItems(gpsPointItems);
    gpsPointListView.setMinHeight(300);
    gpsPointListView.setPrefHeight(2000);

    gpsListViewSelectedPropertyChangedEventHandler = (observable, oldValue, newValue) -> {
      TrackingSegment trackingSegment = null;
      if (newValue != null) {
        trackingSegment = newValue.getTrackingSegment();
        showMarker(newValue);

      } else {
        showMarker(null);
      }
      googleMapsRoute.highlightOnlyTrackingSegment(trackingSegment);
    };

    gpsPointListView.getSelectionModel().selectedItemProperty().addListener(gpsListViewSelectedPropertyChangedEventHandler);

  }


  public ListView<GpsPointUiWrapper> getNode(){
    return  gpsPointListView;
  }

  public void showMarker(GpsPointUiWrapper gpsPointUiWrapper) {

    if (gpsPointUiWrapper == null) {
      if (marker != null) {
        marker.dispose();
        marker = null;
      }
    } else {
      IExtendedGpsPoint gpsPoint = gpsPointUiWrapper.getGpsPoint();

      SimpleDataPoint simpleDataPoint = new SimpleDataPoint(gpsPoint.getLatitude(),
          gpsPoint.getLongitude());
      if (marker == null) {
        marker = googleMapsController.createMarker(simpleDataPoint);
      }
      marker.setPosition(simpleDataPoint);

    }


  }

  public void initRoute(Tracking tracking, Color normalColor,Color hightlightColor){
    if(googleMapsRoute != null){
      destroy();
    }
    TrackingSegmentBag latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();
    googleMapsRoute = new GoogleMapsRoute();
    googleMapsRoute.init(googleMapsController, latestTrackingSegmentBag, normalColor,hightlightColor);

    List<GpsPointUiWrapper> gpsPointUiWrappers = GpsPointUiWrapper.generateList(latestTrackingSegmentBag,tracking.getStartTimestamp());
    gpsPointItems.addAll(gpsPointUiWrappers);

    isInitialized = true;
  }

  public void zoomTo(){
    googleMapsRoute.zoomTo();
  }

  public void destroy(){
    if(googleMapsRoute != null){
      googleMapsRoute.dispose();
      googleMapsRoute = null;
    }

    if(marker != null){
      marker.dispose();
      marker = null;
    }
    gpsPointItems.clear();
    isInitialized = false;
  }

  public void show() {
    if(marker != null)
    marker.show();
    googleMapsRoute.show();
  }

  public void hide() {
    if(marker != null)
      marker.hide();
    googleMapsRoute.hide();
  }

  public void jumpToGpsPoint(int index){
    GpsPointUiWrapper gpsPointUiWrapper = gpsPointItems.get(index);
    gpsPointListView.scrollTo(gpsPointUiWrapper);
    gpsPointListView.getSelectionModel().select(gpsPointUiWrapper);
    gpsPointListView.getFocusModel().focus(index);
  }

}
