package at.fhv.transportClassifier.mainserver.bean;

import at.fhv.gis.entities.db.GisPoint;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.common.transaction.NoTransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import at.fhv.transportClassifier.mainserver.impl.GisFeatureCalculator;
import at.fhv.transportClassifier.mainserver.impl.SpeedCalculatorService;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.util.ArrayList;
import java.util.List;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

/**
 * Created by Johannes on 13.06.2017.
 */
@Stateless
//@TransactionManagement(value= TransactionManagementType.BEAN)
public class FeatureCalculationServiceBean implements
    at.fhv.transportClassifier.mainserver.api.FeatureCalculationServiceLocal {


  private FeatureCalculationService featureCalculationService = new FeatureCalculationService();

  private  GisFeatureCalculator gisFeatureCalculator = new GisFeatureCalculator();


  @PersistenceContext(unitName = "persistence_context_mysql")
  private EntityManager em;


  @Override
  public List<FeatureResult> calculateFeature(List<IGpsPoint> interpolatedCoordinatesExact,
      CoordinateInterpolator coordinateInterpolator)
      throws TransactionException {
    List<FeatureResult> results = new ArrayList<>();
    try {

      GisPoint gisPoint = em.find(GisPoint.class, 1l);
      long area_id = gisPoint.getArea_id();
      ++area_id;
//    ITransaction transaction = new NoTransaction(userTransaction);
    ITransaction transaction = new NoTransaction();
    gisFeatureCalculator.init(em, transaction);
    featureCalculationService.addFeatureCalculator(new SpeedCalculatorService());
    featureCalculationService.addFeatureCalculator(gisFeatureCalculator);

    results = featureCalculationService
        .calcFeatures(interpolatedCoordinatesExact, coordinateInterpolator);

    transaction.commit();
  }catch (TransactionException ex){
    ex.printStackTrace();
  }

    return results;
  }
}
