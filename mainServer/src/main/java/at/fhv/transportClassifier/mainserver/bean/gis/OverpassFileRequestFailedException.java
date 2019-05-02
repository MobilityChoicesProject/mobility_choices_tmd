package at.fhv.transportClassifier.mainserver.bean.gis;

public class OverpassFileRequestFailedException extends Exception {

  public OverpassFileRequestFailedException() {
  }

  public OverpassFileRequestFailedException(String message) {
    super(message);
  }

  public OverpassFileRequestFailedException(String message, Throwable cause) {
    super(message, cause);
  }

  public OverpassFileRequestFailedException(Throwable cause) {
    super(cause);
  }

  public OverpassFileRequestFailedException(String message, Throwable cause,
      boolean enableSuppression, boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
