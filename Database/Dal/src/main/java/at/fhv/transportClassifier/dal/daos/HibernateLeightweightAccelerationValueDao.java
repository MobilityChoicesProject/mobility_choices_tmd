package at.fhv.transportClassifier.dal.daos;

import at.fhv.transportClassifier.dal.HibernateSessionMananger;
import at.fhv.transportClassifier.dal.interfaces.LeightweightAccelerationValueDao;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import org.hibernate.Session;

/**
 * Created by Johannes on 01.03.2017.
 */
public class HibernateLeightweightAccelerationValueDao implements LeightweightAccelerationValueDao {

    private HibernateSessionMananger hibernateSessionMananger;


    public HibernateLeightweightAccelerationValueDao(HibernateSessionMananger hibernateSessionMananger) {
        this.hibernateSessionMananger = hibernateSessionMananger;

    }

    private HibernateSessionMananger getInternalHibernateSessionMananger() {
        return hibernateSessionMananger;
    }

    @Override
    public int count(LeightweightTracking tracking) {
        Session session = getInternalHibernateSessionMananger().getSession();
        int count = ((Long)session.createQuery("select count(*) from AccelerationValueEntity where tracking_idTracking = "+ tracking.getTrackingId()+ "").uniqueResult()).intValue();
        return count;
}






}
