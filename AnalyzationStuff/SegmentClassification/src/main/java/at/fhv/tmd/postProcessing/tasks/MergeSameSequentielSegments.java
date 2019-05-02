package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import javafx.geometry.Pos;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 23.07.2017.
 */
public class MergeSameSequentielSegments implements PostprocessTask {

    public static final String source = "MergeSameSequentielSegments";

    FeatureCalculationService featureCalculationService;
    Classifier classifier;

    public MergeSameSequentielSegments(Classifier classifier,
                                       FeatureCalculationService featureCalculationService) {
        this.featureCalculationService = featureCalculationService;
        this.classifier = classifier;
    }

    @Override
    public int getPriorityLevel() {
        return 1000;
    }

    @Override
    public void process(TrackingContext trackingContext) {
        boolean changedSomething = true;
        while (changedSomething) {
            changedSomething = doProcess(trackingContext);
        }


    }

    private boolean doProcess(TrackingContext trackingContext) {

        CoordinateInterpolator coordinateInterpolator = trackingContext.getData(TrackingContext.COORDINATE_INTERPOLATOR);
        List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();
        int size = segmentContextList.size();
        boolean changedSomething = false;
        for (int i = 0; i < size; i++) {

            SegmentContext segmentContext = segmentContextList.get(i);
            TransportType transportType = PostprocessHelper.getTransportType(segmentContext);
            int j = i + 1;
            for (; j < size; j++) {
                SegmentContext followSegmentContext = segmentContextList.get(j);
                TransportType followingtransportType = PostprocessHelper.getTransportType(followSegmentContext);
                if (transportType != followingtransportType) {
                    break;
                }
            }
            j--;

            if (i >= j) {
                continue;
            }
            changedSomething = true;
            SegmentContext lastSegmentWithSameType = segmentContextList.get(j);

            LocalDateTime startTime = segmentContext.getStartTime();
            LocalDateTime endTime = lastSegmentWithSameType.getEndTime();

            List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
                    .getInterpolatedCoordinatesExact(startTime, endTime, Duration.ofSeconds(1));

            ClassificationResult classificationResult;
            if (PostprocessHelper.getTransportType(segmentContext) == TransportType.STATIONARY) {
                List<Tuple<TransportType, Double>> classification = new ArrayList<>();
                classification.add(new Tuple<>(TransportType.STATIONARY, 1.0));
                classificationResult = new ClassificationResult(classification);
            } else {
                List<FeatureResult> featureResults = featureCalculationService
                        .calcFeatures(interpolatedCoordinatesExact, coordinateInterpolator);

                classificationResult = classifier.classify(featureResults);
            }
            for (; j >= i; j--) {
                segmentContextList.remove(j);
            }

            SegmentContext mergedSegmentContext = new SegmentContext();
            mergedSegmentContext.setStartTime(startTime);
            mergedSegmentContext.setEndTime(endTime);
            if (lastSegmentWithSameType.hasNextContext()) {
                mergedSegmentContext.setNextContext(lastSegmentWithSameType.getNextContext());
                lastSegmentWithSameType.getNextContext().setPreviousContext(mergedSegmentContext);
            }
            if (segmentContext.hasPreviousContext()) {
                mergedSegmentContext.setPreviousContext(segmentContext.getPreviousContext());
                segmentContext.getPreviousContext().setNextContext(mergedSegmentContext);
            }

            PostprocessHelper.addPostProcessResult(mergedSegmentContext, classificationResult, source);
            segmentContextList.add(i, mergedSegmentContext);
            size = segmentContextList.size();
        }

        return changedSomething;
    }
}
