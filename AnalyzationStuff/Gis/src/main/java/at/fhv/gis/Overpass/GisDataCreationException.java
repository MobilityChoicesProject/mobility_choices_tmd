package at.fhv.gis.Overpass;

/**
 * Created by Johannes on 11.08.2017.
 */
public class GisDataCreationException extends Exception {


  public GisDataCreationException() {
  }

  public GisDataCreationException(String message) {
    super(message);
  }

  public GisDataCreationException(String message, Throwable cause) {
    super(message, cause);
  }

  public GisDataCreationException(Throwable cause) {
    super(cause);
  }

  public GisDataCreationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
