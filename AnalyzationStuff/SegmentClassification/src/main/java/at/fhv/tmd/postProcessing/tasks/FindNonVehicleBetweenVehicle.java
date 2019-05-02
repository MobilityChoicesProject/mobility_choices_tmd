package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.featureCalculation.FeatureCalculationService;
import at.fhv.tmd.postProcessing.ActivityEvaluationStats;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.tmd.segmentClassification.classifier.ClassifierNew;
import at.fhv.tmd.smoothing.CoordinateInterpolator;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivity;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivityMapper;
import at.fhv.transportdetector.trackingtypes.TransportType;
import sun.rmi.transport.Transport;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.*;

import static at.fhv.context.TrackingContext.FILTERED_GPS_INPUT;

public class FindNonVehicleBetweenVehicle implements PostprocessTask {

    private static final Duration TIME_INTERVALL = Duration.ofSeconds(15);
    private static final double ACTIVITY_PERCENTAGE_THRESHOLD = 0.6;
    private static String source = "FindNonVehicleBetweenVehicle";

    private ConfigService _configService;
    private Classifier _classifier;
    private FeatureCalculationService _featureCalculationService;

    public FindNonVehicleBetweenVehicle(ConfigService configService, Classifier classifier, FeatureCalculationService featureCalculationService) {
        _configService = configService;
        _classifier = classifier;
        _featureCalculationService = featureCalculationService;
    }

    @Override
    public int getPriorityLevel() {
        return 120;
    }

    @Override
    public void process(TrackingContext trackingContext) {

        List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

        Map<Integer, SegmentContext> indexMap = new HashMap<>();

        List<IGpsPoint> gpsPoints = trackingContext.getData(FILTERED_GPS_INPUT);
        int k = 0;
        for (SegmentContext currentSegment : segmentContextList) {
            for (int j = k; j < gpsPoints.size(); j++) {
                IGpsPoint point = gpsPoints.get(j);
                if (point.getTime().compareTo(currentSegment.getStartTime()) >= 0) {
                    indexMap.put(j, currentSegment);
                    k = j;
                    break;
                }
            }
        }


        for (Integer startIndex : indexMap.keySet()) {
            if (checkForNonVehicleBetweenSegments(trackingContext, startIndex)) {
                List<IGpsPoint> nonVehicleCoords = findGpsPointsOfNonVehicleSegment(trackingContext, startIndex);
                if (!nonVehicleCoords.isEmpty()) {
                    SegmentContext previousSegment = indexMap.get(startIndex).getPreviousContext();
                    SegmentContext nextSegment = indexMap.get(startIndex);

                    if (previousSegment != null && nextSegment != null) {
                        TransportType previousTransporttype = PostprocessHelper.getTransportType(previousSegment);
                        TransportType nextTransporttype = PostprocessHelper.getTransportType(nextSegment);

                        if (nextTransporttype != null && previousTransporttype != null
                                && isVehicle(previousTransporttype) && isVehicle(nextTransporttype)
                                && previousTransporttype != nextTransporttype) {


                            SegmentContext nonVehicleSegment = new SegmentContext();
                            nonVehicleSegment.setStartTime(nonVehicleCoords.get(0).getTime());
                            nonVehicleSegment.setEndTime(nonVehicleCoords.get(nonVehicleCoords.size() - 1).getTime());
                            nonVehicleSegment.setPreviousContext(previousSegment);
                            nonVehicleSegment.setNextContext(nextSegment);

                            //previous segment
                            previousSegment.setEndTime(nonVehicleSegment.getStartTime());
                            previousSegment.setNextContext(nonVehicleSegment);

                            //nextSegment
                            if (nextSegment != null) {
                                nextSegment.setStartTime(nonVehicleSegment.getEndTime());
                                nextSegment.setPreviousContext(nonVehicleSegment);
                            }

                            List<Tuple<TransportType, Double>> tuples = new ArrayList<>();
                            tuples.add(new Tuple<>(TransportType.CAR, 0.0));
                            tuples.add(new Tuple<>(TransportType.BUS, 0.0));
                            tuples.add(new Tuple<>(TransportType.TRAIN, 0.0));
                            tuples.add(new Tuple<>(TransportType.BIKE, 0.0));
                            tuples.add(new Tuple<>(TransportType.OTHER, 1.0));
                            tuples.add(new Tuple<>(TransportType.STATIONARY, 0.0));
                            ClassificationResult newResult = new ClassificationResult(tuples);
                            PostprocessHelper.addPostProcessResult(nonVehicleSegment, newResult, source);

                            segmentContextList.add(nonVehicleSegment);
                        }
                    }
                }
            }
        }
        segmentContextList.sort(Comparator.comparing(SegmentContext::getStartTime));
    }

    private boolean isVehicle(TransportType transportType) {
        switch (transportType) {
            case CAR:
            case BIKE:
            case BUS:
            case TRAIN:
            case STATIONARY:
                return true;
            case WALK:
            case OTHER:
            default:
                return false;
        }
    }

    private List<IGpsPoint> findGpsPointsOfNonVehicleSegment(TrackingContext trackingContext, Integer startIndex) {
        List<IGpsPoint> allCoords = trackingContext.getData(FILTERED_GPS_INPUT);

        List<IGpsPoint> onFootCoords = new ArrayList<>();
        //search non vehicle points before
        int i = startIndex;
        IGpsPoint currentPoint = allCoords.get(i);
        while (i - 1 > 0 && currentPoint.getActivity().equals(BackgroundGeolocationActivity.ON_FOOT)) {
            onFootCoords.add(currentPoint);
            i--;
            currentPoint = allCoords.get(i);
        }

        //search non vehicle points after
        i = startIndex;
        currentPoint = allCoords.get(i);
        while (i + 1 < allCoords.size() && currentPoint.getActivity().equals(BackgroundGeolocationActivity.ON_FOOT)) {
            onFootCoords.add(currentPoint);
            i++;
            currentPoint = allCoords.get(i);
        }
        onFootCoords.sort((Comparator.comparing(IGpsPoint::getTime)));

        return onFootCoords;
    }

    private boolean checkForNonVehicleBetweenSegments(TrackingContext trackingContext, int startIndex) {
        List<IGpsPoint> coords = PostprocessHelper.getCoordinatesBetweenInterval(trackingContext, startIndex, TIME_INTERVALL);

        Map<BackgroundGeolocationActivity, ActivityEvaluationStats> statisticsMap = PostprocessHelper.getBackgroundGeolocationActivityMap();
        for (IGpsPoint point : coords) {
            ActivityEvaluationStats stats = statisticsMap.get(point.getActivity());
            stats.increaseCount();
            stats.addConfidence(point.getConfidence());
        }

        List<BackgroundGeolocationActivity> activities = BackgroundGeolocationActivityMapper.getBackgroundGeolocationActivity(TransportType.OTHER);
        double sum = 0.0;
        for (BackgroundGeolocationActivity activity : activities) {
            sum += statisticsMap.get(activity).getCount();
        }
        return sum / coords.size() > ACTIVITY_PERCENTAGE_THRESHOLD;
    }
}
