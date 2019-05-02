package at.fhv.transportClassifier.common.configSettings;

/**
 * Created by Johannes on 09.08.2017.
 */
public class ConfigSetting {


  private String key;
  private String name;
  private String description;
  private double value;


  public ConfigSetting(String key, String name, String description, double value) {
    this.key = key;
    this.name = name;
    this.description = description;
    this.value = value;
  }

  public ConfigSetting(String key, String description, double value) {
    this.key = key;
    this.description = description;
    this.value = value;
  }

  public String getKey() {
    return key;
  }

  public String getDescription() {
    return description;
  }

  public double getValue() {
    return value;
  }

  public String getName() {
    return name;
  }


}
