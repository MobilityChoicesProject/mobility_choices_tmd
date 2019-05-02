package at.fhv.transportClassifier.mainserver.bean.gis;

public class GisPersistenceException extends Exception {

  public GisPersistenceException() {
  }

  public GisPersistenceException(String message) {
    super(message);
  }

  public GisPersistenceException(String message, Throwable cause) {
    super(message, cause);
  }

  public GisPersistenceException(Throwable cause) {
    super(cause);
  }

  public GisPersistenceException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
