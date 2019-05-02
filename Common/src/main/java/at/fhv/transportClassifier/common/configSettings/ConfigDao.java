package at.fhv.transportClassifier.common.configSettings;

import at.fhv.transportClassifier.common.transaction.TransactionException;
import java.util.List;

/**
 * Created by Johannes on 10.08.2017.
 */
public interface ConfigDao {

  public void saveSettigns(List<ConfigSetting> configSettingList ) throws TransactionException;

  public List<ConfigSetting> loadSettings() throws TransactionException;

}
