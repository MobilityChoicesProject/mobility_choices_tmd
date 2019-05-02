package at.fhv.tmd.postProcessing.tasks;

import at.fhv.context.SegmentContext;
import at.fhv.context.TrackingContext;
import at.fhv.tmd.postProcessing.PostprocessTask;
import at.fhv.transportClassifier.segmentsplitting.SegmentPreType;
import java.time.Duration;
import java.util.List;

/**
 * Created by Johannes on 23.07.2017.
 */
public class RemoveNotClassifiedYetSegments implements PostprocessTask {

  @Override
  public int getPriorityLevel() {
    return -1;
  }

  @Override
  public void process(TrackingContext trackingContext) {
    List<SegmentContext> segmentContextList = trackingContext.getSegmentContextList();

    int size = segmentContextList.size();
    for(int i = 0; i <size ;i++){

      SegmentContext segmentContext = segmentContextList.get(i);
      if(segmentContext.hasData(SegmentContext.PRE_TYPE)){
        SegmentPreType segmentPreType = segmentContext.getData(SegmentContext.PRE_TYPE);
        if(segmentPreType == SegmentPreType.NotClassifiedYet){

          SegmentContext nextSegmentContext = null;
          SegmentContext previousSegmentContext = null;

          Duration nextContextDuration = Duration.ofSeconds(0);
          Duration previousContextDuration = Duration.ofSeconds(0);

          if(segmentContext.hasNextContext()){
            SegmentContext nextContext = segmentContext.getNextContext();
            nextContextDuration = Duration.between(nextContext.getStartTime(),nextContext.getEndTime());
          }
          if(segmentContext.hasPreviousContext()){
            SegmentContext previousContext = segmentContext.getPreviousContext();
            previousContextDuration = Duration.between(previousContext.getStartTime(),previousContext.getEndTime());
          }
          boolean previousContextDurationIsLonger= previousContextDuration.compareTo(nextContextDuration)>0;
          if(previousContextDurationIsLonger){
            previousSegmentContext = segmentContext.getPreviousContext();
            previousSegmentContext.setEndTime(segmentContext.getEndTime());
            previousSegmentContext.setNextContext(segmentContext.getNextContext());
            if(segmentContext.hasNextContext()){
              segmentContext.getNextContext().setPreviousContext(previousSegmentContext);
            }
          }else{
            nextSegmentContext = segmentContext.getNextContext();
            nextSegmentContext.setStartTime(segmentContext.getStartTime());
            nextSegmentContext.setPreviousContext(segmentContext.getPreviousContext());
            if(segmentContext.hasPreviousContext()){
              segmentContext.getPreviousContext().setNextContext(nextSegmentContext);
            }
          }

          segmentContextList.remove(segmentContext);
          size = size-1;

        }
      }
    }

  }
}
