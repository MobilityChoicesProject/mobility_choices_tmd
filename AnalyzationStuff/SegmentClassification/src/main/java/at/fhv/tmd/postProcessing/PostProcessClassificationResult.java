package at.fhv.tmd.postProcessing;

import at.fhv.tmd.segmentClassification.classifier.ClassificationResult;
import java.io.Serializable;

/**
 * Created by Johannes on 19.07.2017.
 */
public class PostProcessClassificationResult implements Serializable{

  private String source;
  private ClassificationResult classifiedResult;

  public PostProcessClassificationResult(String source,
      ClassificationResult classifiedResult) {
    this.source = source;
    this.classifiedResult = classifiedResult;
  }

  public String getSource() {
    return source;
  }

  public ClassificationResult getClassifiedResult() {
    return classifiedResult;
  }
}
