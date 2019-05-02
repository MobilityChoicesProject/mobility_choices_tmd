package at.fhv.transportdetector.trackingtypes;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Johannes on 07.02.2017.
 */
public enum TransportType {
    CAR,
    BIKE,
    BUS,
    TRAIN,
    WALK,
    OTHER,
    STATIONARY;

    private static List<TransportType> orderedTransportTypes;
    public static List<TransportType> getValuesAlpabethicAsc(){
        if(orderedTransportTypes== null){
            TransportType[] values = TransportType.values();
            List<TransportType> transportTypes = new ArrayList<>();
            for (TransportType value : values) {
                transportTypes.add(value);
            }
            Collections.sort(transportTypes,(o1, o2) -> String.CASE_INSENSITIVE_ORDER.compare(o1.name(),o2.name()));

            orderedTransportTypes = transportTypes;
        }

        return orderedTransportTypes;
    }

}
