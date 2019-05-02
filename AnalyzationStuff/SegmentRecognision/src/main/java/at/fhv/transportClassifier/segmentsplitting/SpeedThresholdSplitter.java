package at.fhv.transportClassifier.segmentsplitting;

import at.fhv.tmd.common.Speed;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by Johannes on 20.04.2017.
 */
public class SpeedThresholdSplitter {




  public List<LocalDateTime> split(Tracking tracking){

    List<IExtendedGpsPoint> gpsPoints = tracking.getGpsPoints();

    Duration frame = Duration.ofSeconds(12);
    boolean firstPoint = true;
    double speedThreshold = 10;
    boolean belowThreshold = false;
    List<LocalDateTime> localDateTimes = new LinkedList<>();
    for(int i =0 ; i < gpsPoints.size();i++){
      IExtendedGpsPoint gpsPoint = gpsPoints.get(i);
      LocalDateTime mostAccurateTime = gpsPoint.getMostAccurateTime();
      LocalDateTime deadline = mostAccurateTime.plus(frame);

      for(int j = i; j< gpsPoints.size();j++){
        IExtendedGpsPoint gpsPoint1 = gpsPoints.get(j);
        LocalDateTime mostAccurateTime1 = gpsPoint1.getMostAccurateTime();

        if(deadline.isBefore(mostAccurateTime1)){
          Speed speed = CoordinateUtil.calcSpeedBetween1(gpsPoint, gpsPoint1);

          if(firstPoint ){
            firstPoint = false;
            belowThreshold = speed.getKmPerHour() < speedThreshold;
            localDateTimes.add(gpsPoint.getSensorTime());
          }else{

            if(speed.getKmPerHour() < speedThreshold){
              if(!belowThreshold){
                localDateTimes.add(gpsPoint.getSensorTime());
                belowThreshold = true;
              }
            }else{
              if(belowThreshold){
                localDateTimes.add(gpsPoint.getSensorTime());
                belowThreshold = false;
              }
            }


          }

          break;
        }
      }

    }

    return  localDateTimes;

  }




}
