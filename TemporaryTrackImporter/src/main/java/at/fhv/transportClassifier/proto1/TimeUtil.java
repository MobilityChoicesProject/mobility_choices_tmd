package at.fhv.transportClassifier.proto1;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Created by Johannes on 14.02.2017.
 */
public class TimeUtil {


   public static LocalDateTime convertToLocalDatetime(long utcTimestamp){
        LocalDateTime date =
                Instant.ofEpochMilli(utcTimestamp).atZone(ZoneId.of("UTC+01:00")).toLocalDateTime();
        return date;
    }

    public static LocalDateTime convertToLocalDatetimeOrNull(long utcTimestmap){
        if(utcTimestmap == 0){
            return null;
        }
        LocalDateTime date =
                Instant.ofEpochMilli(utcTimestmap).atZone(ZoneId.of("UTC+01:00")).toLocalDateTime();
        return date;
    }
}
