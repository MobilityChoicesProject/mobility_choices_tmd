package at.fhv.tmd.postProcessing;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Speed;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.processFlow.TransportTypeProbability;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivity;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

/**
 * Created by Johannes on 23.07.2017.
 */
public class PostprocessHelper {


    public static Speed calcSpeed(List<IGpsPoint> pointList) {

        int size = pointList.size();
        if (size < 2) {
            return new Speed(0);
        }

        Iterator<IGpsPoint> iterator = pointList.iterator();

        IGpsPoint lastPoint = iterator.next();

        Speed speedsum = new Speed(0);
        while (iterator.hasNext()) {
            IGpsPoint point = iterator.next();

            Speed speed = CoordinateUtil.calcSpeedBetween(lastPoint, point);
            speedsum = speedsum.plus(speed);
            lastPoint = point;
        }

        double averageSpeedKmH = speedsum.getKmPerHour() / size;
        return new Speed(averageSpeedKmH);


    }

    public static ClassificationResult getClassificationResult(SegmentContext segmentContext) {

        if (segmentContext.hasData(SegmentContext.POST_PROCESS_RESULT)) {
            PostprocessClassifications postprocessClassifications = segmentContext.getData(SegmentContext.POST_PROCESS_RESULT);
            PostProcessClassificationResult postProcessClassificationResult = postprocessClassifications.get(postprocessClassifications.size() - 1);
            ClassificationResult classifiedResult = postProcessClassificationResult.getClassifiedResult();
            return classifiedResult;

        } else {
            if (segmentContext.hasData(SegmentContext.CLASSIFICATION_RESULT)) {
                ClassificationResult classificationResult = segmentContext.getData(SegmentContext.CLASSIFICATION_RESULT);
                return classificationResult;
            } else {
                return null;
            }
        }
    }

    public static TransportType getTransportType(SegmentContext segmentContext) {

        if (segmentContext.hasData(SegmentContext.POST_PROCESS_RESULT)) {
            PostprocessClassifications postprocessClassifications = segmentContext.getData(SegmentContext.POST_PROCESS_RESULT);
            PostProcessClassificationResult postProcessClassificationResult = postprocessClassifications.get(postprocessClassifications.size() - 1);
            ClassificationResult classifiedResult = postProcessClassificationResult.getClassifiedResult();
            TransportType mostLikeliestResult = classifiedResult.getMostLikeliestResult();
            return mostLikeliestResult;

        } else {
            if (segmentContext.hasData(SegmentContext.CLASSIFICATION_RESULT)) {
                ClassificationResult classificationResult = segmentContext.getData(SegmentContext.CLASSIFICATION_RESULT);
                return classificationResult.getMostLikeliestResult();
            } else {
                return null;
            }
        }
    }

    public static List<TransportType> transportTypesAscending = TransportType.getValuesAlpabethicAsc();

    public static List<TransportTypeProbability> getTransportTypeProbabilities(SegmentContext segmentContext) {

        if (!transportTypesAscending.contains(TransportType.STATIONARY)) {
            transportTypesAscending.add(TransportType.STATIONARY);
        }

        if (segmentContext.hasData(SegmentContext.POST_PROCESS_RESULT)) {
            PostprocessClassifications postprocessClassifications = segmentContext.getData(SegmentContext.POST_PROCESS_RESULT);
            PostProcessClassificationResult postProcessClassificationResult = postprocessClassifications.get(postprocessClassifications.size() - 1);
            ClassificationResult classifiedResult = postProcessClassificationResult.getClassifiedResult();

            List<TransportTypeProbability> transportTypeProbabilities = new ArrayList<>();
            for (TransportType transportType : transportTypesAscending) {
                double likelyHoodForTransportType = classifiedResult.getLikelyHoodFor(transportType);
                TransportTypeProbability transportTypeProbability = new TransportTypeProbability(
                        transportType, likelyHoodForTransportType);
                transportTypeProbabilities.add(transportTypeProbability);
            }
            return transportTypeProbabilities;

        } else {
            if (segmentContext.hasData(SegmentContext.CLASSIFICATION_RESULT)) {
                ClassificationResult classificationResult = segmentContext.getData(SegmentContext.CLASSIFICATION_RESULT);
                List<TransportTypeProbability> transportTypeProbabilities = new ArrayList<>();
                for (TransportType transportType : transportTypesAscending) {
                    double likelyHoodForTransportType = classificationResult.getLikelyHoodFor(transportType);
                    TransportTypeProbability transportTypeProbability = new TransportTypeProbability(
                            transportType, likelyHoodForTransportType);
                    transportTypeProbabilities.add(transportTypeProbability);
                }
                return transportTypeProbabilities;
            } else {
                return null;
            }
        }
    }


    public static void addPostProcessResult(SegmentContext segmentContext, ClassificationResult classificationResult, String source) {

        if (!segmentContext.hasData(SegmentContext.POST_PROCESS_RESULT)) {
            segmentContext.addData(SegmentContext.POST_PROCESS_RESULT, new PostprocessClassifications());
        }


        PostProcessClassificationResult postProcessClassificationResult = new PostProcessClassificationResult(source, classificationResult);
        PostprocessClassifications
                .addPostProcessClassification(segmentContext, postProcessClassificationResult);

        PostprocessClassifications postProcessClassificationResults = segmentContext.getData(SegmentContext.POST_PROCESS_RESULT);
        postProcessClassificationResults.add(postProcessClassificationResult);

    }


