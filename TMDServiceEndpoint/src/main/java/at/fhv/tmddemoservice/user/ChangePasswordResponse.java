package at.fhv.tmddemoservice.user;

/**
 * Created by Johannes on 26.07.2017.
 */
public class ChangePasswordResponse {

  private String status;

  public ChangePasswordResponse() {
  }

  public ChangePasswordResponse(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
