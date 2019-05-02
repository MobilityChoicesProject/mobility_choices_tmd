package at.fhv.tmd.drools;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.TransportType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johannes on 04.07.2017.
 */
public class TrackingElement {

  private HashMap<TransportType,List<Tuple<Double,Double>>> data = new HashMap();
  private HashMap<TransportType,Tuple<TransportType,Double>> transportTypes = new HashMap<>();

  public void add(TransportType transportType,double probability, double weight){

    List<Tuple<Double, Double>> tuples = data.get(transportType);
    if(tuples == null){
      tuples = new ArrayList<>();
      data.put(transportType,tuples);
    }
    tuples.add(new Tuple<>(probability,weight));

  }



  public void add(TransportType transportType,double probability){
    add(transportType,probability,1);
  }


  public Tuple<TransportType,Double> getMostLikeliest(){
   return getAverageProbabilities().get(0);
  }

  public List<Tuple<TransportType,Double>> getAverageProbabilities(){

    List<Tuple<TransportType,Double>> results = new ArrayList<>();
    double totalSum= 0;
    for (TransportType transportType : data.keySet()) {
      List<Tuple<Double, Double>> tuples = data.get(transportType);
      double SumValue = 0;
      double weightSum = 0;
      for (Tuple<Double, Double> tuple : tuples) {
        SumValue  +=  tuple.getItem1()*tuple.getItem2();
        weightSum +=tuple.getItem2();
      }

      double averageProbability = SumValue/weightSum;
      totalSum += averageProbability;
      results.add(new Tuple<>(transportType,averageProbability));
    }

    double sum=0;
    List<Tuple<TransportType,Double>> results1 = new ArrayList<>();
    for (Tuple<TransportType, Double> result : results) {
      double percentage = result.getItem2() / totalSum;
      results1.add(new Tuple<>(result.getItem1(),percentage));
      sum += percentage;
    }

    if(Math.abs(sum-1)>0.10){
      int d=6;
    }



    results1.sort((o1, o2) -> -o1.getItem2().compareTo(o2.getItem2()));

    return results1;
  }




}
