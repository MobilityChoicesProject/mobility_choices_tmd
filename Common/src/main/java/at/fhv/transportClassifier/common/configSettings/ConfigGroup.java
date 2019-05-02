package at.fhv.transportClassifier.common.configSettings;

import java.util.LinkedList;
import java.util.List;

public class ConfigGroup {


  private String name;
  private String description;
  private List<ConfigSetting> configSettingList;

  public ConfigGroup() {
  }


  public void addConfigSetting(ConfigSetting configSetting){
    if(configSettingList == null){
      configSettingList= new LinkedList<>();
    }
    configSettingList.add(configSetting);

  }


  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public List<ConfigSetting> getConfigSettingList() {
    return configSettingList;
  }

  public void setConfigSettingList(
      List<ConfigSetting> configSettingList) {
    this.configSettingList = configSettingList;
  }
}
