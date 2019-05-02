package at.fhv.gis.entities.db;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Johannes on 10.08.2017.
 */

@Entity
@Table(name = "configuration_setting")
public class ConfigSettingEntity {

  @Id
  private String configKey;
  private double configValue;
  private String description;


  public ConfigSettingEntity() {
  }

  public ConfigSettingEntity(String configKey, String description, double configValue) {
    this.configKey = configKey;
    this.description = description;
    this.configValue = configValue;
  }

  public String getConfigKey() {
    return configKey;
  }

  public void setConfigKey(String key) {
    this.configKey = key;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public double getConfigValue() {
    return configValue;
  }

  public void setConfigValue(double configValue) {
    this.configValue = configValue;
  }
}
