package at.fhv.tmd.featureCalculation;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.util.ArrayList;
import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 20.06.2017.
 */
public class FeatureCalculationService {

  private static Logger logger = LoggerFactory.getLogger(FeatureCalculationService.class);

  private List<FeatureCalculator> featureCalculators = new ArrayList<>();

  public void addFeatureCalculator(FeatureCalculator featureCalculator){
    featureCalculators.add(featureCalculator);
  }


  public List<FeatureResult> calcFeatures(List<IGpsPoint> coordinates ,CoordinateInterpolator coordinateInterpolator){
    FeatureContext featureContext = new FeatureContext();

    featureContext.addInput(FeatureInputConstants.coordinateInterpolator,coordinateInterpolator);
    featureContext.addInput(FeatureInputConstants.continousInterpolatedGpsList,coordinates);

    for (FeatureCalculator featureCalculator : featureCalculators) {
      try {
        featureCalculator.calcFeature(featureContext);
      } catch (FeatureCalculationException e) {
        logger.error("There was an excepion during the calculation of the feature",e);
        e.printStackTrace();
      }
    }

    return featureContext.getResults();

  }














}
