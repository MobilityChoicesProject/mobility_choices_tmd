package at.fhv.transportClassifier.dal.interfaces;

import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import java.util.List;

/**
 * Created by Johannes on 01.03.2017.
 */
public interface LeightweightTrackingDao {

    List<LeightweightTracking> getAll();

    Tracking getFullTracking(LeightweightTracking leightweightTracking);

    List<LeightweightTracking> getAll(int startIndex, int countLimit);

    Tracking getGpsTracking(LeightweightTracking leightweightTracking);


    Tracking getGpsTracking(long id);
}
