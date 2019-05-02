package at.fhv.tmd.featureCalculation;

import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

/**
 * Created by Johannes on 20.06.2017.
 */
public class FeatureContext {


  private HashMap<String,Object> input = new HashMap<>();
  private HashMap<String,FeatureResult> resultHashMap = new HashMap<>();

  public  <T> T getInput(String key){
    Object o = input.get(key);
    return (T)o;
  }

  public void addFeature(String featureName, double featureValue){
    FeatureResult featureResult = new FeatureResult(featureName, featureValue);
    resultHashMap.put(featureName,featureResult);
  }

  public List<FeatureResult> getResults(){
    List<FeatureResult> results = new ArrayList<>();
    for (Entry<String, FeatureResult> stringFeatureResultEntry : resultHashMap.entrySet()) {
      results.add(stringFeatureResultEntry.getValue());
    }

    return results;
  }


  public void addInput(String featureInputName, Object input) {
    this.input.put(featureInputName,input);

  }
}
