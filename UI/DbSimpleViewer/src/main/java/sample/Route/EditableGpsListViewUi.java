package sample.Route;

import at.fhv.jn.googleMaps.GoogleMapsController;
import at.fhv.jn.googleMaps.Marker;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.transportClassifier.dal.interfaces.TrackingRepository;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.NotAvailableExcepion;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingInfo;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingSegment;
import at.fhv.transportdetector.trackingtypes.builder.SimpleTrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javafx.beans.value.ChangeListener;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import sample.EditabeleGpsPointListCell;
import sample.GpsPointUiWrapper;
import sample.modul.ModulContext;

/**
 * Created by Johannes on 19.04.2017.
 */
public class EditableGpsListViewUi {



  public ObservableList<GpsPointUiWrapper> gpsPointItems =  FXCollections.observableArrayList();
  private ListView<GpsPointUiWrapper> gpsPointListView = new ListView<>();
  private Marker marker;
  private GoogleMapsController googleMapsController;
  private ChangeListener<GpsPointUiWrapper> gpsListViewSelectedPropertyChangedEventHandler;
  private GoogleMapsRoute googleMapsRoute;
  private VBox box = new VBox();
  private TrackingRepository trackingRepository;
  private DateTimeFormatter simpleDateFormat =  DateTimeFormatter.ofPattern("dd.MM.yyyy _ HH:mm:ss");
  private boolean isInitialized = false;
  private Tracking tracking;
  private ModulContext context ;

  public boolean isInitialized() {
    return isInitialized;
  }


