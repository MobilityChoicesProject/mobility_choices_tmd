package at.fhv.tmd.simpleRules;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.util.List;

/**
 * Created by Johannes on 07.06.2017.
 */
public class Rules {




  private TransportType getLikelistTransportType(List<Tuple<String,Double>> probabilities){

    double highestProbability = 0;
    String transportTypeStr = null;
    for (Tuple<String, Double> probability : probabilities) {
      if(highestProbability< probability.getItem2()){
        highestProbability=probability.getItem2();
        transportTypeStr= probability.getItem1();
      }
    }

    TransportType transportType = Enum.valueOf(TransportType.class, transportTypeStr);
    return transportType;

  }











}
