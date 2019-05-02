package sample;

import at.fhv.jn.googleMaps.GoogleMapsController;
import at.fhv.jn.googleMaps.SimpleRoute;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportClassifier.dal.interfaces.TrackingRepository;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import at.fhv.xychart.AcceleratorData;
import at.fhv.xychart.SimpleChartController;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
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
import javafx.scene.layout.VBox;
import sample.Route.GoogleMapsRoute;
import sample.modul.AcceleartionFilterModul;
import sample.modul.DensityClusterPointModul;
import sample.modul.GaussianSmootherModul;
import sample.modul.GoogleMapsRouteViewerModul;
import sample.modul.Modul;
import sample.modul.ModulContext;
import sample.modul.PositionAccuracyFilterModul;
import sample.modul.SamePositionWorseAccuracyFilterModul;
import sample.modul.SegmentationModul;
import sample.modul.SpeedFilterModul;
import sample.modul.SplitterModul;
import sample.modul.TrackingInfoModul;
import sample.modul.WalkingSplitterModul;

public class MainController {

    private GoogleMapsRoute googleMapsRoute;
    private LeightweightTrackingDao leightweightTrackingDao;
    private ObservableList<LeightweightTracking> trackingItems =  FXCollections.observableArrayList();
  private ObservableList<Modul> modulList =  FXCollections.observableArrayList();


  private SplitterViewer splitterViewer = new SplitterViewer();
    private MouseUiController mouseUiController;
    private ModulContext modulContext = new ModulContext();


    private VBox modulContainer = new VBox();

    @FXML
    Button loadButton;

     @FXML
    Button loadGoodOnesId;

    @FXML
    SplitPane mainSplitPane;

    SplitPane mapsAxisSplitPane = new SplitPane();



    public ListView<LeightweightTracking> trackingListView = new ListView<>();
    public ListView<Modul> modulListView = new ListView<>();


    private GoogleMapsController googleMapsController;
    private SimpleChartController chartController;
    private Tracking currentlySelectedTracking;
    private TrackingRepository trackingRepository;

    public void injectDependencies(LeightweightTrackingDao leightweightTrackingDao,MouseUiController mouseUiController,TrackingRepository trackingRepository){
        this.leightweightTrackingDao   = leightweightTrackingDao;
        this.mouseUiController = mouseUiController;
        this.trackingRepository =trackingRepository;
    }



