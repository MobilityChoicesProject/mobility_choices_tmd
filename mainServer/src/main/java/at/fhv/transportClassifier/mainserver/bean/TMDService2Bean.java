package at.fhv.transportClassifier.mainserver.bean;

import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.featureCalculation.RailwayClosestDistanceCalculator;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.postProcessing.PostprocessingService;
import at.fhv.tmd.postProcessing.tasks.*;
import at.fhv.tmd.processFlow.TmdResult;
import at.fhv.tmd.processFlow.TmdService;
import at.fhv.tmd.processFlow.TrackingContextProvider;
import at.fhv.tmd.segmentClassification.classifier.ClassifierException;
import at.fhv.tmd.segmentClassification.classifier.ClassifierNew;
import at.fhv.tmd.segmentClassification.classifier.ClassifierParameters;
import at.fhv.transportClassifier.common.transaction.NoTransaction;
import at.fhv.transportClassifier.mainserver.api.ConfigServerLocal;
import at.fhv.transportClassifier.mainserver.api.TmdServiceLocal;
import at.fhv.transportClassifier.mainserver.impl.GisFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.LocationFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.RailwayClosestDistanceCalculatorImp;
import at.fhv.transportClassifier.mainserver.impl.SpeedCalculatorService;
import at.fhv.transportClassifier.segmentsplitting.SegmentationServiceImp;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Johannes on 12.08.2017.
 */

@Stateless
public class TMDService2Bean implements TmdServiceLocal {


  public static AtomicInteger id = new AtomicInteger(0);

  private TmdService ramdomForestTmdService;
  private GisFeatureCalculator gisFeatureCalculator;
  private RailwayClosestDistanceCalculator gisPointClosesnessCalculator;

  @EJB
  private ConfigServerLocal configService;


  @PersistenceContext(unitName = "persistence_context_mysql")
  private EntityManager em;



  @PostConstruct
  private void init(){

    FeatureCalculationService featureCalculationService = new FeatureCalculationService();
    gisFeatureCalculator = new GisFeatureCalculator();
    featureCalculationService = new FeatureCalculationService();
    featureCalculationService.addFeatureCalculator(new SpeedCalculatorService());
    featureCalculationService.addFeatureCalculator(new LocationFeatureCalculator());
    featureCalculationService.addFeatureCalculator(gisFeatureCalculator);

    ClassifierNew classifier1  = new ClassifierNew();

    try {
      classifier1.init(ClassifierParameters.WITHOUT_SEGMENTATION());
    } catch (ClassifierException e) {
     throw new RuntimeException("classifier initialization failed");
    }
    gisPointClosesnessCalculator = new RailwayClosestDistanceCalculatorImp();
    PostprocessingService postprocessingService = new PostprocessingService();
    List<PostprocessTask> postprocessTasks = new ArrayList<>();
    postprocessTasks.add(new OtherEvaluation());
    postprocessTasks.add(new RemoveNotClassifiedYetSegments());
    postprocessTasks.add(new TrainStationMovingSignalShortage(gisPointClosesnessCalculator));
    postprocessTasks.add(new MovingSignalShortage(classifier1,featureCalculationService));
    postprocessTasks.add(new MergeSameSequentielSegments(classifier1,featureCalculationService));
    postprocessTasks.add(new OtherBetweenSameTransportModes(classifier1,featureCalculationService));
    postprocessTasks.add(new ThreeVehiclesInARow(classifier1,featureCalculationService));
    postprocessTasks.add(new TooShortVehicles(classifier1,featureCalculationService));

    postprocessTasks.add(new MergeWihtoutClassification());
    postprocessTasks.add(new VehicleCrossoverEvaluation(configService));
    postprocessTasks.add(new ActivityEvaluation(configService));
    postprocessTasks.add(new EvaluateNonMotorized());
// HealingAlgorithm and FindNonVehicleBetweenVehicle is disabled because they degrade the endresult
//    postprocessTasks.add(new HealingAlgorithm());
//    postprocessTasks.add(new FindNonVehicleBetweenVehicle(configService, classifier1,featureCalculationService));
    postprocessingService.setTasks(postprocessTasks);
    SegmentationServiceImp segmentationServiceImp = new SegmentationServiceImp();

    ramdomForestTmdService = new TmdService(segmentationServiceImp,
        featureCalculationService, classifier1, postprocessingService);
    ramdomForestTmdService.init();

  }

  private void updatePersistenceContext(){
    gisFeatureCalculator.init(em,new NoTransaction());
    gisPointClosesnessCalculator.updateEntityManager(em);

    ramdomForestTmdService.updateConfigService(configService);
  }


  @Override
  public TmdResult process(ArrayList<IGpsPoint> gpsPointList) {

    updatePersistenceContext();
    TrackingContextProvider trackingContextProvider = new TrackingContextProvider() {
      @Override
      public TrackingContext getTrackingContext() {

        TrackingContext trackingContext = new TrackingContext();
        trackingContext.setTrackingId(id.get());
        trackingContext.setTrackingStartTime(gpsPointList.get(0).getTime());
        trackingContext.setTrackingEndTime(gpsPointList.get(gpsPointList.size()-1).getTime());
        trackingContext.addData(TrackingContext.RAW_GPS_INPUT,gpsPointList );
        return trackingContext;
      }
    };

    TmdResult tmdResult = ramdomForestTmdService.process(trackingContextProvider);

    return tmdResult;
  }
}
