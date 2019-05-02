package sample;

import at.fhv.jn.googleMaps.GoogleMapsController;
import at.fhv.jn.googleMaps.Marker;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.jn.googleMaps.SimpleRoute;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.AccelerationTracking;
import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.builder.SimpleAllTracking;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import at.fhv.xychart.AcceleratorData;
import at.fhv.xychart.DataPointRecords;
import at.fhv.xychart.SimpleAcceleratorData;
import at.fhv.xychart.SimpleChartController;
import at.fhv.xychart.SimpleObserver;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Orientation;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.paint.Color;
import sample.Route.GoogleMapsRoute;

public class Controller {

    private GoogleMapsRoute googleMapsRoute;
    private LeightweightTrackingDao leightweightTrackingDao;
    private ObservableList<LeightweightTracking> trackingItems =  FXCollections.observableArrayList();
  private ObservableList<GpsPointUiWrapper> gpsPointItems =  FXCollections.observableArrayList();
    private SplitterViewer splitterViewer = new SplitterViewer();
    private MouseUiController mouseUiController;

    @FXML
    Button loadButton;

     @FXML
    Button loadGoodOnesId;

    @FXML
    Button acSplittingButtonId;

    @FXML
    Button gausianSmoothButton;

    @FXML
    SplitPane mainSplitPane;

    SplitPane mapsAxisSplitPane = new SplitPane();



    public ListView<LeightweightTracking> trackingListView = new ListView<>();
    public ListView<GpsPointUiWrapper> gpsPointListView = new ListView<>();
    private GoogleMapsController googleMapsController;
    private SimpleChartController chartController;
    private Tracking currentlySelectedTracking;

    public void injectDependencies(LeightweightTrackingDao leightweightTrackingDao,MouseUiController mouseUiController){
        this.leightweightTrackingDao   = leightweightTrackingDao;
        this.mouseUiController = mouseUiController;
    }



