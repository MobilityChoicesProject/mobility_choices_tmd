package at.fhv.tmd.segmentClassification.classifier;

import at.fhv.transportdetector.trackingtypes.TransportType;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by Johannes on 21.07.2017.
 */
public class ClassifierParameters {


  public ClassifierParameters(String clasifierPath, Set<String> usedFeatures, List<TransportType> predictionClasses) {
    this.clasifierPath = clasifierPath;
    this.usedFeatures = usedFeatures;
    this.predictionClasses = predictionClasses;
  }

  private String clasifierPath;
  private List<TransportType> predictionClasses;
  private Set<String> usedFeatures;

  public String getClasifierPath() {
    return clasifierPath;
  }

  public Set<String> getUsedFeatures() {
    return usedFeatures;
  }

  public List<TransportType> getPredictionClasses() {
    return predictionClasses;
  }


  public static ClassifierParameters WITHOUT_SEGMENTATION(){

    String path = "/final_random_forest_model.model";
    Set<String> features  = new HashSet<>();
    features.add("95_speed_percentil");
    features.add("average_train_endpoint_distance");
    features.add("averageDistanceToClosePoints");
    features.add("average_bus_endpoint_distance");
    features.add("average_bus_route_distance");
    features.add("average_rail_route_distance");
    features.add("median_speed_acceleration");
    features.add("median_speed");
    features.add("speed_variance");

    List<TransportType> transportTypes = TransportType.getValuesAlpabethicAsc();
    transportTypes.remove(TransportType.WALK);
    ClassifierParameters classifierParameters = new ClassifierParameters(path,features,transportTypes);
    return  classifierParameters;

  }

  public static ClassifierParameters DEFAULT(){

    String path = "/segmenationRandomForest.model";
    Set<String> features  = new HashSet<>();
    features.add("95_speed_percentil");
    features.add("average_train_endpoint_distance");
    features.add("averageDistanceToClosePoints");
    features.add("average_bus_endpoint_distance");
    features.add("average_bus_route_distance");
    features.add("average_rail_route_distance");
    features.add("median_speed_acceleration");
    features.add("median_speed");
    features.add("speed_variance");

    List<TransportType> transportTypes = TransportType.getValuesAlpabethicAsc();

    ClassifierParameters classifierParameters = new ClassifierParameters(path,features,transportTypes);
    return  classifierParameters;

  }


}
