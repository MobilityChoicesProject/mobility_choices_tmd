package at.fhv.tmd.processFlow;

import at.fhv.context.TrackingContext;

/**
 * Created by Johannes on 21.07.2017.
 */
public interface EarlyReturnCondition {


  public boolean isReached(String stage);

  void setTrackingContext(TrackingContext trackingContext);
}
