package at.fhv.tmd.featureCalculation;

/**
 * Created by Johannes on 20.06.2017.
 */
public interface FeatureCalculator {

  void calcFeature(FeatureContext featureContext) throws FeatureCalculationException;
}
