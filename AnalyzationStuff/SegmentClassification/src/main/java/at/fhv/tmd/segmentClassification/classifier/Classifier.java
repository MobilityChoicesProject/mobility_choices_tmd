package at.fhv.tmd.segmentClassification.classifier;

import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.util.List;

/**
 * Created by Johannes on 21.07.2017.
 */
public interface Classifier {

  ClassificationResult classify(List<FeatureResult> featureResultListOfOneSegment);
}
