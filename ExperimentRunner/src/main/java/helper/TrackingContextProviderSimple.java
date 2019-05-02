package helper;

import at.fhv.context.TrackingContext;
import at.fhv.tmd.processFlow.TrackingContextProvider;

/**
 * Created by Johannes on 31.07.2017.
 */
public class TrackingContextProviderSimple implements TrackingContextProvider {

  private TrackingContext trackingContext;

  public TrackingContextProviderSimple(TrackingContext trackingContext) {
    this.trackingContext = trackingContext;
  }

  @Override
  public TrackingContext getTrackingContext() {
    return trackingContext;
  }
}
