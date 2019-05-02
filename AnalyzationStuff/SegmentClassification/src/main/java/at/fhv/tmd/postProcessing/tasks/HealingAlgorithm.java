package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.transportdetector.trackingtypes.TransportType;
import sun.rmi.transport.Transport;

import java.time.Duration;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class HealingAlgorithm implements PostprocessTask {
    private static String source = "HealingAlgorithm";

    @Override
    public int getPriorityLevel() {
        return 2200;
    }

    @Override
    public void process(TrackingContext trackingContext) {
        List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

        for (int i = 0; i < segmentContextList.size(); i++) {
            SegmentContext currentSegment = segmentContextList.get(i);
            TransportType currentTransporttype = PostprocessHelper.getTransportType(currentSegment);

            //find first walking segment
            while (i < segmentContextList.size()-1 && PostprocessHelper.getTransportType(currentSegment) != TransportType.OTHER) {
                i++;
                currentSegment = segmentContextList.get(i);
            }
            i++;
            if (i < segmentContextList.size()) {
                currentSegment = segmentContextList.get(i);
            }

            List<SegmentContext> segmentsBetweenWalking = new ArrayList<>();
            HashMap<TransportType, Long> transportTypeDurationMap = new HashMap<>();
            //find next walking segment
            while (i < segmentContextList.size() - 1 && PostprocessHelper.getTransportType(currentSegment) != TransportType.OTHER) {


                TransportType transportType = PostprocessHelper.getTransportType(currentSegment);
                long duration = Duration.between(currentSegment.getStartTime(), currentSegment.getEndTime()).getSeconds();
                if (!transportTypeDurationMap.containsKey(transportType)) {
                    transportTypeDurationMap.put(transportType, 0L);
                }
                transportTypeDurationMap.put(transportType, transportTypeDurationMap.get(transportType) + duration);
                segmentsBetweenWalking.add(currentSegment);
                i++;
                if (i < segmentContextList.size())
                    currentSegment = segmentContextList.get(i);
                else
                    break;
            }


            //remove all stationary segments
            int size = segmentsBetweenWalking.size();
            for (int j = size - 1; j >= 0; j--) {
                if (PostprocessHelper.getTransportType(segmentsBetweenWalking.get(j)) == TransportType.STATIONARY) {
                    segmentsBetweenWalking.remove(j);
                }
            }
            //get the transporttype with the longest duration
            Tuple<TransportType, Long> highestDurationType = null;
            for (TransportType type : transportTypeDurationMap.keySet()) {
                long duration = transportTypeDurationMap.get(type);
                if (highestDurationType == null) {
                    highestDurationType = new Tuple<>(type, duration);
                } else if (highestDurationType.getItem2() < duration) {
                    highestDurationType = new Tuple<>(type, duration);
                }
            }

            if (transportTypeDurationMap.size() > 1) {
                List<Tuple<TransportType, Double>> tuples = new ArrayList<>();
                List<TransportType> transportTypes = PostprocessHelper.transportTypesAscending;
                for (TransportType transportType : transportTypes) {
                    if (transportType != TransportType.WALK && transportType != TransportType.OTHER) {
                        if (transportType == highestDurationType.getItem1()) {
                            tuples.add(new Tuple<>(transportType, 1.0));
                        } else {
                            tuples.add(new Tuple<>(transportType, 0.0));
                        }
                    }
                }

                segmentsBetweenWalking.forEach(s -> PostprocessHelper.addPostProcessResult(s, new ClassificationResult(tuples), source));
            }
        }
    }
}