    protected void init() throws IOException {

        trackingListView.setCellFactory(param -> {
            return new ListCellTrackingFile();
        });


        trackingListView.setItems(trackingItems);


        modulListView.setCellFactory(param ->{ return new ModulCellController();});
        modulListView.setItems(modulList);


        loadButton.setOnAction(event -> {
            mouseUiController.setMouseType(Cursor.WAIT);
            List<LeightweightTracking> all = leightweightTrackingDao.getAll();
            all.sort((o1, o2) -> o1.getStartTimestamp().compareTo(o2.getStartTimestamp()));
            trackingItems.clear();
            trackingItems.addAll(all);
            mouseUiController.setMouseType(Cursor.DEFAULT);

        });

        loadGoodOnesId.setOnAction(event -> {
            mouseUiController.setMouseType(Cursor.WAIT);
            List<LeightweightTracking> all = leightweightTrackingDao.getAll();
            List<LeightweightTracking> goodTrackings = new ArrayList<>();

              String path="D:/Projects/Masterarbeit/DataContainer/data/validTrackingIdNamePair.json";
              List<TrackingIdNamePair> load = TrackingIdNamePairFileReaderHelper.load(path);
                for (LeightweightTracking leightweightTracking : all) {

                  for (TrackingInfo trackingInfo : leightweightTracking.getTrackingInfos()) {
                    if (trackingInfo.getInfoName().equals(Constants.FILENAME)) {
                      String infoValue = trackingInfo.getInfoValue();
                      TrackingIdNamePair trackingIdNamePair = new TrackingIdNamePair(
                          leightweightTracking.getTrackingId(), infoValue);

                      if(load.contains(trackingIdNamePair)){
                        goodTrackings.add(leightweightTracking);
                        break;
                      }

                    }

                  }
                }

                goodTrackings.sort((o1, o2) -> o1.getStartTimestamp().compareTo(o2.getStartTimestamp()));
                trackingItems.clear();
                trackingItems.addAll(goodTrackings);



            mouseUiController.setMouseType(Cursor.DEFAULT);

        });


        trackingListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            mouseUiController.setMouseType(Cursor.WAIT);
            LeightweightTracking selectedItem = newValue;
            onTrackingChanged(selectedItem);
            mouseUiController.setMouseType(Cursor.DEFAULT);
        });



        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/GoogleMapsView.fxml"));
        Node googleMapsView = loader.load();
        googleMapsController = loader.getController();


        FXMLLoader xyChartLoader = new FXMLLoader(AcceleratorData.class.getResource(
            "/xyChart.fxml"));
        Node chartView = xyChartLoader.load();
      SimpleChartController xyChartLoaderController = xyChartLoader.getController();
        xyChartLoaderController.init();


        chartController =xyChartLoaderController;

      mapsAxisSplitPane.setOrientation(Orientation.VERTICAL);
        mapsAxisSplitPane.getItems().add(googleMapsView);
        mapsAxisSplitPane.getItems().add(chartView);
        mapsAxisSplitPane.setDividerPosition(0,0.8);
        mapsAxisSplitPane.setDividerPosition(1,0.9);

        mainSplitPane.getItems().add(trackingListView);
        mainSplitPane.getItems().add(mapsAxisSplitPane);
        mainSplitPane.getItems().add(modulListView);
        mainSplitPane.getItems().add(modulContainer);

        mainSplitPane.setDividerPosition(0,0.2);
        mainSplitPane.setDividerPosition(1,0.6);
        mainSplitPane.setDividerPosition(2,0.8);
        mainSplitPane.setDividerPosition(3,1);

        splitterViewer.init(googleMapsController);



        modulContext.setGoogleMapsController(googleMapsController);
        modulContext.setChartController(chartController);



        GoogleMapsRouteViewerModul googleMapsRouteViewerModul = new GoogleMapsRouteViewerModul(trackingRepository);
        GaussianSmootherModul gaussianSmootherModul = new GaussianSmootherModul();

        modulList.add(googleMapsRouteViewerModul);
        modulList.add(gaussianSmootherModul);
        modulList.add(new SamePositionWorseAccuracyFilterModul());
        modulList.add(new SpeedFilterModul());
        modulList.add(new AcceleartionFilterModul());
        modulList.add(new PositionAccuracyFilterModul());
        modulList.add(new SplitterModul());
        modulList.add(new WalkingSplitterModul());
        modulList.add(new DensityClusterPointModul());
        modulList.add(new SegmentationModul());
        modulList.add(new TrackingInfoModul());

        for (Modul modul : modulList) {
            try{
                modul.init(modulContext);
            }catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }

        modulListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
           if(newValue != null){

               if(oldValue != null){
                   oldValue.deActivate();
               }

               Modul modul = newValue;
               Node node = modul.getNode();
               modulContainer.getChildren().clear();
               modulContainer.getChildren().add(node);

               modul.activate();
           }

        });
    }

    private  SimpleRoute kernelSmootherRoute = null;




    private void onTrackingChanged(LeightweightTracking leightweightTracking){

        if(leightweightTracking!= null){
            Tracking fullTracking = leightweightTrackingDao.getFullTracking(leightweightTracking);
            modulContext.setLeightweighTracking(leightweightTracking);
            modulContext.setMainTracking(fullTracking);
            modulContext.setListView(trackingListView);
        }

        for (Modul modul : modulList) {
            try{
                modul.trackingChanged();

            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

    }





}
