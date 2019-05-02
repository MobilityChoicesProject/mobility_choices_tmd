package at.fhv.transportClassifier.common.configSettings;

import java.util.List;

/**
 * Created by Johannes on 09.08.2017.
 */
public interface ConfigService {

  double getValue(String key)  ;

  List<ConfigSetting> getConfigSettings()  ;

  List<ConfigGroup> getConfigGroups();

  void chanceSettings(List<NewSettings> newSettings)  ;

  List<ConfigSetting> getDeafaultSettings()  ;

}
