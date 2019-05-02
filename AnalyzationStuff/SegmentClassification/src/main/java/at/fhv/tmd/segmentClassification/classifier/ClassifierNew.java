package at.fhv.tmd.segmentClassification.classifier;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.features.FeatureResult;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import weka.classifiers.Classifier;
import weka.core.DenseInstance;
import weka.core.Instance;

/**
 * Created by Johannes on 21.06.2017.
 */
public class ClassifierNew implements at.fhv.tmd.segmentClassification.classifier.Classifier {

    private Classifier cls;
    private boolean notInitialized = true;

    List<String> predictionClasses = new ArrayList<>();

    private ClassifierParameters classifierParameters;

    public void init(ClassifierParameters classifierParameters) throws ClassifierException {

        if (notInitialized) {
            String clasifierPath = classifierParameters.getClasifierPath();
            loadClassifier(clasifierPath);

            this.classifierParameters = classifierParameters;

            for (TransportType predictiveClass : classifierParameters.getPredictionClasses()) {
                predictionClasses.add("'" + predictiveClass.name() + "'");
            }

            notInitialized = false;
        }

    }


    @Override
    public ClassificationResult classify(List<FeatureResult> featureResultListOfOneSegment) {
        if (notInitialized) {
            throw new IllegalStateException("Classifier has to be initialized first");
        }

        Instance inst = new DenseInstance(15);
        int index = 0;

        featureResultListOfOneSegment = new ArrayList<>(featureResultListOfOneSegment);
        featureResultListOfOneSegment.sort((o1, o2) -> o1.getFeatureName().charAt(0) - o2.getFeatureName().charAt(0));
        ArfFileWrapper arfFileWrapper = new ArfFileWrapper();


        Set<String> usedFeatures = classifierParameters.getUsedFeatures();
        for (FeatureResult featureResult : featureResultListOfOneSegment) {

            if (!usedFeatures.contains(featureResult.getFeatureName())) {
                continue;
            }

            inst.setValue(index++, featureResult.getFeatureValue());
            arfFileWrapper.addNumericAttribute(featureResult.getFeatureName());
        }


        arfFileWrapper.setLabelAttribute("TransportType", predictionClasses);

        inst.setDataset(arfFileWrapper.getDataSet("transportClassifier"));

        double[] doubles = new double[0];
        try {
            doubles = cls.distributionForInstance(inst);

        } catch (Exception e) {
            e.printStackTrace();
        }
        for (double aDouble : doubles) {
            int b = 4;
        }
        List<Tuple<TransportType, Double>> tuples = convertTo(doubles);

        return new ClassificationResult(tuples);
    }

    public List<Tuple<TransportType, Double>> convertTo(double[] doubles) {

        List<TransportType> predictionClasses = classifierParameters.getPredictionClasses();
        predictionClasses.remove(TransportType.WALK);
        predictionClasses.remove(TransportType.STATIONARY);

        int i = 0;
        List<Tuple<TransportType, Double>> results = new ArrayList<>();
        for (TransportType predictionClass : predictionClasses) {
            results.add(new Tuple<>(predictionClass, doubles[i++]));
        }

        return results;
    }


    public void loadClassifier(String path) throws ClassifierException {
        if (path == null) {
            path = "/src/main/resources/segmenationRandomForest.model";
        }

        System.out.println("load classifier: " + path);
        InputStream resourceAsStream = this.getClass()
                .getResourceAsStream(path);
        try {
            cls = (Classifier) weka.core.SerializationHelper.read(resourceAsStream);
            resourceAsStream.close();
        } catch (Exception e) {
            throw new ClassifierException("Could not load RandomForest model");
        }

    }


}
