package at.fhv.transportClassifier.dal.interfaces;

import java.time.LocalDateTime;

/**
 * Created by Johannes on 09.02.2017.
 */
public interface TrackingSpezificationByCreationDuration extends TrackingSpezification {
    void setDuration(LocalDateTime includedMinTime, LocalDateTime excludedMaxTime);
}
