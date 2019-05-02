package at.fhv.gis.Overpass;

/**
 * Created by Johannes on 20.05.2017.
 */
public class GisDataException extends Exception {

  public GisDataException() {
  }

  public GisDataException(String message) {
    super(message);
  }

  public GisDataException(String message, Throwable cause) {
    super(message, cause);
  }

  public GisDataException(Throwable cause) {
    super(cause);
  }

  public GisDataException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
