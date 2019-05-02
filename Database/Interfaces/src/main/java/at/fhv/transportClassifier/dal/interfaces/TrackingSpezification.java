package at.fhv.transportClassifier.dal.interfaces;

import at.fhv.transportdetector.trackingtypes.Tracking;

/**
 * Created by Johannes on 09.02.2017.
 */
public interface TrackingSpezification extends Spezification<Tracking>{

    boolean isReady();
    boolean isSatiesfiedBy(Tracking tracking);

}
