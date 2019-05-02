package at.fhv.tmd.featureCalculation;

/**
 * Created by Johannes on 20.06.2017.
 */
public class FeatureCalculationException extends Exception {

  public FeatureCalculationException() {
  }

  public FeatureCalculationException(String message) {
    super(message);
  }

  public FeatureCalculationException(String message, Throwable cause) {
    super(message, cause);
  }

  public FeatureCalculationException(Throwable cause) {
    super(cause);
  }

  public FeatureCalculationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
