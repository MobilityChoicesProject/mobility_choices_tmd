package at.fhv.transportClassifier.mainserver.security;

/**
 * Created by Johannes on 11.08.2017.
 */
public class AuthenticationException extends  Exception {

  public AuthenticationException() {
  }

  public AuthenticationException(String message) {
    super(message);
  }

  public AuthenticationException(String message, Throwable cause) {
    super(message, cause);
  }

  public AuthenticationException(Throwable cause) {
    super(cause);
  }

  public AuthenticationException(String message, Throwable cause, boolean enableSuppression,
      boolean writableStackTrace) {
    super(message, cause, enableSuppression, writableStackTrace);
  }
}
