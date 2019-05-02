package at.fhv.transportClassifier.common.configSettings;

/**
 * Created by Johannes on 10.08.2017.
 */
public class ConfigurationExcepction extends RuntimeException {


  public ConfigurationExcepction() {
  }

  public ConfigurationExcepction(String message) {
    super(message);
  }

  public ConfigurationExcepction(String message, Throwable cause) {
    super(message, cause);
  }

  public ConfigurationExcepction(Throwable cause) {
    super(cause);
  }

  public ConfigurationExcepction(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
