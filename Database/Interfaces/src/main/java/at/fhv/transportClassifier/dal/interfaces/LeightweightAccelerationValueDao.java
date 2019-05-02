package at.fhv.transportClassifier.dal.interfaces;

import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;

/**
 * Created by Johannes on 01.03.2017.
 */
public interface LeightweightAccelerationValueDao {
    int count(LeightweightTracking tracking);
}
