package at.fhv.tmddemoservice.user;

/**
 * Created by Johannes on 26.07.2017.
 */
public class LoginResponse {

  private String status;
  private String token;

  public LoginResponse() {
  }

  public LoginResponse(String status, String token) {
    this.status = status;
    this.token = token;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }
}