    protected void init() throws IOException {

        trackingListView.setCellFactory(param -> {
            return new ListCellTrackingFile();
        });

        gpsPointListView.setCellFactory(param -> {
            return new GpsPointListCell();
        });
        gpsPointListView.setItems(gpsPointItems);
        trackingListView.setItems(trackingItems);

        loadButton.setOnAction(event -> {
            mouseUiController.setMouseType(Cursor.WAIT);
            List<LeightweightTracking> all = leightweightTrackingDao.getAll();
            trackingItems.clear();
            trackingItems.addAll(all);
            mouseUiController.setMouseType(Cursor.DEFAULT);

        });

        loadGoodOnesId.setOnAction(event -> {
            mouseUiController.setMouseType(Cursor.WAIT);
            List<LeightweightTracking> all = leightweightTrackingDao.getAll();

            GsonBuilder gsonBuilder = new GsonBuilder();
            Gson gson = gsonBuilder.setPrettyPrinting().create();
            try {
                String content = new String(Files.readAllBytes(Paths.get("D:\\Projects\\Masterarbeit\\DataContainer\\analyzation\\perfectResultsIds")));
                Class<Long[]> aClass1 = Long[].class;
//                Class<? extends List> aClass = list.getClass();
                Long[] longs = gson.fromJson(content,aClass1);
                ArrayList<Long> longs1 = new ArrayList<Long>();
                for(int i = 0; i< longs.length;i++){
                    longs1.add(longs[i]);
                }

                List<LeightweightTracking> trackingsToShow = new ArrayList<LeightweightTracking>();
                for (LeightweightTracking leightweightTracking : all) {
                    long timestmap = leightweightTracking.getStartTimestamp()
                        .toInstant(ZoneOffset.ofTotalSeconds(0)).toEpochMilli();
                    if (longs1.contains(timestmap)) {

                        trackingsToShow.add(leightweightTracking);
                    }
                }
                trackingItems.clear();
                trackingItems.addAll(trackingsToShow);
            } catch (IOException e) {
                e.printStackTrace();
            }
            mouseUiController.setMouseType(Cursor.DEFAULT);





        });

        trackingListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mouseUiController.setMouseType(Cursor.WAIT);
            LeightweightTracking selectedItem = newValue;
            changeTrackingList(selectedItem);
            mouseUiController.setMouseType(Cursor.DEFAULT);
        });

        gpsPointListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            TrackingSegment trackingSegment= null;
            if(newValue != null){
                trackingSegment = newValue.getTrackingSegment();
                showMarker(newValue);

            }else{
                showMarker(null);
            }
            googleMapsRoute.highlightOnlyTrackingSegment(trackingSegment);



        });


        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/src/main/resources/GoogleMapsView.fxml"));
        Node googleMapsView = loader.load();
        googleMapsController = loader.getController();


        FXMLLoader xyChartLoader = new FXMLLoader(AcceleratorData.class.getResource(
            "/src/main/resources/sample.fxml"));
        Node chartView = xyChartLoader.load();
        SimpleChartController xyChartLoaderController = xyChartLoader.getController();
        xyChartLoaderController.init();
        chartController =xyChartLoaderController;

        chartController.addPositionClickObserver(new SimpleObserver<Double>() {
            @Override
            public void onUpdate(Observable o, Double arg) {
                LocalDateTime startTime = gpsPointItems.get(0).getGpsPoint().getSensorTime();
                int i = 0;
                long seconds = 0;
                for (GpsPointUiWrapper gpsPointItem : gpsPointItems) {
                    LocalDateTime currentTime = gpsPointItem.getGpsPoint().getSensorTime();
                    Duration between = Duration.between(startTime, currentTime);
                     seconds= between.toMillis() / 1000;
                    if(seconds > arg){
                        jumpToGpsPoint(i);
                        return;
                    }
                    i++;
                }
                jumpToGpsPoint(i-1);

                long seconds1 = seconds;


            }
        });

        mapsAxisSplitPane.setOrientation(Orientation.VERTICAL);
        mapsAxisSplitPane.getItems().add(googleMapsView);
        mapsAxisSplitPane.getItems().add(chartView);
        mapsAxisSplitPane.setDividerPosition(0,0.8);
        mapsAxisSplitPane.setDividerPosition(1,0.9);

        mainSplitPane.getItems().add(trackingListView);
        mainSplitPane.getItems().add(gpsPointListView);
        mainSplitPane.getItems().add(mapsAxisSplitPane);
        mainSplitPane.setDividerPosition(0,0.2);
        mainSplitPane.setDividerPosition(1,0.5);
        mainSplitPane.setDividerPosition(2,1);

        splitterViewer.init(googleMapsController);
        acSplittingButtonId.setOnAction(event -> {
            if(currentlySelectedTracking instanceof AccelerationTracking){
                    SimpleAllTracking fullTracking = (SimpleAllTracking) this.currentlySelectedTracking;
                splitterViewer.show(fullTracking);

            }


        });

        gausianSmoothButton.setOnAction(event -> {

            if(kernelSmootherRoute== null){
            }else{
                kernelSmootherRoute.dispose();
                kernelSmootherRoute= null;
            }



        });

    }

    private  SimpleRoute kernelSmootherRoute = null;

    Marker marker;
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
                marker = googleMapsController.createMarker(simpleDataPoint);
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


    private void jumpToGpsPoint(int index){
        GpsPointUiWrapper gpsPointUiWrapper = gpsPointItems.get(index);
        gpsPointListView.scrollTo(gpsPointUiWrapper);
        gpsPointListView.getSelectionModel().select(gpsPointUiWrapper);
        gpsPointListView.getFocusModel().focus(index);
    }

    private void changeTrackingList(LeightweightTracking selectedItem) {

        splitterViewer.show(null);


        currentlySelectedTracking= null;
        gpsPointItems.clear();
        if(googleMapsRoute != null){
            googleMapsRoute.dispose();
            googleMapsRoute =null;
        }
        if(selectedItem!= null){
            Tracking fullTracking = leightweightTrackingDao.getFullTracking(selectedItem);
            TrackingSegmentBag trackingSegmentBag = fullTracking.getTrackingSegmentBags().get(0);

//            List<GpsPointUiWrapper> gpsPointUiWrappers = GpsPointUiWrapper.generateSmoothedList(trackingSegmentBag,fullTracking.getStartTimestamp());
            List<GpsPointUiWrapper> gpsPointUiWrappers = GpsPointUiWrapper.generateList(trackingSegmentBag,fullTracking.getStartTimestamp());
            gpsPointItems.addAll(gpsPointUiWrappers);

            googleMapsRoute = new GoogleMapsRoute();
            googleMapsRoute.init(googleMapsController, trackingSegmentBag,Color.RED,Color.PURPLE);


            currentlySelectedTracking = fullTracking;
            if(selectedItem.isAcceleratorDataAvailable()){

                LocalDateTime startTimestamp = fullTracking.getStartTimestamp();

                AccelerationTracking accelerationTracking = (AccelerationTracking) fullTracking;

                LocalDateTime gpsStart = accelerationTracking.getGpsPoints().get(0).getSensorTime();
                LocalDateTime accStart = accelerationTracking.getAcceleratorStates().get(0).getTime();
                LocalDateTime gpsLast = accelerationTracking.getGpsPoints().get(accelerationTracking.getGpsPoints().size() - 1).getSensorTime();
                LocalDateTime acLast = accelerationTracking.getAcceleratorStates().get(accelerationTracking.getAcceleratorStates().size() - 1).getTime();
                Duration gps = Duration.between(gpsLast,gpsStart);
                Duration ac = Duration.between(acLast,accStart);
                double gpsMillis = gps.toMillis();
                double acMillis = ac.toMillis();

                double factor = gpsMillis/acMillis;
                factor = 1;

                DataPointRecords x = new DataPointRecordsImp(accelerationTracking.getAcceleratorStates(),factor,startTimestamp){
                    @Override
                    public double getAxis(AcceleratorState accelerationTracking) {
                        return accelerationTracking.getXAcceleration();
                    }
                };
                DataPointRecords y = new DataPointRecordsImp(accelerationTracking.getAcceleratorStates(),factor,startTimestamp){
                    @Override
                    public double getAxis(AcceleratorState accelerationTracking) {
                        return accelerationTracking.getYAcceleration();
                    }
                };
                DataPointRecords z = new DataPointRecordsImp(accelerationTracking.getAcceleratorStates(),factor,startTimestamp){
                    @Override
                    public double getAxis(AcceleratorState accelerationTracking) {
                        return accelerationTracking.getZAcceleration();
                    }
                };
                AcceleratorData acceleratorData = new SimpleAcceleratorData(x,y,z);


                chartController.setAccelerationData(acceleratorData);

            }


        }


    }


}
