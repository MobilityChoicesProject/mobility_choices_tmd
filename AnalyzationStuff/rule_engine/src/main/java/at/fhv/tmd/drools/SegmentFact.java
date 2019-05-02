package at.fhv.tmd.drools;

import at.fhv.transportdetector.trackingtypes.TransportType;
import java.util.List;

/**
 * Created by Johannes on 08.06.2017.
 */
public class SegmentFact {


  public List<Fact> factList;
  private SegmentFact previous;
  private SegmentFact next;
  private TransportType likeliestTransportType;
  private double likeliestTransportTypeProbability;

  public List<Fact> getFactList() {
    return factList;
  }

  public void setFactList(List<Fact> factList) {
    this.factList = factList;
  }

  public SegmentFact getPrevious() {
    return previous;
  }

  public void setPrevious(SegmentFact previous) {
    this.previous = previous;
  }

  public SegmentFact getNext() {
    return next;
  }

  public void setNext(SegmentFact next) {
    this.next = next;
  }
}
