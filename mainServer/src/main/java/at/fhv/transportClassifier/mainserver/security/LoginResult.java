package at.fhv.transportClassifier.mainserver.security;

/**
 * Created by Johannes on 26.07.2017.
 */
public class LoginResult {

  private boolean succesfull;
  private String token;

  public LoginResult(boolean succesfull, String token) {
    this.succesfull = succesfull;
    this.token = token;
  }

  public boolean isSuccesfull() {
    return succesfull;
  }

  public String getToken() {
    return token;
  }
}
