package at.fhv.transportClassifier.common.configSettings;

import at.fhv.transportClassifier.common.transaction.TransactionException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johannes on 09.08.2017.
 */
public class ConfigServiceImp implements ConfigService {


  ConfigDao configDao;
  public ConfigServiceImp(ConfigDao configDao) {
    this.configDao = configDao;
  }



  protected ConfigServiceDefaultCache defaultValueCache = new ConfigServiceDefaultCache();
//  protected HashMap<String,ConfigSetting> defaultValueCache = new HashMap();
  private HashMap<String,ConfigSetting> cache = new HashMap();




  public void init() throws ConfigurationExcepction {

    List<ConfigSetting> configSettings = null;
    try {
      configSettings = configDao.loadSettings();
    } catch (TransactionException e) {
      throw new ConfigurationExcepction(e);
    }
    for (ConfigSetting configSetting : configSettings) {
      cache.put(configSetting.getKey(),configSetting);
    }

  }




  @Override
  synchronized public double getValue(String key){
    if(cache.containsKey(key)){
      return cache.get(key).getValue();
    }else{
      return defaultValueCache.get(key).getValue();
    }
  }




  @Override
  synchronized public List<ConfigSetting> getConfigSettings(){

    List<ConfigSetting> ConfigSettings = new ArrayList<>();
    for (String s : defaultValueCache.keySet()) {

      if(cache.containsKey(s)){
        ConfigSetting configSetting = cache.get(s);
        ConfigSettings.add(configSetting);
      }else{
        ConfigSetting configSetting = defaultValueCache.get(s);
        ConfigSettings.add(configSetting);
      }
    }
    return ConfigSettings;
  }


  @Override
  synchronized public List<ConfigGroup> getConfigGroups(){
    List<ConfigGroup> groups = defaultValueCache.getGroups();

    List<ConfigGroup> configGroups = new ArrayList<>(groups.size());
    for (ConfigGroup group : groups) {
      ConfigGroup newConfigGroup = new ConfigGroup();

      for (ConfigSetting configSetting : group.getConfigSettingList()) {
        if(cache.containsKey(configSetting.getKey())){
          ConfigSetting configSettingFromCache = cache.get(configSetting.getKey());
          ConfigSetting newConfigSetting = new ConfigSetting(configSettingFromCache.getKey(),configSetting.getName(),configSetting.getDescription(),configSettingFromCache.getValue());
          newConfigGroup.addConfigSetting(newConfigSetting);
        }else{
          newConfigGroup.addConfigSetting(configSetting);
        }
        newConfigGroup.setName(group.getName());
        newConfigGroup.setDescription(group.getDescription());
      }

      configGroups.add(newConfigGroup);
    }
    return configGroups;
  }


  @Override
  synchronized public void chanceSettings(List<NewSettings> newSettings)
      throws ConfigurationExcepction {

    for (NewSettings newSetting : newSettings) {

      boolean containsKey  = cache.keySet().contains(newSetting.getKey()) || defaultValueCache.containsKey(newSetting.getKey());
      if (!containsKey) {
        throw new IllegalArgumentException("key is not suppored");
      }
    }
    List<ConfigSetting> configSettingsToAdd = new ArrayList<>();
    for (NewSettings newSetting : newSettings) {
      String key = newSetting.getKey();
      double value = newSetting.getValue();
      ConfigSetting configSetting;

      if(cache.containsKey(newSetting.getKey())) {
        configSetting = cache.get(key);
      }else{
        configSetting = defaultValueCache.get(key);
      }

      configSettingsToAdd.add(new ConfigSetting(key,configSetting.getDescription(),value));
    }

    try{

      configDao.saveSettigns(configSettingsToAdd);
      for (ConfigSetting configSetting : configSettingsToAdd) {
        cache.put(configSetting.getKey(),configSetting);
      }
    }catch (TransactionException transactionEx){
      throw new ConfigurationExcepction(transactionEx);
    }


  }


  @Override
  public List<ConfigSetting> getDeafaultSettings() {
    List<ConfigSetting> ConfigSettings = new ArrayList<>();
    for (String s : defaultValueCache.keySet()) {
      ConfigSetting configSetting = defaultValueCache.get(s);
      ConfigSettings.add(configSetting);
    }
    return ConfigSettings;
  }


}
