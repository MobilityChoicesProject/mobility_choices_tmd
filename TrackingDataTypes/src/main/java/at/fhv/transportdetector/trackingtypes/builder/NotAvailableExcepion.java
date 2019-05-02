package at.fhv.transportdetector.trackingtypes.builder;

/**
 * Created by Johannes on 12.06.2017.
 */
public class NotAvailableExcepion extends RuntimeException {

  public NotAvailableExcepion() {
  }

  public NotAvailableExcepion(String message) {
    super(message);
  }

  public NotAvailableExcepion(String message, Throwable cause) {
    super(message, cause);
  }

  public NotAvailableExcepion(Throwable cause) {
    super(cause);
  }

  public NotAvailableExcepion(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
