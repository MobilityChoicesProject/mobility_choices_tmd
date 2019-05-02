package at.fhv.tmddemoservice.configsettings;

/**
 * Created by Johannes on 11.08.2017.
 */
public class ConfigChangedStatus {


  private String status;

  public ConfigChangedStatus() {
  }

  public ConfigChangedStatus(String status) {
    this.status = status;
  }

  public String getStatus() {
    return status;
  }

  public void setStatus(String status) {
    this.status = status;
  }
}
