package at.fhv.gis;

import at.fhv.gis.entities.db.GisArea;
import at.fhv.gis.entities.db.GisDataUpdateEntity;
import at.fhv.gis.entities.db.GisDataUpdateStatusEntity;
import at.fhv.gis.entities.db.GisPoint;
import at.fhv.transportClassifier.common.transaction.ITransaction;
import at.fhv.transportClassifier.common.transaction.TransactionException;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 24.05.2017.
 */
public class GisDataDao {

  private static Logger logger = LoggerFactory.getLogger(GisDataDao.class);

  private EntityManager em;
  private ITransaction transaction;

  public void init(EntityManager em, ITransaction transaction){
    this.em = em;
    this.transaction = transaction;
  }



  public GisDataUpdateEntity save(GisArea gisArea, Iterable<GisPoint> gisPoints,
      GisDataUpdateEntity gisDataUpdateEntity) throws TransactionException {

    logger.info("starting transaction");
    transaction.beginn();
    logger.info("transaction started");


    if(gisDataUpdateEntity.getId()>0){
      logger.info("merging gisDataUpdateEntity");
      em.merge(gisDataUpdateEntity);
      logger.info("merged gisDataUpdateEntity");
    }else{
      logger.info("persisting gisDataUpdateEntity");
      em.persist(gisDataUpdateEntity);
      logger.info("persisted gisDataUpdateEntity");
    }

    logger.info("flushing entityManager");
    em.flush();
    logger.info("entityManager flushed");
    logger.info("persisting gisArea");
    em.persist(gisArea);
    logger.info("persisted gisArea");

    logger.info("flushing entityManager");
    em.flush();
    logger.info("flushed entityManager ");

    logger.info("merging gisPoints");
    for (GisPoint gisPoint : gisPoints) {
      gisPoint.setArea_id(gisArea.getId());
      em.merge(gisPoint);
    }
    logger.info("merged gisPoints");

    logger.info("commiting transaction");
    transaction.commit();
    logger.info("committed transaction");

    return gisDataUpdateEntity;
  }

  public GisDataUpdateEntity getLastGisDataUpdate() throws TransactionException {
    List<GisDataUpdateEntity> lastDataUpdates = getLastDataUpdates(1,true);
    if(lastDataUpdates== null || lastDataUpdates.size() == 0){
      return null;
    }
    return lastDataUpdates.get(0);

  }

  public void saveGisDataUpdate(GisDataUpdateEntity gisDataUpdateEntity)
      throws TransactionException {

    transaction.beginn();
    em.merge(gisDataUpdateEntity);
    transaction.commit();

  }
  public List<GisDataUpdateEntity> getLastDataUpdates(int maxNumber, boolean onlyOk) throws TransactionException {
    List<GisDataUpdateEntity> oneResultList;
    try {
      transaction.beginn();

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

      transaction.commit();
    }catch(Exception ex){
      logger.error("there was an exception",ex);
      throw ex;
    }
    return oneResultList;

  }

  public void setLatGisUpdateToOldAndRemoveAreasAndPoints()
      throws TransactionException {

    GisDataUpdateEntity gisDataUpdateEntity = getLastGisDataUpdate();
    if(gisDataUpdateEntity== null){
      return;
    }

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

    gisDataUpdateEntity.setStatus(GisDataUpdateStatusEntity.old);
    em.merge(gisDataUpdateEntity);

    transaction.commit();

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



  private GisArea getOldGisArea(GisArea gisArea){
    CriteriaBuilder cb = em.getCriteriaBuilder();
    CriteriaQuery<GisArea> cq =
        cb.createQuery(GisArea.class);
    Root<GisArea> c = cq.from(GisArea.class);
    Path<Object> southWest = c.get("southWest");
    Path<Object> northEast = c.get("northEast");

    List<Predicate> predicates = new ArrayList<>();
    Predicate p1 = cb.equal(southWest, gisArea.getSouthWest());
    Predicate p2 = cb.equal(northEast, gisArea.getNorthEast());
    predicates.add(p1);
    predicates.add(p2);
    cq.select(c).where(predicates.toArray(new Predicate[]{}));
    List<GisArea> resultList = em.createQuery(cq).getResultList();
    if(resultList.size() >0){
      return resultList.get(0);
    }else{
      return null;
    }

  }

  public void activateNewGisStatus(GisDataUpdateEntity gisDataUpdateEntity)
      throws TransactionException {

      List<GisDataUpdateEntity> lastDataUpdates = getLastDataUpdates(1,true);
      if(lastDataUpdates != null  && lastDataUpdates.size()== 1){
        GisDataUpdateEntity oldGisDataUpdate = lastDataUpdates.get(0);
        oldGisDataUpdate.setStatus(GisDataUpdateStatusEntity.old);

        CriteriaBuilder cb = em.getCriteriaBuilder();
        CriteriaQuery<GisArea> gisAreaCriteriaQuery = cb.createQuery(GisArea.class);
        Root<GisArea> root = gisAreaCriteriaQuery.from(GisArea.class);
        Path<Object> gisdataupdate_idGisDataUpdate1 = root.get("gisUpdate");
        Predicate gisdataupdate_idGisDataUpdate = cb.equal(gisdataupdate_idGisDataUpdate1,oldGisDataUpdate.getId());
        gisAreaCriteriaQuery.where(gisdataupdate_idGisDataUpdate);
        TypedQuery<GisArea> query = em.createQuery(gisAreaCriteriaQuery);
        List<GisArea> resultList = query.getResultList();

        for (GisArea gisArea : resultList) {
          removeGisPoints(gisArea.getId());
          em.flush();
          em.remove(gisArea);
        }

        em.merge(oldGisDataUpdate);
      }

      em.merge(gisDataUpdateEntity);

  }
}
