package at.fhv.transportClassifier.dal.spezifications;

import at.fhv.transportClassifier.dal.interfaces.TrackingSpezificationById;
import at.fhv.transportdetector.trackingtypes.Tracking;

/**
 * Created by Johannes on 01.03.2017.
 */
public class HibernateTrackingSpezificiationByIncludedData implements TrackingSpezificationById {

    private int id;
    private boolean ready;

    @Override
    public boolean isReady() {
        return ready;
    }

    @Override
    public boolean isSatiesfiedBy(Tracking tracking) {


        return false;
    }


    @Override
    public void setId(int id) {
        this.id = id;
        ready = true;
    }

}
