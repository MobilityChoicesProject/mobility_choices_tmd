package at.fhv.tmddemoservice.user;

/**
 * Created by Johannes on 26.07.2017.
 */
public class LoginRequest {

  private String username;
  private char[] password;

  public LoginRequest() {
  }

  public LoginRequest(String username, char[] password) {
    this.username = username;
    this.password = password;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public char[] getPassword() {
    return password;
  }

  public void setPassword(char[] password) {
    this.password = password;
  }
}
