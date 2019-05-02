package helper;

import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import java.util.List;

/**
 * Created by Johannes on 24.06.2017.
 */
public class ManualyEditedHelper {

  public static boolean isManuallyEdited(List<TrackingInfo> trackingInfos){
    for (TrackingInfo trackingInfo : trackingInfos) {
      if(trackingInfo.getInfoName().equals(Constants.ManualyEdited)){
        return true;
      }
    }
    return false;

  }




}
