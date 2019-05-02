package at.fhv.gis;

/**
 * Created by Johannes on 26.05.2017.
 */
public class OverpassDataException extends Exception {

  public OverpassDataException() {
  }

  public OverpassDataException(String message) {
    super(message);
  }

  public OverpassDataException(String message, Throwable cause) {
    super(message, cause);
  }

  public OverpassDataException(Throwable cause) {
    super(cause);
  }

  public OverpassDataException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
