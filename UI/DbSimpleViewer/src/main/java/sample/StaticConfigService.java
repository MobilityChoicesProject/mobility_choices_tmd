package sample;

import at.fhv.transportClassifier.common.configSettings.ConfigDao;
import at.fhv.transportClassifier.common.configSettings.ConfigGroup;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceImp;
import at.fhv.transportClassifier.common.configSettings.ConfigSetting;
import at.fhv.transportClassifier.common.configSettings.NewSettings;
import at.fhv.transportClassifier.common.transaction.NoTransaction;
import at.fhv.transportClassifier.mainserver.impl.ConfigSettingDaoImp;
import java.util.List;

/**
 * Created by Johannes on 14.08.2017.
 */
public class StaticConfigService implements ConfigService {

  private ConfigServiceDefaultCache configServiceImp = new ConfigServiceDefaultCache() ;

  private static StaticConfigService instance = new StaticConfigService();
  public static ConfigService getInstance(){
    return instance;
  }



  @Override
  public double getValue(String key) {

    ConfigSetting configSetting = configServiceImp.get(key);
    return configSetting.getValue() ;
  }

  @Override
  public List<ConfigSetting> getConfigSettings() {
    return null;
  }

  @Override
  public List<ConfigGroup> getConfigGroups() {
    return null;
  }

  @Override
  public void chanceSettings(List<NewSettings> newSettings) {

  }

  @Override
  public List<ConfigSetting> getDeafaultSettings() {
    return null;
  }
}
