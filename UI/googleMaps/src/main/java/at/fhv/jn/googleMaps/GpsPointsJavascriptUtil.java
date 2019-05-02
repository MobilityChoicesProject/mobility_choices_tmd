package at.fhv.jn.googleMaps;

import java.util.List;
import java.util.Locale;

/**
 * Created by Johannes on 01.02.2017.
 */
public class GpsPointsJavascriptUtil {

    public static String ConvertToJson(List<DataPoint> dataPoints){
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (DataPoint dataPoint : dataPoints) {
            stringBuilder.append(" {lat: ");
            stringBuilder.append(ConvertToString(dataPoint.getLatitude()));
            stringBuilder.append(", lng: ");
            stringBuilder.append(ConvertToString(dataPoint.getLongitude()));
            stringBuilder.append("},");
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

    public static String ConvertToJson(DataPoint dataPoint){
        StringBuilder stringBuilder = new StringBuilder();

            stringBuilder.append(" {lat: ");
            stringBuilder.append(ConvertToString(dataPoint.getLatitude()));
            stringBuilder.append(", lng: ");
            stringBuilder.append(ConvertToString(dataPoint.getLongitude()));
            stringBuilder.append("}");

        return stringBuilder.toString();
    }

    public static String ConvertToString(double value){
        return String.format(Locale.ENGLISH, "%1$.6f", value);

    }

}
