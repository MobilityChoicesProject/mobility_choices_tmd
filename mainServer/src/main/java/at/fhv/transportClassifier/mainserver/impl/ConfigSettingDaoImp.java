package at.fhv.transportClassifier.mainserver.impl;

import at.fhv.gis.entities.db.ConfigSettingEntity;
import at.fhv.transportClassifier.common.configSettings.ConfigDao;
import at.fhv.transportClassifier.common.configSettings.ConfigSetting;
import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by Johannes on 10.08.2017.
 */
public class ConfigSettingDaoImp implements ConfigDao {

  private EntityManager entityManager;
  private ITransaction transaction;


  public void init(EntityManager entityManager, ITransaction transaction){
    this.entityManager = entityManager;
    this.transaction = transaction;
  }


  @Override
  public void saveSettigns(List<ConfigSetting> configSettingList) throws TransactionException {

      transaction.beginn();

      for (ConfigSetting configSetting : configSettingList) {
        ConfigSettingEntity configSettingEntity = new ConfigSettingEntity(configSetting.getKey(),configSetting.getDescription(),configSetting.getValue());
        entityManager.merge(configSettingEntity);
      }

      transaction.commit();
  }



  @Override
  public List<ConfigSetting> loadSettings() throws TransactionException {

    transaction.beginn();
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<ConfigSettingEntity> cq = cb.createQuery(ConfigSettingEntity.class);
    Root<ConfigSettingEntity> rootEntry = cq.from(ConfigSettingEntity.class);
    CriteriaQuery<ConfigSettingEntity> all = cq.select(rootEntry);
    TypedQuery<ConfigSettingEntity> allQuery = entityManager.createQuery(all);
    List<ConfigSettingEntity> resultList = allQuery.getResultList();

    ArrayList<ConfigSetting> configSettings = new ArrayList<>();
    for (ConfigSettingEntity configSettingEntity : resultList) {
      configSettings.add(new ConfigSetting(configSettingEntity.getConfigKey(),configSettingEntity.getDescription(),configSettingEntity.getConfigValue()));
    }

    transaction.commit();

    return configSettings;

  }
}
