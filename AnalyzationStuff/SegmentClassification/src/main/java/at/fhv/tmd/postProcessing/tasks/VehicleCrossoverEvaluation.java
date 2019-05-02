package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.postProcessing.*;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportdetector.trackingtypes.TransportType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class VehicleCrossoverEvaluation implements PostprocessTask, ConfigServiceUpdateable {

    private static String source = "VehicleCrossoverEvaluation";
    private static double VEHICLE_CROSSOVER_THRESHOLD;
    private HashMap<TransportType, HashMap<TransportType, Double>> crossoverProbabilities;
    private ConfigService configService;


    public VehicleCrossoverEvaluation(ConfigService configService) {
        this.configService = configService;
        updateConfigSettings();
    }

    @Override
    public int getPriorityLevel() {
        return 2100;
    }

    @Override
    public void process(TrackingContext trackingContext) {
        List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

        boolean hasChanged;
        do {
            hasChanged = false;
            List<SegmentContext> baseSegments = getAllBaseSegments(segmentContextList);
            for (SegmentContext baseSegment : baseSegments) {
                SegmentContext previousSegment = baseSegment.getPreviousContext();
                SegmentContext nextSegment = baseSegment.getNextContext();

                if (previousSegment != null) {
                    hasChanged = reevaluateProbabilities(previousSegment, baseSegment) || hasChanged;
                }

                if (nextSegment != null) {
                    hasChanged = reevaluateProbabilities(nextSegment, baseSegment) || hasChanged;
                }
            }
        } while (hasChanged);
    }

    private boolean reevaluateProbabilities(SegmentContext segment, SegmentContext baseSegment) {
        if (!isLikliestProbabilityOverThreshold(segment)) {
            PostprocessClassifications postprocessClassifications = segment.getData(SegmentContext.POST_PROCESS_RESULT);
            PostProcessClassificationResult postProcessClassificationResult =
                    (postprocessClassifications == null) ? null : postprocessClassifications.get(postprocessClassifications.size() - 1);
            //each segment is only once reevaluated
            if (postProcessClassificationResult != null && postProcessClassificationResult.getSource().equals(source)) {
                return false;
            }

            List<Tuple<TransportType, Double>> currentProbabilities;
            if (postProcessClassificationResult == null) {
                currentProbabilities = ((ClassificationResult) segment.getData(SegmentContext.CLASSIFICATION_RESULT)).getResults();
            } else {
                currentProbabilities = postProcessClassificationResult.getClassifiedResult().getResults();
            }

            List<Tuple<TransportType, Double>> result = new ArrayList<>();
            TransportType baseSegmentType = PostprocessHelper.getTransportType(baseSegment);
            double sumProbabilities = 0;
            for (Tuple<TransportType, Double> probabilityTuple : currentProbabilities) {
                TransportType type = probabilityTuple.getItem1();
                double crossoverProbability;
                if (baseSegment.getEndTime().isBefore(segment.getStartTime())) {
                    crossoverProbability = 1.0 + getCrossoverProbability(baseSegmentType, type);
                } else {
                    crossoverProbability = 1.0 + getCrossoverProbability(type, baseSegmentType);
                }
                double newProbability = probabilityTuple.getItem2() * crossoverProbability;

                sumProbabilities += newProbability;
                result.add(new Tuple<>(type, newProbability));
            }

            //normalize
            for (Tuple<TransportType, Double> tuple : result) {
                tuple.setItem2(tuple.getItem2() / sumProbabilities);
            }

            PostprocessHelper.addPostProcessResult(segment, new ClassificationResult(result), source);
            return true;
        }
        return false;
    }

    private double getCrossoverProbability(TransportType from, TransportType to) {
        return crossoverProbabilities.get(from).get(to);
    }

    private boolean isLikliestProbabilityOverThreshold(SegmentContext segment) {
        ClassificationResult classificationResult = PostprocessHelper.getClassificationResult(segment);

        TransportType mostLikliest = classificationResult.getMostLikeliestResult();
        double likelyhood = classificationResult.getLikelyHoodFor(mostLikliest);
        if (likelyhood > VEHICLE_CROSSOVER_THRESHOLD) {
            return true;
        }
        return false;
    }

    private List<SegmentContext> getAllBaseSegments(List<SegmentContext> segments) {
        List<SegmentContext> baseSegments = new ArrayList<>();
        for (SegmentContext segment : segments) {
            if (isLikliestProbabilityOverThreshold(segment)) {
                baseSegments.add(segment);
            }
        }
        return baseSegments;
    }

    @Override
    public void updateConfigService(ConfigService configService) {
        this.configService = configService;
        updateConfigSettings();
    }

    private void updateConfigSettings() {
        crossoverProbabilities = new HashMap<>();

        VEHICLE_CROSSOVER_THRESHOLD = configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_threshold);
        // car to...
        crossoverProbabilities.put(TransportType.CAR, new HashMap<>());
        crossoverProbabilities.get(TransportType.CAR).put(TransportType.CAR, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_car_car));
        crossoverProbabilities.get(TransportType.CAR).put(TransportType.BIKE, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_car_bike));
        crossoverProbabilities.get(TransportType.CAR).put(TransportType.BUS, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_car_bus));
        crossoverProbabilities.get(TransportType.CAR).put(TransportType.TRAIN, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_car_train));
        crossoverProbabilities.get(TransportType.CAR).put(TransportType.OTHER, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_car_other));
        crossoverProbabilities.get(TransportType.CAR).put(TransportType.STATIONARY, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_car_stationary));
        // bike to...
        crossoverProbabilities.put(TransportType.BIKE, new HashMap<>());
        crossoverProbabilities.get(TransportType.BIKE).put(TransportType.BIKE, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bike_bike));
        crossoverProbabilities.get(TransportType.BIKE).put(TransportType.CAR, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bike_car));
        crossoverProbabilities.get(TransportType.BIKE).put(TransportType.BUS, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bike_bus));
        crossoverProbabilities.get(TransportType.BIKE).put(TransportType.TRAIN, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bike_train));
        crossoverProbabilities.get(TransportType.BIKE).put(TransportType.OTHER, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bike_other));
        crossoverProbabilities.get(TransportType.BIKE).put(TransportType.STATIONARY, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bike_stationary));
        // bus to...
        crossoverProbabilities.put(TransportType.BUS, new HashMap<>());
        crossoverProbabilities.get(TransportType.BUS).put(TransportType.BUS, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bus_bus));
        crossoverProbabilities.get(TransportType.BUS).put(TransportType.CAR, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bus_car));
        crossoverProbabilities.get(TransportType.BUS).put(TransportType.BIKE, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bus_bike));
        crossoverProbabilities.get(TransportType.BUS).put(TransportType.TRAIN, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bus_train));
        crossoverProbabilities.get(TransportType.BUS).put(TransportType.OTHER, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bus_other));
        crossoverProbabilities.get(TransportType.BUS).put(TransportType.STATIONARY, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_bus_stationary));
        // train to...
        crossoverProbabilities.put(TransportType.TRAIN, new HashMap<>());
        crossoverProbabilities.get(TransportType.TRAIN).put(TransportType.TRAIN, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_train_train));
        crossoverProbabilities.get(TransportType.TRAIN).put(TransportType.CAR, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_train_car));
        crossoverProbabilities.get(TransportType.TRAIN).put(TransportType.BIKE, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_train_bike));
        crossoverProbabilities.get(TransportType.TRAIN).put(TransportType.BUS, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_train_bus));
        crossoverProbabilities.get(TransportType.TRAIN).put(TransportType.OTHER, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_train_other));
        crossoverProbabilities.get(TransportType.TRAIN).put(TransportType.STATIONARY, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_train_stationary));
        // other to...
        crossoverProbabilities.put(TransportType.OTHER, new HashMap<>());
        crossoverProbabilities.get(TransportType.OTHER).put(TransportType.OTHER, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_other_other));
        crossoverProbabilities.get(TransportType.OTHER).put(TransportType.CAR, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_other_car));
        crossoverProbabilities.get(TransportType.OTHER).put(TransportType.BIKE, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_other_bike));
        crossoverProbabilities.get(TransportType.OTHER).put(TransportType.BUS, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_other_bus));
        crossoverProbabilities.get(TransportType.OTHER).put(TransportType.TRAIN, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_other_train));
        crossoverProbabilities.get(TransportType.OTHER).put(TransportType.STATIONARY, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_other_stationary));
        // stationary to...
        crossoverProbabilities.put(TransportType.STATIONARY, new HashMap<>());
        crossoverProbabilities.get(TransportType.STATIONARY).put(TransportType.STATIONARY, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_stationary_stationary));
        crossoverProbabilities.get(TransportType.STATIONARY).put(TransportType.CAR, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_stationary_car));
        crossoverProbabilities.get(TransportType.STATIONARY).put(TransportType.BIKE, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_stationary_bike));
        crossoverProbabilities.get(TransportType.STATIONARY).put(TransportType.BUS, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_stationary_bus));
        crossoverProbabilities.get(TransportType.STATIONARY).put(TransportType.TRAIN, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_stationary_train));
        crossoverProbabilities.get(TransportType.STATIONARY).put(TransportType.OTHER, configService.getValue(ConfigServiceDefaultCache.pp_vehicleCrossoverProb_stationary_other));
    }
}
