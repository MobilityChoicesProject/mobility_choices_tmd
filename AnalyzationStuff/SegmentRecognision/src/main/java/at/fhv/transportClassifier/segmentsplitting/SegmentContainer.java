package at.fhv.transportClassifier.segmentsplitting;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 03.05.2017.
 */
public class SegmentContainer {

  private List<Segment> segmentList =new LinkedList<>();


  public SegmentContainer(LocalDateTime startTime, LocalDateTime endtime){
    Segment segment = new Segment();
    segment.setStartTime(startTime);
    segment.setEndTime(endtime);
    segment.setPreType(SegmentPreType.NotClassifiedYet);
    segmentList.add(segment);
  }


  public void changeSegment(Segment oldSegment, List<Segment> newSegments){
    Iterator<Segment> iterator = segmentList.iterator();

    int index = segmentList.indexOf(oldSegment);
    segmentList.remove(index);

    LocalDateTime startTime = oldSegment.getStartTime();
    LocalDateTime endTime = oldSegment.getEndTime();

    for (Segment newSegment : newSegments) {
      LocalDateTime currentTime = newSegment.getStartTime();
      Duration diff = Duration.between(startTime, currentTime);
      if(diff.toMillis() == 0){
        // do nothing
      }else{
        Segment segment =createNewUnClassifiedSegment(startTime, currentTime);
        segmentList.add(index,segment);
        index++;
      }

      segmentList.add(index,newSegment);
      startTime = newSegment.getEndTime();
      index++;
    }

    Duration diff = Duration.between(startTime, endTime);
    if(diff.toMillis() == 0){
      // do nothing
    }else{
      Segment segment =createNewUnClassifiedSegment(startTime, endTime);
      segmentList.add(index,segment);
      index++;
    }


  }

  protected Segment createNewUnClassifiedSegment(LocalDateTime startTime, LocalDateTime endTime) {
    Segment segment = new Segment();
    segment.setStartTime(startTime);
    segment.setEndTime( endTime);
    segment.setPreType(SegmentPreType.NotClassifiedYet);
    return segment;
  }


  public List<Segment> getSegmentList(){
    return segmentList;
  }









}
