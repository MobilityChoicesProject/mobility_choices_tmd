package at.fhv.tmddemoservice.configsettings;

/**
 * Created by Johannes on 09.08.2017.
 */
public class ConfigSettingEntity {

  private String key;
  private double value;
  private String description;

  public ConfigSettingEntity() {
  }

  public ConfigSettingEntity(String key, double value, String description) {
    this.key = key;
    this.value = value;
    this.description = description;
  }

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

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }
}
