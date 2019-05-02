package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.postProcessing.ActivityEvaluationStats;
import at.fhv.tmd.postProcessing.ConfigServiceUpdateable;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.processFlow.TransportTypeProbability;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivity;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivityMapper;
import at.fhv.transportdetector.trackingtypes.TransportType;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityEvaluation implements PostprocessTask, ConfigServiceUpdateable {

    private static String source = "ActivityEvaluation";
    private double _backgroundGeolocationIterations;
    private ConfigService _configService;


    public ActivityEvaluation(ConfigService configService) {
        _configService = configService;
        updateConfigSettings();
    }

    @Override
    public int getPriorityLevel() {
        return 2000;
    }

    @Override
    public void process(TrackingContext trackingContext) {
        int startIndex = 0;
        for (SegmentContext segmentContext : trackingContext.getSegmentContextList()) {
            if (PostprocessHelper.getTransportType(segmentContext) == TransportType.STATIONARY) {
                continue;
            }

            LocalDateTime end = segmentContext.getEndTime();

            Map<BackgroundGeolocationActivity, ActivityEvaluationStats> statisticsMap = PostprocessHelper.getBackgroundGeolocationActivityMap();

            List<IGpsPoint> coords = PostprocessHelper.getCoordinatesBetween(trackingContext, startIndex, end);
            startIndex += coords.size();

            for (IGpsPoint point : coords) {
                ActivityEvaluationStats stats = statisticsMap.get(point.getActivity());
                stats.increaseCount();
                stats.addConfidence(point.getConfidence());
            }

            Map<BackgroundGeolocationActivity, Double> multiplyingFactors = new HashMap<>();
            for (BackgroundGeolocationActivity activity : BackgroundGeolocationActivity.values()) {
                ActivityEvaluationStats stats = statisticsMap.get(activity);
                multiplyingFactors.put(activity, Math.pow(1 + (stats.getCount() / (double) coords.size() * stats.getAverageConfidence()), _backgroundGeolocationIterations));
            }

            List<TransportTypeProbability> currentProbabilities = PostprocessHelper.getTransportTypeProbabilities(segmentContext);
            Map<TransportType, Double> unnormalizedProbabilities = new HashMap<>();

            if (currentProbabilities == null) {
                return; // TODO maybe only use background geo location data?
            }

            for (TransportTypeProbability prob : currentProbabilities) {
                TransportType type = prob.getTransportType();
                List<BackgroundGeolocationActivity> activities = BackgroundGeolocationActivityMapper.getBackgroundGeolocationActivity(type);


                for (BackgroundGeolocationActivity activity : activities) {
                    double multFactor = multiplyingFactors.get(activity);
                    Double currUnnormalizedProb = unnormalizedProbabilities.get(type);
                    double newProb = 0;
                    if (currUnnormalizedProb == null) {
                        newProb = prob.getProbability() * multFactor;
                    } else {
                        newProb = currUnnormalizedProb * multFactor;
                    }
                    unnormalizedProbabilities.put(type, newProb);
                }
            }

            double sumProbabilities = 0;
            for (Double probs : unnormalizedProbabilities.values()) {
                sumProbabilities += probs;
            }

            List<Tuple<TransportType, Double>> tuples = new ArrayList<>();
            tuples.add(new Tuple<>(TransportType.CAR, unnormalizedProbabilities.get(TransportType.CAR) / sumProbabilities));
            tuples.add(new Tuple<>(TransportType.BUS, unnormalizedProbabilities.get(TransportType.BUS) / sumProbabilities));
            tuples.add(new Tuple<>(TransportType.TRAIN, unnormalizedProbabilities.get(TransportType.TRAIN) / sumProbabilities));
            tuples.add(new Tuple<>(TransportType.BIKE, unnormalizedProbabilities.get(TransportType.BIKE) / sumProbabilities));
            tuples.add(new Tuple<>(TransportType.OTHER, unnormalizedProbabilities.get(TransportType.OTHER) / sumProbabilities));

            PostprocessHelper.addPostProcessResult(segmentContext, new ClassificationResult(tuples), source);
        }
    }



    @Override
    public void updateConfigService(ConfigService configService) {
        _configService = configService;
        updateConfigSettings();
    }

    private void updateConfigSettings() {
        _backgroundGeolocationIterations = _configService.getValue(ConfigServiceDefaultCache.pp_backgroundGeolocation_iterations);
    }
}
