package at.fhv.tmddemoservice.user;

/**
 * Created by Johannes on 09.08.2017.
 */
public class TokenCheckResponse {

  private boolean tokenIsValid;

  public boolean isTokenIsValid() {
    return tokenIsValid;
  }

  public void setTokenIsValid(boolean tokenIsValid) {
    this.tokenIsValid = tokenIsValid;
  }
}
