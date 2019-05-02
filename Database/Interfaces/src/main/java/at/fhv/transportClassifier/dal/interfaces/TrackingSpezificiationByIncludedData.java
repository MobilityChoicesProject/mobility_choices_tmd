package at.fhv.transportClassifier.dal.interfaces;

/**
 * Created by Johannes on 09.02.2017.
 */
public interface TrackingSpezificiationByIncludedData extends TrackingSpezification {
    void includeAcceleratorAndDisplayStateChangeEventData(boolean flag);
}
