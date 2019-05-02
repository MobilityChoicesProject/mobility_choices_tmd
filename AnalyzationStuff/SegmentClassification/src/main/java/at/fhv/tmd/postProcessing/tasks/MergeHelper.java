package at.fhv.tmd.postProcessing.tasks;

import static at.fhv.tmd.postProcessing.PostprocessHelper.meltThreeSegments;
import static at.fhv.tmd.postProcessing.PostprocessHelper.meltTwoSegments;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.tasks.MovingSignalShortage.BestMelting;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportdetector.trackingtypes.TransportType;

/**
 * Created by Johannes on 03.08.2017.
 */
public class MergeHelper {

    public static int findBestMerge(TrackingContext trackingContext, SegmentContext segmentContext, String source, Classifier classifier, FeatureCalculationService featureCalculationService) {
        ClassificationResult meltWithPreviousResult = null;
        ClassificationResult meltWithNextResult = null;
        ClassificationResult meltWithPreviousAndNextResult = null;

        CoordinateInterpolator coordinateInterpolator = trackingContext.getData(TrackingContext.COORDINATE_INTERPOLATOR);

        if (segmentContext.getData(SegmentContext.PRE_TYPE) == SegmentPreType.stationaryCluster) {
            return 0;
        }
        if ((segmentContext.hasPreviousContext() && segmentContext.getPreviousContext().getData(SegmentContext.PRE_TYPE) == SegmentPreType.stationaryCluster &&
                segmentContext.hasNextContext() && segmentContext.getNextContext().getData(SegmentContext.PRE_TYPE) == SegmentPreType.stationaryCluster)) {
            meltThreeSegments(trackingContext, segmentContext.getPreviousContext(), segmentContext, segmentContext.getNextContext(), segmentContext.getPreviousContext().getData(SegmentContext.CLASSIFICATION_RESULT),
                    source + "_meltWithPreviousAndNext");
            return 1;
        }

        if (segmentContext.hasPreviousContext() && (segmentContext.getPreviousContext().getData(SegmentContext.PRE_TYPE) != SegmentPreType.stationaryCluster
                || !segmentContext.hasNextContext())) { // segment to merge is last segment
            SegmentContext previousContext = segmentContext.getPreviousContext();
            meltWithPreviousResult = PostprocessHelper
                    .classifiyNew(featureCalculationService, previousContext.getStartTime(), segmentContext.getEndTime(), coordinateInterpolator, classifier);
        }
        if (segmentContext.hasNextContext() && (segmentContext.getNextContext().getData(SegmentContext.PRE_TYPE) != SegmentPreType.stationaryCluster
                || !segmentContext.hasPreviousContext())) { // segment to merge is first segment
            SegmentContext nextContext = segmentContext.getNextContext();
            meltWithNextResult = PostprocessHelper.classifiyNew(featureCalculationService, segmentContext.getStartTime(), nextContext.getEndTime(), coordinateInterpolator, classifier);
        }

        if (segmentContext.hasNextContext() && segmentContext.hasPreviousContext() &&
                segmentContext.getPreviousContext().getData(SegmentContext.PRE_TYPE) != SegmentPreType.stationaryCluster &&
                segmentContext.getNextContext().getData(SegmentContext.PRE_TYPE) != SegmentPreType.stationaryCluster) {
            SegmentContext previousContext = segmentContext.getPreviousContext();
            SegmentContext nextContext = segmentContext.getNextContext();
            meltWithPreviousAndNextResult = PostprocessHelper.classifiyNew(featureCalculationService, previousContext.getStartTime(), nextContext.getEndTime(), coordinateInterpolator, classifier);
        }

        //evaluate
        double bestResult = 0;
        BestMelting bestMelting = null;
        if (meltWithPreviousResult != null) {
            TransportType mostLikeliestResult = meltWithPreviousResult.getMostLikeliestResult();
            double likelyHoodFor = meltWithPreviousResult.getLikelyHoodFor(mostLikeliestResult);
            if (bestResult < likelyHoodFor) {
                bestResult = likelyHoodFor;
                bestMelting = BestMelting.meltWithPrevious;
            }
        }
        if (meltWithNextResult != null) {
            TransportType mostLikeliestResult = meltWithNextResult.getMostLikeliestResult();
            double likelyHoodFor = meltWithNextResult.getLikelyHoodFor(mostLikeliestResult);
            if (bestResult < likelyHoodFor) {
                bestResult = likelyHoodFor;
                bestMelting = BestMelting.meltWithNext;
            }
        }
        if (meltWithPreviousAndNextResult != null) {
            TransportType mostLikeliestResult = meltWithPreviousAndNextResult.getMostLikeliestResult();
            double likelyHoodFor = meltWithPreviousAndNextResult.getLikelyHoodFor(mostLikeliestResult);
            if (bestResult < likelyHoodFor) {
                bestResult = likelyHoodFor;
                bestMelting = BestMelting.meltWithPreviousAndNext;
            }
        }

        if (bestMelting == null) {
            throw new RuntimeException("should not happen. trackingid: " + trackingContext.getTrackingId());
        }
        switch (bestMelting) {
            case meltWithNext: {
                meltTwoSegments(trackingContext, segmentContext, segmentContext.getNextContext(), meltWithNextResult, source + "_meltWithNext");
                return 0;
            }
            case meltWithPrevious: {
                meltTwoSegments(trackingContext, segmentContext.getPreviousContext(), segmentContext, meltWithPreviousResult, source + "_meltWithPrevious");
                return 1;
            }
            case meltWithPreviousAndNext: {
                meltThreeSegments(trackingContext, segmentContext.getPreviousContext(), segmentContext, segmentContext.getNextContext(), meltWithPreviousAndNextResult, source + "_meltWithPreviousAndNext");
                return 1;
            }
        }

        return 0;
    }

}
