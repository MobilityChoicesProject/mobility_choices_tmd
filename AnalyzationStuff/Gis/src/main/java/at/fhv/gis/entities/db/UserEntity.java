package at.fhv.gis.entities.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Johannes on 10.08.2017.
 */

@Entity
@Table(name = "user_management")
public class UserEntity {

  @Id
  private String username;


  private String hashedPwd;

  public UserEntity() {
  }

  public UserEntity(String username, String hashedPwd) {
    this.username = username;
    this.hashedPwd = hashedPwd;
  }

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public String getHashedPwd() {
    return hashedPwd;
  }

  public void setHashedPwd(String hashedPwd) {
    this.hashedPwd = hashedPwd;
  }
}
