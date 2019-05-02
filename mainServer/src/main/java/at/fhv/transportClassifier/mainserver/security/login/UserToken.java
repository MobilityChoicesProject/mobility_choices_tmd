package at.fhv.transportClassifier.mainserver.security.login;

import javax.persistence.Entity;

/**
 * Created by Johannes on 26.07.2017.
 */
@Entity(name = "User")
public class UserToken {


  private String username;
  private String token;


  public UserToken() {
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getToken() {
    return token;
  }

  public void setToken(String token) {
    this.token = token;
  }

  public UserToken(String username,  String token) {
    this.username = username;
    this.token = token;
  }

  public String getUsername() {
    return username;
  }


  public String getHashedPassword() {
    return token;
  }
}
