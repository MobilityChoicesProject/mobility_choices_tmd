package at.fhv.transportClassifier.mainserver.security;

import at.fhv.gis.entities.db.UserEntity;
import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

/**
 * Created by Johannes on 11.08.2017.
 */
public class UserDao {


  EntityManager entityManager;
  ITransaction transaction;

  public void set(EntityManager entityManager,ITransaction transaction){
    this.entityManager= entityManager;
    this.transaction = transaction;
  }

  public void saveUser(String username, String hashedPwd) throws TransactionException {

    transaction.beginn();
    UserEntity userEntity = new UserEntity(username,hashedPwd);
    entityManager.merge(userEntity);
    transaction.commit();

  }

  public List<UserEntity> getUsers() throws TransactionException {

    transaction.beginn();

    transaction.beginn();
    CriteriaBuilder cb = entityManager.getCriteriaBuilder();
    CriteriaQuery<UserEntity> cq = cb.createQuery(UserEntity.class);
    Root<UserEntity> rootEntry = cq.from(UserEntity.class);
    CriteriaQuery<UserEntity> all = cq.select(rootEntry);
    TypedQuery<UserEntity> allQuery = entityManager.createQuery(all);
    List<UserEntity> resultList = allQuery.getResultList();
    transaction.commit();

    return resultList;
  }






}
