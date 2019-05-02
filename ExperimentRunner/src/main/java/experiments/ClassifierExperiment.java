package experiments;

import at.fhv.features.LabeledFeature;
import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import at.fhv.tmd.segmentClassification.classifier.ClassifierException;
import at.fhv.tmd.segmentClassification.classifier.ClassifierNew;
import at.fhv.tmd.segmentClassification.classifier.ClassifierParameters;
import at.fhv.transportClassifier.common.TrackingIdNamePairFileReaderHelper;
import at.fhv.transportClassifier.common.TrackingIdNamePair;
import at.fhv.transportClassifier.dal.interfaces.LeightweightTrackingDao;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;
import helper.FeatureCalculationHelper;
import helper.PropertyHelper;
import helper.TrackingIdNamePairIterator;
import java.io.File;
import java.util.List;
import javax.persistence.EntityManagerFactory;

/**
 * Created by Johannes on 27.06.2017.
 */
public class ClassifierExperiment {




  private LeightweightTrackingDao leightweightTrackingDao;
  private  EntityManagerFactory emf;

  public ClassifierExperiment(
      LeightweightTrackingDao leightweightTrackingDao, EntityManagerFactory emf) {
    this.leightweightTrackingDao = leightweightTrackingDao;
    this.emf = emf;
  }

  public void doIt(){





    String dataFolder = PropertyHelper.getValue(Constants.dataFolder);

    File trainingSetFile = new File(dataFolder,Constants.trainingSet);
    File testSetFile = new File(dataFolder,Constants.testSet);

    List<TrackingIdNamePair> trainingSet = TrackingIdNamePairFileReaderHelper.load(trainingSetFile.getPath());

    trainingSet = trainingSet.subList(0,30);

    FeatureCalculationHelper featureCalculationHelper = new FeatureCalculationHelper();
    List<LabeledFeature> trainingSetlabeledFeatureList = featureCalculationHelper
        .calculate(new TrackingIdNamePairIterator(trainingSet,leightweightTrackingDao), emf, -1);


    ClassifierNew classifierNew = new ClassifierNew();
    try {
      classifierNew.init(ClassifierParameters.DEFAULT());
    } catch (ClassifierException e) {
      e.printStackTrace();
    }

    for (LabeledFeature labeledFeature : trainingSetlabeledFeatureList) {
      List<FeatureResult> featureResultList = labeledFeature.getFeatureResultList();
      ClassificationResult classify = classifierNew.classify(featureResultList);

      if(classify.getMostLikeliestResult().equals(labeledFeature.getTransportType())){
        System.out.println(labeledFeature.getTransportType().name()+"          guess: "+classify.getMostLikeliestResult().name()+"      <-------------");

      }else{
        System.out.println(labeledFeature.getTransportType().name()+"          guess: "+classify.getMostLikeliestResult().name()+"      ");

      }


    }

    int b= 4;


  }


}
