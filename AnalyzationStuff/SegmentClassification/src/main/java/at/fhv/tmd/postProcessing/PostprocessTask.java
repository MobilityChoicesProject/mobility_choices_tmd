package at.fhv.tmd.postProcessing;

import at.fhv.context.TrackingContext;
import at.fhv.transportClassifier.common.configSettings.ConfigService;

/**
 * Created by Johannes on 19.07.2017.
 */
public interface PostprocessTask {

  int getPriorityLevel();

  void process(TrackingContext trackingContext);
}

