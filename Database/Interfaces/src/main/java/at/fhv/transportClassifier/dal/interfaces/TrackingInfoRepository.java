package at.fhv.transportClassifier.dal.interfaces;

import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import java.util.List;

/**
 * Created by Johannes on 15.02.2017.
 */
public interface TrackingInfoRepository extends Repository {


    List<TrackingInfo> query(List<Spezification<TrackingInfo>> trackingInfoSpezification);
    Spezification<TrackingInfo> getSpezification(String interfaceName) ;
    List<TrackingInfo> query(Spezification<TrackingInfo> trackingInfoSpezification) ;


}
