package at.fhv.tmd.segmentClassification.classifier;

/**
 * Created by Johannes on 21.06.2017.
 */
public class ArfFileException extends RuntimeException {

  public ArfFileException() {
  }

  public ArfFileException(String message) {
    super(message);
  }

  public ArfFileException(String message, Throwable cause) {
    super(message, cause);
  }

  public ArfFileException(Throwable cause) {
    super(cause);
  }

  public ArfFileException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
