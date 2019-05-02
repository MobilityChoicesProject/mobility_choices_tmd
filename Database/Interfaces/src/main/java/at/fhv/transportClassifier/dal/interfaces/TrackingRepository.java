package at.fhv.transportClassifier.dal.interfaces;

import at.fhv.transportdetector.trackingtypes.Tracking;
import java.util.List;

/**
 * Created by Johannes on 09.02.2017.
 */
public interface TrackingRepository  extends Repository{

    void add(Tracking tracking);

    void remove(Tracking tracking);

    void update(Tracking tracking);

    List<Tracking> query(TrackingSpezification trackingSpezification);
    List<Tracking> query(List<TrackingSpezification> trackingSpezifications);

    Spezification<Tracking> getSpezification(Class<Spezification<Tracking>> spezification);


}
