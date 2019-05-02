package at.fhv.transportClassifier.dal.spezifications;

import at.fhv.transportClassifier.dal.interfaces.TrackingInfoFilenameSpezification;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;

/**
 * Created by Johannes on 15.02.2017.
 */
public class HibernateTrackingInfoFilenameSpezification implements TrackingInfoFilenameSpezification {



    @Override
    public boolean isReady() {
        return true;
    }

    @Override
    public boolean isSatiesfiedBy(TrackingInfo entity) {
        return entity.getInfoName().equals(Constants.FILENAME);
    }
}
