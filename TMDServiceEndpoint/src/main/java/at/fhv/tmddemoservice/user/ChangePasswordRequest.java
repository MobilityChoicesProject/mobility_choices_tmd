package at.fhv.tmddemoservice.user;

/**
 * Created by Johannes on 26.07.2017.
 */
public class ChangePasswordRequest {

  private String username;
  private char[] oldPassword;
  private char[] newPassword;

  public String getUsername() {
    return username;
  }

  public void setUsername(String username) {
    this.username = username;
  }

  public char[] getOldPassword() {
    return oldPassword;
  }

  public void setOldPassword(char[] oldPassword) {
    this.oldPassword = oldPassword;
  }

  public char[] getNewPassword() {
    return newPassword;
  }

  public void setNewPassword(char[] newPassword) {
    this.newPassword = newPassword;
  }
}