    public static ClassificationResult classifiyNew(FeatureCalculationService featureCalculationService, LocalDateTime startTime, LocalDateTime endTime, CoordinateInterpolator coordinateInterpolator, Classifier classifier) {

        List<IGpsPoint> interpolatedCoordinatesExact = coordinateInterpolator
                .getInterpolatedCoordinatesExact(startTime, endTime, Duration.ofSeconds(1));

        ArrayList<FeatureResult> featureResults = (ArrayList<FeatureResult>) featureCalculationService.calcFeatures(interpolatedCoordinatesExact, coordinateInterpolator);

        ClassificationResult classificationResult = classifier.classify(featureResults);

        return classificationResult;

    }


    public static void meltTwoSegments(TrackingContext trackingContext, SegmentContext segmentContext1, SegmentContext segmentContext2, ClassificationResult classificationResult, String source) {
        ArrayList<SegmentContext> segmentContextList = (ArrayList<SegmentContext>) trackingContext.getSegmentContextList();
        int index = segmentContextList.indexOf(segmentContext1);
        segmentContextList.remove(segmentContext1);
        segmentContextList.remove(segmentContext2);

        SegmentContext meltedSegmentContext = new SegmentContext();
        meltedSegmentContext.setStartTime(segmentContext1.getStartTime());
        meltedSegmentContext.setEndTime(segmentContext2.getEndTime());
        if (segmentContext1.hasPreviousContext()) {
            meltedSegmentContext.setPreviousContext(segmentContext1.getPreviousContext());
            segmentContext1.getPreviousContext().setNextContext(meltedSegmentContext);
        }
        if (segmentContext2.hasNextContext()) {
            meltedSegmentContext.setNextContext(segmentContext2.getNextContext());
            segmentContext2.getNextContext().setPreviousContext(meltedSegmentContext);
        }

        segmentContextList.add(index, meltedSegmentContext);
        addPostProcessResult(meltedSegmentContext, classificationResult, source);

    }

    public static void meltThreeSegments(TrackingContext trackingContext, SegmentContext segmentContext1,
                                         SegmentContext segmentContext2, SegmentContext segmentContext3,
                                         ClassificationResult classificationResult, String source) {

        ArrayList<SegmentContext> segmentContextList = (ArrayList<SegmentContext>) trackingContext.getSegmentContextList();
        int index = segmentContextList.indexOf(segmentContext1);
        segmentContextList.remove(segmentContext1);
        segmentContextList.remove(segmentContext2);
        segmentContextList.remove(segmentContext3);


        SegmentContext meltedSegmentContext = new SegmentContext();
        meltedSegmentContext.setStartTime(segmentContext1.getStartTime());
        meltedSegmentContext.setEndTime(segmentContext3.getEndTime());

        if (segmentContext1.hasPreviousContext()) {
            meltedSegmentContext.setPreviousContext(segmentContext1.getPreviousContext());
            segmentContext1.getPreviousContext().setNextContext(meltedSegmentContext);
        }
        if (segmentContext3.hasNextContext()) {
            meltedSegmentContext.setNextContext(segmentContext3.getNextContext());
            segmentContext3.getNextContext().setPreviousContext(meltedSegmentContext);
        }

        segmentContextList.add(index, meltedSegmentContext);
        addPostProcessResult(meltedSegmentContext, classificationResult, source);
    }

    public static List<IGpsPoint> getCoordinatesBetween(TrackingContext trackingContext, int startIndex, LocalDateTime end) {
        List<IGpsPoint> list = trackingContext.getData("filtered_gps_input");

        List<IGpsPoint> coords = new ArrayList<>();
        for (int i = startIndex; i < list.size(); i++) {
            IGpsPoint point = list.get(i);
            LocalDateTime time = point.getTime();

            if (time.isBefore(end) || time.isEqual(end)) {
                coords.add(point);
            }
            if (point.getTime().isAfter(end)) {
                break;
            }
        }
        return coords;
    }

    public static List<IGpsPoint> getCoordinatesBetweenInterval(TrackingContext trackingContext, int startIndex, Duration intervall) {
        List<IGpsPoint> list = trackingContext.getData("filtered_gps_input");

        List<IGpsPoint> coords = new ArrayList<>();
        LocalDateTime startTime = list.get(startIndex).getTime();
        LocalDateTime minTime = startTime.minus(intervall);
        LocalDateTime maxTime = startTime.plus(intervall);
        for (int i = startIndex; i > 0; i--) {
            IGpsPoint point = list.get(i);
            LocalDateTime time = point.getTime();

            if (time.isAfter(minTime)) {
                coords.add(point);
            }
            if (time.isBefore(minTime)) {
                break;
            }
        }

        for (int i = startIndex; i < list.size(); i++) {
            IGpsPoint point = list.get(i);
            LocalDateTime time = point.getTime();
            if (time.isBefore(maxTime)) {
                coords.add(point);
            }
            if (time.isAfter(maxTime)) {
                break;
            }
        }
        coords.sort(Comparator.comparing(IGpsPoint::getTime));
        return coords;
    }

    public static HashMap<BackgroundGeolocationActivity, ActivityEvaluationStats> getBackgroundGeolocationActivityMap() {
        HashMap<BackgroundGeolocationActivity, ActivityEvaluationStats> activityMap = new HashMap<>();
        for (BackgroundGeolocationActivity activity : BackgroundGeolocationActivity.values()) {
            if (!activityMap.containsKey(activity)) {
                ActivityEvaluationStats stats = new ActivityEvaluationStats(activity);
                activityMap.put(activity, stats);
            }
        }
        return activityMap;
    }
}
