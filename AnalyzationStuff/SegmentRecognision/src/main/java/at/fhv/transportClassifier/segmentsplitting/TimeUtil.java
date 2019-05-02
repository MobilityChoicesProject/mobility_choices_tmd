package at.fhv.transportClassifier.segmentsplitting;

import java.time.LocalDateTime;

/**
 * Created by Johannes on 06.05.2017.
 */
public class TimeUtil {


  public static LocalDateTime removeMs(LocalDateTime time){
    return LocalDateTime.of(time.getYear(),time.getMonth(),time.getDayOfMonth(),time.getHour(),time.getMinute(),time.getSecond());



  }

}
