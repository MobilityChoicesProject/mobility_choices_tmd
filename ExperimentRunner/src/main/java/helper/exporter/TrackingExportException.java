package helper.exporter;

public class TrackingExportException extends Exception {

  public TrackingExportException() {
  }

  public TrackingExportException(String message) {
    super(message);
  }

  public TrackingExportException(String message, Throwable cause) {
    super(message, cause);
  }

  public TrackingExportException(Throwable cause) {
    super(cause);
  }

  public TrackingExportException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
