package at.fhv.transportClassifier.mainserver.bean;

import at.fhv.transportClassifier.common.configSettings.ConfigGroup;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceImp;
import at.fhv.transportClassifier.common.configSettings.ConfigSetting;
import at.fhv.transportClassifier.common.configSettings.ConfigurationExcepction;
import at.fhv.transportClassifier.common.configSettings.NewSettings;
import at.fhv.transportClassifier.common.transaction.NoTransaction;
import at.fhv.transportClassifier.mainserver.api.ConfigServerLocal;
import at.fhv.transportClassifier.mainserver.impl.ConfigSettingDaoImp;
import java.util.List;
import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 09.08.2017.
 */
@Singleton
public class ConfigServerBean  implements ConfigServerLocal{

  private static Logger logger = LoggerFactory.getLogger(ConfigServerBean.class);


  ConfigServiceImp configServer;


  @PersistenceContext(unitName = "persistence_context_mysql")
  private EntityManager em;



  private ConfigSettingDaoImp configDao;



  @PostConstruct
  void init() {
    logger.info("initing configDao");
    configDao = new ConfigSettingDaoImp();
    logger.info("inited configDao");
    logger.info("initing ConfigServiceImp");
    configServer = new ConfigServiceImp(configDao);
    logger.info("inited ConfigServiceImp");

  }

  private boolean configerServerNotInitialized = true;
  private void initConfigServer() throws ConfigurationExcepction {
    configDao.init(em,new NoTransaction());
    if(configerServerNotInitialized){
      try {
        configServer.init();
      } catch (ConfigurationExcepction configurationExcepction) {
        throw configurationExcepction;
      }
      configerServerNotInitialized= false;
    }

  }

  @Override
  public double getValue(String key) throws ConfigurationExcepction {
    initConfigServer();
    return configServer.getValue(key);
  }

  @Override
  public List<ConfigSetting> getConfigSettings() throws ConfigurationExcepction {
    initConfigServer();
    return configServer.getConfigSettings();  }

  @Override
  public List<ConfigGroup> getConfigGroups() {
    initConfigServer();

    return configServer.getConfigGroups();
  }

  @Override
  public void chanceSettings(List<NewSettings> newSettings) throws ConfigurationExcepction {
      initConfigServer();
      configServer.chanceSettings(newSettings);

  }

  @Override
  public List<ConfigSetting> getDeafaultSettings() throws ConfigurationExcepction {
    initConfigServer();
    return configServer.getDeafaultSettings();
  }
}
