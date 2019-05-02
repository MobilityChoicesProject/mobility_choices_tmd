package experiments;

import at.fhv.gis.GisDataCreationService;
import at.fhv.gis.GisDataDao;
import at.fhv.gis.Overpass.GisDataCreationException;
import at.fhv.transportClassifier.mainserver.transaction.EntityManagerTransaction;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

/**
 * Created by Johannes on 11.08.2017.
 */
public class GisDataCreationExperiment {



  public void doIt(EntityManager entityManager){

    GisDataDao gisDataDao = new GisDataDao();
    EntityTransaction transaction = entityManager.getTransaction();
    EntityManagerTransaction entityManagerTransaction = new EntityManagerTransaction(transaction);
    gisDataDao.init(entityManager,entityManagerTransaction);
    GisDataCreationService gisDataCreationService = new GisDataCreationService(gisDataDao);
    try {
      gisDataCreationService.create(new SimpleBoundingBox(46.5,6.5,48.5,15.5));
    } catch (GisDataCreationException e) {
      e.printStackTrace();
    }


  }


}
