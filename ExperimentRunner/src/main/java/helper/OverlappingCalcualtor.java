package helper;

import at.fhv.tmd.common.Distance;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportClassifier.common.BinaryCollectionSearcher;
import at.fhv.transportClassifier.common.CoordinateUtil;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 22.06.2017.
 */
public class OverlappingCalcualtor {




  public OverlappingResult calcOverlappings(List<Section> firstSections, List<Section> secondSections) {




    Duration totalDuration = Duration.ofSeconds(0);
    Distance totalDistance = new Distance(0);


    Duration totalOverlappingDuration = Duration.ofSeconds(0);
    Distance totalOverlappingDistance = new Distance(0);

    for (Section firstSection : firstSections) {
      String name = firstSection.getName();

      LocalDateTime startTime = firstSection.getStartTime();
      LocalDateTime endTime = firstSection.getEndTime();


      Duration segment1Duration = Duration.between(startTime,endTime);
      Distance segment1Distance = calcDistance(startTime,endTime,firstSection.getGpsPointList());

      totalDistance = totalDistance.plus(segment1Distance);
      totalDuration = totalDuration.plus(segment1Duration);

      Distance overlappingDistanceSum = new Distance(0);
      Duration overlappingDurationSum = Duration.ofSeconds(0);

      for (Section secondSection : secondSections) {
        LocalDateTime section2StartTime = secondSection.getStartTime();
        LocalDateTime section2EndTime = secondSection.getEndTime();

        String secondSectionName = secondSection.getName();
        if (name.equals(secondSectionName)) {

          boolean endBeforeOrAtStart =
              section2EndTime.isBefore(startTime) || section2EndTime.isEqual(startTime);
          boolean startsAfterOrAtEnd =
              section2StartTime.isAfter(endTime) || section2StartTime.isEqual(endTime);
          if (endBeforeOrAtStart || startsAfterOrAtEnd) {
            // irrelevant

          } else {

            LocalDateTime minRightTime, maxRightRime;

            if (section2StartTime.isBefore(startTime)) {
              minRightTime = startTime;
            } else {
              minRightTime = section2StartTime;
            }

            if (section2EndTime.isAfter(endTime)) {
              maxRightRime = endTime;
            } else {
              maxRightRime = section2EndTime;
            }

            Duration overlappingDuration = Duration.between(minRightTime, maxRightRime);
            overlappingDurationSum = overlappingDurationSum.plus(overlappingDuration);

            Distance distance = calcDistance(minRightTime, maxRightRime,
                firstSection.getGpsPointList());
            overlappingDistanceSum = overlappingDistanceSum.plus(distance);
          }


        }
      }
      totalOverlappingDuration = totalOverlappingDuration.plus(overlappingDurationSum);
      totalOverlappingDistance = totalOverlappingDistance.plus(overlappingDistanceSum);

    }



    OverlappingResult overlappingResult = new OverlappingResult(totalOverlappingDistance,totalOverlappingDuration,totalDistance,totalDuration);
     return overlappingResult;


  }


  BinaryCollectionSearcher<IGpsPoint,LocalDateTime> binaryCollectionSearcher = new BinaryCollectionSearcher();

    private Distance calcDistance(LocalDateTime minRightTime, LocalDateTime maxRightRime,
        List<? extends IGpsPoint> gpsPointList) {

      int startIndex= binaryCollectionSearcher.findClosest(gpsPointList,minRightTime,(item, localDateTime) -> item.getTime().compareTo(localDateTime));
      int endIndex= binaryCollectionSearcher.findClosest(gpsPointList,maxRightRime,(item, localDateTime) -> item.getTime().compareTo(localDateTime));

      Distance distanceSum = new Distance(0);
      IGpsPoint lastPoint = gpsPointList.get(startIndex);
      for(int i = startIndex+1;i <= endIndex;i++){

        IGpsPoint current = gpsPointList.get(i);
        Distance distance = CoordinateUtil.haversineDistance(current,lastPoint);
        distanceSum=distanceSum.plus(distance);

        lastPoint= current;
      }
      return distanceSum;
    }







}
