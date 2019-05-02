package at.fhv.tmddemoservice.configsettings;

/**
 * Created by Johannes on 09.08.2017.
 */
public class ConfigSettingRequest {

  private String key;
  private double value;

  public String getKey() {
    return key;
  }

  public void setKey(String key) {
    this.key = key;
  }

  public double getValue() {
    return value;
  }

  public void setValue(double value) {
    this.value = value;
  }
}
