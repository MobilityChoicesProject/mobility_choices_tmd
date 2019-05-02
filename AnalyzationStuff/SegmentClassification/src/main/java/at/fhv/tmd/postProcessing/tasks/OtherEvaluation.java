package at.fhv.tmd.postProcessing.tasks;

import static at.fhv.tmd.postProcessing.PostprocessHelper.addPostProcessResult;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import at.fhv.transportdetector.trackingtypes.TransportType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 21.07.2017.
 */
public class OtherEvaluation implements PostprocessTask {

    private static String source = "OtherEvaluation";

    @Override
    public int getPriorityLevel() {
        return 10;
}

    @Override
    public void process(TrackingContext trackingContext) {

        List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

        for (SegmentContext segmentContext : segmentContextList) {
            SegmentPreType preType = segmentContext.getData(SegmentContext.PRE_TYPE);

            if (preType == SegmentPreType.stationaryCluster || preType == SegmentPreType.stationarySignalShortage) {

                LocalDateTime startTime = segmentContext.getStartTime();
                LocalDateTime endTime = segmentContext.getEndTime();
                Duration duration = Duration.between(startTime, endTime);


                List<Tuple<TransportType, Double>> tuples = new ArrayList<>();
                tuples.add(new Tuple<>(TransportType.STATIONARY, 1.0));

                addPostProcessResult(segmentContext, new ClassificationResult(tuples), source);
            }

        }


    }


}