  public EditableGpsListViewUi(GoogleMapsController  googleMapsController,TrackingRepository trackingRepository){
    this.googleMapsController = googleMapsController;
    gpsPointListView.setCellFactory(param -> {
      return new EditabeleGpsPointListCell();
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
      if(googleMapsRoute!= null){
        googleMapsRoute.highlightOnlyTrackingSegment(trackingSegment);
      }
    };

    gpsPointListView.getSelectionModel().selectedItemProperty().addListener(gpsListViewSelectedPropertyChangedEventHandler);

    HBox topBox = new HBox();
    Button currentVersionPreviousButton = new Button("Prev.");
    currentVersionPreviousButton.setOnAction(event -> {
      int selectedIndex = gpsPointListView.getSelectionModel().getSelectedIndex();
      GpsPointUiWrapper selectedItem = gpsPointListView.getSelectionModel().getSelectedItem();
      if(selectedItem==null){
        int lastIndex = gpsPointItems.size() - 1;
        selectedItem = gpsPointItems.get(lastIndex);
        selectedIndex=lastIndex;
      }
      TransportType transportType = selectedItem.getTransportType();

      for(;selectedIndex>=0 ;selectedIndex--){
        GpsPointUiWrapper gpsPointUiWrapper = gpsPointItems.get(selectedIndex);
        if (!gpsPointUiWrapper.getTransportType().equals(transportType)) {
          gpsPointListView.scrollTo(selectedIndex);
          gpsPointListView.getSelectionModel().select(selectedIndex);
          break;
        }
      }
    });
    Button currentVersionNextButton = new Button("Next.");
    currentVersionNextButton.setOnAction(event -> {
      int selectedIndex = gpsPointListView.getSelectionModel().getSelectedIndex();
      GpsPointUiWrapper selectedItem = gpsPointListView.getSelectionModel().getSelectedItem();
      if(selectedItem==null){
        int lastIndex = 0;
        selectedItem = gpsPointItems.get(lastIndex);
        selectedIndex=lastIndex;
      }
      TransportType transportType = selectedItem.getTransportType();
      int size = gpsPointItems.size();
      for(;selectedIndex<size ;selectedIndex++){
        GpsPointUiWrapper gpsPointUiWrapper = gpsPointItems.get(selectedIndex);
        if (!gpsPointUiWrapper.getTransportType().equals(transportType)) {
          gpsPointListView.scrollTo(selectedIndex);
          gpsPointListView.getSelectionModel().select(selectedIndex);
          break;
        }
      }
    });

    Button button = new Button("Save Version 1.");
    button.setOnAction(event -> {

      List<TrackingSegmentBag> trackingSegmentBags = this.tracking.getTrackingSegmentBags();
      TrackingSegmentBag version1= null;
      for (TrackingSegmentBag trackingSegmentBag : trackingSegmentBags) {
        if(trackingSegmentBag.getVersion()== 1){
          version1 = trackingSegmentBag;
          break;
        }
      }
      if(version1 != null){
        trackingSegmentBags.remove(version1);
      }

//      Iterator<TrackingInfo> iterator = tracking.getTrackingInfos().iterator();
//      while (iterator.hasNext()) {
//        TrackingInfo next = iterator.next();
//          if (next.getInfoName().equals("manuallyEdited")) {
//            iterator.remove();
//          }
//        }


      List<TrackingSegment> trackingSegments = getTrackingSegments();

      SimpleTrackingSegmentBag trackingSegmentBag  = new SimpleTrackingSegmentBag();
      trackingSegmentBags.add(trackingSegmentBag);
      trackingSegmentBag.setVersion(1);
      trackingSegmentBag.addSegments(trackingSegments);
      SimpleTrackingInfo manuallyEdited = new SimpleTrackingInfo("manuallyEdited",
          LocalDateTime.now().format(simpleDateFormat));
      this.tracking.getTrackingInfos().add(manuallyEdited);

      trackingRepository.update(this.tracking);
      this.context.getLeightweighTracking().getTrackingInfos().add(manuallyEdited);
      this.context.refreshListView();

    });



    Button prevButton = new Button("Prev.");
    prevButton.setOnAction(event -> {
      int selectedIndex = gpsPointListView.getSelectionModel().getSelectedIndex();
      GpsPointUiWrapper selectedItem = gpsPointListView.getSelectionModel().getSelectedItem();
      if(selectedItem==null){
        int lastIndex = gpsPointItems.size() - 1;
        selectedItem = gpsPointItems.get(lastIndex);
        selectedIndex=lastIndex;
      }
      String newTransportType = selectedItem.getNewTransportType();
      for(;selectedIndex>=0 ;selectedIndex--){
        GpsPointUiWrapper gpsPointUiWrapper = gpsPointItems.get(selectedIndex);
        String newTransportType1 = gpsPointUiWrapper.getNewTransportType();

        if (newTransportType1 != null && newTransportType1.length()>1 && !newTransportType1.equals(newTransportType)) {
          gpsPointListView.scrollTo(selectedIndex);
          gpsPointListView.getSelectionModel().select(selectedIndex);
          break;
        }
      }
    });

    Button nextButton = new Button("Next.");
    nextButton.setOnAction(event -> {
      int selectedIndex = gpsPointListView.getSelectionModel().getSelectedIndex();
      GpsPointUiWrapper selectedItem = gpsPointListView.getSelectionModel().getSelectedItem();
      if(selectedItem==null){
        int lastIndex = 0;
        selectedItem = gpsPointItems.get(lastIndex);
        selectedIndex=lastIndex;
      }
      String newTransportType = selectedItem.getNewTransportType();
      int size = gpsPointItems.size();
      for(;selectedIndex<size ;selectedIndex++) {
        GpsPointUiWrapper gpsPointUiWrapper = gpsPointItems.get(selectedIndex);
        String newTransportType1 = gpsPointUiWrapper.getNewTransportType();
        if (newTransportType1 != null && newTransportType1.length()>1 && !newTransportType1.equals(newTransportType)) {

          gpsPointListView.scrollTo(selectedIndex);
          gpsPointListView.getSelectionModel().select(selectedIndex);
          break;
        }
      }

    });

    Button markAsEdited = new Button("Mark as Edited");

    markAsEdited.setOnAction(event -> {

      SimpleTrackingInfo manuallyEdited = new SimpleTrackingInfo(Constants.ManualyEdited,LocalDateTime.now().format(simpleDateFormat));
      tracking.getTrackingInfos().add(manuallyEdited);
      trackingRepository.update(tracking);
      this.context.getLeightweighTracking().getTrackingInfos().add(manuallyEdited);
      this.context.refreshListView();
    });



    topBox.getChildren().add(currentVersionPreviousButton);
    topBox.getChildren().add(currentVersionNextButton);
    topBox.getChildren().add(button);
    topBox.getChildren().add(prevButton);
    topBox.getChildren().add(nextButton);
    topBox.getChildren().add(markAsEdited);
    box.getChildren().add(topBox);
    box.getChildren().add(gpsPointListView);
  }

  private List<TrackingSegment> getTrackingSegments(){

    List<TrackingSegment> trackingSegments = new ArrayList<>();

    LocalDateTime firstTime;
    TransportType firstTransportType;
    GpsPointUiWrapper lastPoint = null;
    Iterator<GpsPointUiWrapper> iterator = gpsPointItems.iterator();
    GpsPointUiWrapper next = iterator.next();
    if(next.getNewTransportType()==null || next.getNewTransportType().equals("")){
      firstTransportType = TransportType.OTHER;
    }else{
      String newTransportType = next.getNewTransportType();
      firstTransportType=Enum.valueOf(TransportType.class,newTransportType);
    }

    firstTime = next.getGpsPoint().getMostAccurateTime();

    while (iterator.hasNext()) {
      next = iterator.next();
      String newTransportType = next.getNewTransportType();
      if(newTransportType != null  && !newTransportType.equals("") && !newTransportType.equals(firstTransportType)){

        SimpleTrackingSegment trackingSegment = new SimpleTrackingSegment();
        trackingSegment.setStartTime(firstTime);
//        trackingSegment.setEndTime(lastPoint.getGpsPoint().getMostAccurateTime());
        trackingSegment.setEndTime(next.getGpsPoint().getMostAccurateTime());
        trackingSegment.setTransportType(firstTransportType);
        trackingSegment.setAllGpsPoints(tracking.getGpsPoints());
        trackingSegments.add(trackingSegment);

        firstTime = next.getGpsPoint().getMostAccurateTime();
        firstTransportType=Enum.valueOf(TransportType.class,newTransportType);

      }
      lastPoint= next;
    }

    SimpleTrackingSegment trackingSegment = new SimpleTrackingSegment();
    trackingSegment.setStartTime(firstTime);
    trackingSegment.setEndTime(lastPoint.getGpsPoint().getMostAccurateTime());
    trackingSegment.setTransportType(firstTransportType);
    trackingSegment.setAllGpsPoints(tracking.getGpsPoints());
    trackingSegments.add(trackingSegment);


  return trackingSegments;
  }


  public Node getNode(){
    return  box;
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

  public void initRoute(Tracking tracking, Color normalColor, Color hightlightColor,
      ModulContext context){
    if(googleMapsRoute != null){
      destroy();
    }

    this.context = context;
    TrackingSegmentBag trackingSegmentBagZero = tracking.getTrackingSegmentBagWithVersion(0);

    this.tracking = tracking;

    googleMapsRoute = new GoogleMapsRoute();
    try{
      googleMapsRoute.init(googleMapsController, trackingSegmentBagZero, normalColor,hightlightColor);

    }catch (Error ex){
      ex.printStackTrace();
    }

    List<GpsPointUiWrapper> gpsPointUiWrappers = GpsPointUiWrapper.generateList(trackingSegmentBagZero,
        tracking.getStartTimestamp());

    try{
      Iterator<GpsPointUiWrapper> iterator = gpsPointUiWrappers.iterator();

      TrackingSegmentBag trackingSegmentBagWithVersionOne = tracking.getTrackingSegmentBagWithVersion(1);
      for (TrackingSegment trackingSegment : trackingSegmentBagWithVersionOne.getSegments()) {
        LocalDateTime startTime = trackingSegment.getStartTime();
        while (iterator.hasNext()){
          GpsPointUiWrapper current = iterator.next();
          if(current.getGpsPoint().getMostAccurateTime().equals(startTime)){
            current.setNewTransportType(trackingSegment.getTransportType().name());
            break;
          }
        }
      }

    }catch (NotAvailableExcepion notAvailableExcepion){

    }


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
