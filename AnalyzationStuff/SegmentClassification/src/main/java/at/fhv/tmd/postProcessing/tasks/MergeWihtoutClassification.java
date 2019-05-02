package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.processFlow.TransportTypeProbability;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.transportdetector.trackingtypes.TransportType;

import java.util.*;

public class MergeWihtoutClassification implements PostprocessTask {
    private static String source = "MergeWithoutClassification";

    @Override
    public int getPriorityLevel() {
        return 5000;
    }

    @Override
    public void process(TrackingContext trackingContext) {

        List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();
        int size = segmentContextList.size();
        for (int i = 0; i < size; i++) {
            SegmentContext currentSegment = segmentContextList.get(i);
            TransportType currentTransportType = PostprocessHelper.getTransportType(currentSegment);


            int j = i;
            LinkedList<SegmentContext> segmentsToMerge = new LinkedList<>();
            SegmentContext nextSegment = currentSegment.getNextContext();
            while (nextSegment != null && Objects.equals(currentTransportType, PostprocessHelper.getTransportType(nextSegment))) {
                segmentsToMerge.add(nextSegment);
                nextSegment = nextSegment.getNextContext();
                j++;
            }
            if (!segmentsToMerge.isEmpty()) {
                segmentsToMerge.add(currentSegment);
                SegmentContext mergeResult = mergeSegments(segmentsToMerge);
                for (int k = j; k >= i; k--) {
                    segmentContextList.remove(k);
                }
                segmentContextList.add(i, mergeResult);
            }
            size = segmentContextList.size();
        }
    }

    private SegmentContext mergeSegments(LinkedList<SegmentContext> segments) {
        SegmentContext result = new SegmentContext();

        segments.sort(Comparator.comparing(SegmentContext::getStartTime));

        result.setPreviousContext(segments.getFirst().getPreviousContext());
        if (result.hasPreviousContext()) {
            result.getPreviousContext().setNextContext(result);
        }
        result.setNextContext(segments.getLast().getNextContext());
        if (result.hasNextContext()) {
            result.getNextContext().setPreviousContext(result);
        }

        result.setStartTime(segments.getFirst().getStartTime());
        result.setEndTime(segments.getLast().getEndTime());

        HashMap<TransportType, Double> resultProbabilities = new HashMap<>();
        for (SegmentContext currentSegment : segments) {
            for (TransportTypeProbability transportTypeProbability : PostprocessHelper.getTransportTypeProbabilities(currentSegment)) {
                if (!resultProbabilities.containsKey(transportTypeProbability.getTransportType())) {
                    resultProbabilities.put(transportTypeProbability.getTransportType(), 0.0);
                }
                Double currentProbability = resultProbabilities.get(transportTypeProbability.getTransportType());
                Double newProbability = currentProbability + transportTypeProbability.getProbability() / segments.size();
                resultProbabilities.put(transportTypeProbability.getTransportType(), newProbability);
            }
        }
        List<Tuple<TransportType, Double>> tuples = new ArrayList<>();
        tuples.add(new Tuple<>(TransportType.CAR, resultProbabilities.get(TransportType.CAR)));
        tuples.add(new Tuple<>(TransportType.BUS, resultProbabilities.get(TransportType.BUS)));
        tuples.add(new Tuple<>(TransportType.TRAIN, resultProbabilities.get(TransportType.TRAIN)));
        tuples.add(new Tuple<>(TransportType.BIKE, resultProbabilities.get(TransportType.BIKE)));
        tuples.add(new Tuple<>(TransportType.OTHER, resultProbabilities.get(TransportType.OTHER)));
        tuples.add(new Tuple<>(TransportType.STATIONARY, resultProbabilities.get(TransportType.STATIONARY)));

        PostprocessHelper.addPostProcessResult(result, new ClassificationResult(tuples), source);

        return result;
    }
}
