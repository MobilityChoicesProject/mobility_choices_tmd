package at.fhv.transportClassifier.segmentsplitting;

/**
 * Created by Johannes on 03.05.2017.
 */
public enum SegmentPreType {
  NotClassifiedYet,
  WalkingSegment,
  NonWalkingSegment,
  stationarySignalShortage,
  movingSignalShortage,
  stationaryCluster,
  NonClassifiable // Segment which is during the start of the tracking and the first gps pointor after the last gps points and the end of the tracking




}
