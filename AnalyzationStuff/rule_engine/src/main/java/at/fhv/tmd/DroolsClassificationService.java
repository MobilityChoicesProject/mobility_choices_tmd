package at.fhv.tmd;

import at.fhv.features.LabeledFeature;
import at.fhv.tmd.drools.FeatureElement;
import at.fhv.tmd.drools.TrackingElement;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.Classifier;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.StatelessKieSession;

/**
 * Created by Johannes on 06.07.2017.
 */
public class DroolsClassificationService implements Classifier{



  private StatelessKieSession kSession;

  public void init(){
    KieServices kieServices = KieServices.Factory.get();
    KieContainer kContainer = kieServices.getKieClasspathContainer();
    kSession = kContainer.newStatelessKieSession();
  }


  public ClassificationResult classify( List<FeatureResult> featureResultList)   {
    if(kSession == null){
      init();
    }
      List<FeatureElement> featureElements = new ArrayList<>();
      TrackingElement trackingElement  = new TrackingElement();
      for (FeatureResult featureResult : featureResultList) {
        FeatureElement featureElement  = new FeatureElement();
        featureElement.setTrackingElement(trackingElement);
        featureElement.setName(featureResult.getFeatureName());
        featureElement.setValue(featureResult.getFeatureValue());
        featureElements.add(featureElement);
      }

      kSession.execute( featureElements );

    ClassificationResult classifiedResult = new ClassificationResult(trackingElement.getAverageProbabilities());
    return classifiedResult;

  }


  private void calc(LabeledFeature[] labeledFeatures){

    Map<TransportType,Map<String,List<Double>>> values = new HashMap<>();
    for (LabeledFeature labeledFeature : labeledFeatures) {

      TransportType transportType = labeledFeature.getTransportType();

      Map<String, List<Double>> stringListMap = values.get(transportType);
      if(stringListMap == null){
        stringListMap = new HashMap<>();
        values.put(transportType,stringListMap);
      }

      for (FeatureResult featureResult : labeledFeature.getFeatureResultList()) {

        List<Double> doubles = stringListMap.get(featureResult.getFeatureName());
        if(doubles == null){
          doubles = new ArrayList<>();
          stringListMap.put(featureResult.getFeatureName(),doubles);
        }

        doubles.add(featureResult.getFeatureValue());
      }
    }

    DecimalFormat df = new DecimalFormat("#.#######");

    Set<TransportType> transportTypes = values.keySet();

    for (TransportType transportType : transportTypes) {

      Map<String, List<Double>> stringListMap = values.get(transportType);

      for (String s : stringListMap.keySet()) {
        List<Double> doubles = stringListMap.get(s);

        double expectedValue = calcExpectedValue(doubles);
        double variance = calcVariance(doubles,expectedValue);

        String transport = padRight(transportType.name(), 15);
        String featureName = padRight(s, 30);
        System.out.println(transport+"   feature: "+featureName +"   exptectedValue:"+df.format(expectedValue)+"    variance:"+ df.format(variance)+ "    standardDerivation:" +df.format(Math.sqrt(variance)) );


        double standardDeriviation = Math.sqrt(variance);

        double min1 = expectedValue-(standardDeriviation);
        double min = expectedValue-(standardDeriviation/2);
        double max = expectedValue+(standardDeriviation/2);
        double max2 = expectedValue+(standardDeriviation);
        System.out.println(transport+"   feature: "+featureName +"   values:"+df.format(min1).replace(",",".")+ ","+df.format(min).replace(",",".")+","+df.format(max).replace(",",".")+"," +df.format(max2).replace(",",".")+", 0, 1" );



      }
    }


  }


  public static String padRight(String s, int n) {
    return String.format("%1$-" + n + "s", s);
  }

  public static String padLeft(String s, int n) {
    return String.format("%1$" + n + "s", s);
  }

  private double calcExpectedValue(List<Double> doubles){

    double doubleSum= 0;
    for (Double aDouble : doubles) {
      doubleSum += aDouble;
    }

    double exptectedValue = doubleSum / doubles.size();
    return exptectedValue;
  }


  private double calcVariance(List<Double> doubles,double expectedValue ){

    double sum=0;
    for (Double aDouble : doubles) {

      double pow = Math.pow((aDouble - expectedValue), 2);
      sum += pow;

    }

    double variance = sum / doubles.size();
    return variance;

  }


}
