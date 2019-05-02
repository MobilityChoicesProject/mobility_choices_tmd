package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.postProcessing.ActivityEvaluationStats;
import at.fhv.tmd.postProcessing.PostprocessHelper;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivity;
import at.fhv.transportdetector.trackingtypes.BackgroundGeolocationActivityMapper;
import at.fhv.transportdetector.trackingtypes.TransportType;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

public class EvaluateNonMotorized implements PostprocessTask {

    private static String source = "EvaluateNonMotorized";

    private static final int MIN_SEGMENT_DURATION = 500;
    private static final double MIN_ACTIVITY_PERCENTAGE = 0.1;

    @Override
    public int getPriorityLevel() {
        return 110;
    }

    @Override
    public void process(TrackingContext trackingContext) {
        List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

        int startIndex = 0;
        int size = segmentContextList.size();
        for (int i = 0; i < size; i++) {
            SegmentContext currentSegment = segmentContextList.get(i);
            TransportType currentTransporttype = PostprocessHelper.getTransportType(currentSegment);
            if (currentTransporttype != TransportType.OTHER && currentTransporttype != TransportType.BIKE)
                continue;


            Duration duration = Duration.between(currentSegment.getStartTime(), currentSegment.getEndTime());

            LocalDateTime endTime = currentSegment.getEndTime();

            Map<BackgroundGeolocationActivity, ActivityEvaluationStats> statisticsMap = PostprocessHelper.getBackgroundGeolocationActivityMap();

            List<IGpsPoint> coords = PostprocessHelper.getCoordinatesBetween(trackingContext, startIndex, endTime);
            startIndex += coords.size();

            for (IGpsPoint point : coords) {
                ActivityEvaluationStats stats = statisticsMap.get(point.getActivity());
                stats.increaseCount();
                stats.addConfidence(point.getConfidence());
            }

            List<BackgroundGeolocationActivity> activities = BackgroundGeolocationActivityMapper.getBackgroundGeolocationActivity(currentTransporttype);
            double sum = 0.0;
            for (BackgroundGeolocationActivity activity : activities) {
                sum += statisticsMap.get(activity).getCount();
            }
            if (sum / coords.size() < MIN_ACTIVITY_PERCENTAGE && duration.getSeconds() < MIN_SEGMENT_DURATION) {
                //merge Segment
                SegmentContext previousSegment = currentSegment.getPreviousContext();
                SegmentContext nextSegment = currentSegment.getNextContext();

                Duration previousDuration = (previousSegment != null) ?
                        Duration.between(previousSegment.getStartTime(), previousSegment.getEndTime()) : Duration.ofSeconds(0);
                Duration nextDuration = (nextSegment != null) ?
                        Duration.between(nextSegment.getStartTime(), nextSegment.getEndTime()) : Duration.ofSeconds(0);

                if (previousSegment != null && previousDuration.getSeconds() > nextDuration.getSeconds()) {
                    //merge with previous
                    ClassificationResult classificationResult = PostprocessHelper.getClassificationResult(previousSegment);
                    PostprocessHelper.meltTwoSegments(trackingContext, previousSegment, currentSegment, classificationResult, source + "_meltWithPrevious");
                    i--;
                } else if (nextSegment != null) {
                    //merge with next
                    ClassificationResult classificationResult = PostprocessHelper.getClassificationResult(nextSegment);
                    PostprocessHelper.meltTwoSegments(trackingContext, currentSegment, nextSegment, classificationResult, source + "_meltWithNext");
                    i--;
                }
                size = segmentContextList.size();
            }
        }
    }
}
