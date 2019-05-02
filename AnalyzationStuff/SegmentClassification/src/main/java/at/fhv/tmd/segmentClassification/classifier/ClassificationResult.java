package at.fhv.tmd.segmentClassification.classifier;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 27.06.2017.
 */
public class ClassificationResult implements Serializable {

  List<Tuple<TransportType,Double>> results = new ArrayList<>();

  public ClassificationResult(
      List<Tuple<TransportType, Double>> results) {
    this.results = results;
  }

  public TransportType getMostLikeliestResult(){
    List<Tuple<TransportType, Double>> results = getResults();
    results.sort((o1, o2) -> -Double.compare(o1.getItem2(),o2.getItem2()));
    return results.get(0).getItem1();
  }

  public  List<Tuple<TransportType,Double>>  getResults(){
    return new ArrayList<>(results);
  }

  public double getLikelyHoodFor(TransportType transportType){
    for (Tuple<TransportType, Double> result : results) {
      if(result.getItem1()== transportType){
        return result.getItem2();
      }
    }
    return 0;
  }


}
