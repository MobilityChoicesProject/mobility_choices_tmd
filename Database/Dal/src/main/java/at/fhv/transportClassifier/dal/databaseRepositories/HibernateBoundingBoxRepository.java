package at.fhv.transportClassifier.dal.databaseRepositories;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.databaseEntities.BoundingBoxEntity;
import at.fhv.transportdetector.trackingtypes.BoundingBox;

/**
 * Created by Johannes on 15.02.2017.
 */
public class HibernateBoundingBoxRepository {

    protected HibernateSessionMananger sessionMananger;

    public HibernateBoundingBoxRepository(HibernateSessionMananger sessionMananger) {
        this.sessionMananger = sessionMananger;
    }

    public BoundingBoxEntity createBoundingBox(BoundingBox boundingBox){
        BoundingBoxEntity boundingBoxEntity = new BoundingBoxEntity();
        boundingBoxEntity.setEastLongitude(boundingBox.getEastLongitude());
        boundingBoxEntity.setWestLongitude(boundingBox.getWestLongitude());
        boundingBoxEntity.setNorthLatitude(boundingBox.getNorthLatitude());
        boundingBoxEntity.setSouthLatitude(boundingBox.getSouthLatitude());

        sessionMananger.getSession().saveOrUpdate(boundingBoxEntity);
        return boundingBoxEntity;
    }









}
