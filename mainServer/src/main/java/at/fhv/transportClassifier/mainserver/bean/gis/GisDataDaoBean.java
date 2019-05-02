package at.fhv.transportClassifier.mainserver.bean.gis;

import at.fhv.gis.entities.db.GisArea;
import at.fhv.gis.entities.db.GisDataUpdate;
import at.fhv.gis.entities.db.GisDataUpdateEntity;
import at.fhv.gis.entities.db.GisDataUpdateStatusEntity;
import at.fhv.gis.entities.db.GisPoint;
import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.Resource;
import javax.ejb.Singleton;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 24.05.2017.
 */
@Singleton
public class GisDataDaoBean implements GisDataDaoLocal {

  private static Logger logger = LoggerFactory.getLogger(GisDataDaoBean.class);

  @PersistenceContext(unitName = "persistence_context_mysql")
  private EntityManager em;


  @Override
  public void initGisData(GisDataUpdateEntity gisDataUpdateEntity){
    setLastRunningToFailed();
    gisDataUpdateEntity.setTimestamp(LocalDateTime.now());
    gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.running);
    em.persist(gisDataUpdateEntity);
  }


  @Override
  public void updateGisData(GisDataUpdateEntity gisDataUpdateEntity, GisArea gisArea,
      Iterable<GisPoint> gisPoints){
    em.merge(gisDataUpdateEntity);
    em.flush();
    em.persist(gisArea);
    em.flush();
    for (GisPoint gisPoint : gisPoints) {
      gisPoint.setArea_id(gisArea.getId());
      gisPoint.setGisDataUpdateId(gisDataUpdateEntity.getId());
      em.persist(gisPoint);
    }
  }


  @Override
  public void activateStatus(GisDataUpdateEntity gisDataUpdateEntity){
    setLastOkToOld();
    gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.ok);
    em.merge(gisDataUpdateEntity);
  }



  @Override
  public List<GisDataUpdateEntity> getLast10DataUpdates(){
    List<GisDataUpdateEntity> lastDataUpdates = getLastDataUpdates(10, false);
    return lastDataUpdates;
  }


  // internal stuff - internal stuff - internal stuff - internal stuff - internal stuff - internal stuff



  private List<GisDataUpdateEntity> getLastDataUpdates(int maxNumber, boolean onlyOk)  {
    List<GisDataUpdateEntity> oneResultList;

      CriteriaBuilder updateCB = em.getCriteriaBuilder();
      CriteriaQuery<GisDataUpdateEntity> gisDataStatusCriteria = updateCB
          .createQuery(GisDataUpdateEntity.class);
      Root<GisDataUpdateEntity> updateroot = gisDataStatusCriteria.from(GisDataUpdateEntity.class);
      if (onlyOk) {
        Path<Object> status = updateroot.get("Status");
        Predicate okstatusPredicate = updateCB.equal(status, GisDataUpdateStatusEntity.ok);
        gisDataStatusCriteria.where(okstatusPredicate);
      }
      Path<Object> timestamp = updateroot.get("timestamp");
      gisDataStatusCriteria.orderBy(updateCB.desc(timestamp));

      TypedQuery<GisDataUpdateEntity> updateDataQuery = em.createQuery(gisDataStatusCriteria);
      updateDataQuery.setMaxResults(maxNumber);
      updateDataQuery.setFirstResult(0);
      oneResultList = updateDataQuery.getResultList();
    return oneResultList;

  }


  private void setLastRunningToFailed() {
    List<GisDataUpdateEntity> lastDataUpdates = getLastDataUpdates(1, false);
    if(lastDataUpdates != null && lastDataUpdates.size()== 1){

      GisDataUpdateEntity gisDataUpdateEntity = lastDataUpdates.get(0);
      if(gisDataUpdateEntity.getStatus()== GisDataUpdateStatusEntity.running){
        gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.failed);
        em.merge(gisDataUpdateEntity);
        em.flush();
        removeGpsPointsAndArea(gisDataUpdateEntity);
      }
    }
  }

  private void setLastOkToOld() {
    List<GisDataUpdateEntity> lastDataUpdates = getLastDataUpdates(1, true);
    if(lastDataUpdates != null && lastDataUpdates.size()== 1){

      GisDataUpdateEntity gisDataUpdateEntity = lastDataUpdates.get(0);
      if(gisDataUpdateEntity.getStatus()== GisDataUpdateStatusEntity.ok){
        gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.old);
        em.merge(gisDataUpdateEntity);
        em.flush();
        removeGpsPointsAndArea(gisDataUpdateEntity);
      }
    }
  }


  private void removeGpsPointsAndArea(GisDataUpdateEntity gisDataUpdateEntity){
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<GisArea> gisAreaCriteriaQuery = cb.createQuery(GisArea.class);
    Root<GisArea> root = gisAreaCriteriaQuery.from(GisArea.class);
    Path<Object> gisdataupdate_idGisDataUpdate1 = root.get("gisUpdate");
    Predicate gisdataupdate_idGisDataUpdate = cb.equal(gisdataupdate_idGisDataUpdate1,gisDataUpdateEntity.getId());
    gisAreaCriteriaQuery.where(gisdataupdate_idGisDataUpdate);
    TypedQuery<GisArea> query = em.createQuery(gisAreaCriteriaQuery);
    List<GisArea> resultList = query.getResultList();

    for (GisArea gisArea : resultList) {
      removeGisPoints(gisArea.getId());
      em.flush();
      em.remove(gisArea);
    }
    em.flush();
  }

  private void removeGisPoints(long id ) {
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaDelete<GisPoint> criteriaDelete = cb.createCriteriaDelete(GisPoint.class);
    Root<GisPoint> root = criteriaDelete.from(GisPoint.class);
    Path<Object> area_id = root.get("area_id");
    Predicate p1 = cb.equal(area_id, id);
    criteriaDelete.where(p1);
    em.createQuery(criteriaDelete).executeUpdate();
  }


}
