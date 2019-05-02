package sample;

import at.fhv.jn.googleMaps.DataPoint;
import at.fhv.jn.googleMaps.GoogleMapsController;
import at.fhv.jn.googleMaps.Route;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.jn.googleMaps.Thickness;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportClassifier.common.BinaryCollectionSearcher;
import at.fhv.transportClassifier.segmentsplitting.Segment;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.paint.Color;
import javafx.util.Callback;

/**
 * Created by Johannes on 09.05.2017.
 */
public class SegmentViewer {


  public ListView<Segment> segmentListView;
  public ObservableList<Segment> segmentObservableList ;
  private GoogleMapsController googleMapsController;
  private List<IGpsPoint> coordinates;
  private Route highlightedRoute = null;
  private     List<Route> routes = new ArrayList<>();
  private HashMap<Segment,Integer> indexHasmap = new HashMap<>();

  public ObservableList<Segment> getSegmentObservableList() {
    if(segmentObservableList == null) {
      segmentObservableList = FXCollections.observableArrayList();
    }
    return segmentObservableList;
  }

  public void init(GoogleMapsController googleMapsController){
     segmentListView= new ListView<>();
    this.googleMapsController = googleMapsController;
    segmentListView.setCellFactory(param -> {return new ListCellSegment();}
    );
    segmentListView.setItems(getSegmentObservableList());

    segmentListView.setMinHeight(600);
    segmentListView.setPrefHeight(2000);
    segmentObservableList.addListener(new ListChangeListener<Segment>() {
      @Override
      public void onChanged(Change<? extends Segment> c) {
        ObservableList<Segment> items = segmentListView.getItems();
        Callback<ListView<Segment>, ListCell<Segment>> cellFactory = segmentListView
            .getCellFactory();
      }
    });

    segmentListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if(highlightedRoute != null){
        highlightedRoute.setColor(Color.RED);
        highlightedRoute = null;
      }
      if(newValue != null){
        Integer integer = indexHasmap.get(newValue);
        highlightedRoute=routes.get(integer);
        highlightedRoute.setColor(Color.BLUEVIOLET);
      }

    });


  }


  public void setSegments(List<IGpsPoint> coordinates,List<Segment> segments){
    this.coordinates = coordinates;
    this.segmentObservableList.clear();
    for (Route route : routes) {
      route.dispose();
    }
    routes.clear();
    indexHasmap.clear();
    if(segments == null){
      return;
    }

    ObservableList<Segment> segmentObservableList = getSegmentObservableList();
    for (Segment segment : segments) {
      segmentObservableList.add(segment);
    }

    BinaryCollectionSearcher<IGpsPoint,LocalDateTime> binaryCollectionSearcher = new BinaryCollectionSearcher<>();
    for (Segment segment : segments) {

      int indexOfFirst = binaryCollectionSearcher.find(coordinates, segment.getStartTime(),
          (item, localDateTime) -> item.getTime().compareTo(localDateTime));
       if(indexOfFirst < 0){
         int positionToInsert = (indexOfFirst+1)*-1;
         indexOfFirst = positionToInsert;
       }

      int indexOfLast = binaryCollectionSearcher.find(coordinates, segment.getEndTime(),
          (item, localDateTime) -> item.getTime().compareTo(localDateTime));
      if(indexOfLast < 0){
        int positionToInsert = (indexOfLast+1)*-1;
        indexOfLast = positionToInsert;

      }
      if(indexOfLast>=coordinates.size()){
        indexOfLast = coordinates.size()-1;
      }

      if(indexOfLast-indexOfFirst <= 0){
        // no datapoints to show;
        continue;
      }

      List<DataPoint> dataPoints = new ArrayList<>();
      for(int i = indexOfFirst;i <= indexOfLast;i++){
        IGpsPoint coordinate = coordinates.get(i);
        dataPoints.add(new SimpleDataPoint(coordinate.getLatitude(),coordinate.getLongitude()));
      }
      Route route = googleMapsController.createRoute(dataPoints);
      route.setColor(Color.PINK);
      route.setThickness(Thickness.Normal);
      int indexOfNewRoute = routes.size();
      indexHasmap.put(segment,indexOfNewRoute);
      routes.add(route);
    }


  }


  public void hide() {
    for (Route route : routes) {
      route.hide();
    }

  }


  public void show(){
    for (Route route : routes) {
      route.show();
    }
  }
}
