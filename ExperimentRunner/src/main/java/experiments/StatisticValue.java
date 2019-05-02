package experiments;

/**
 * Created by Johannes on 11.07.2017.
 */
public class StatisticValue {

  private int truePositives = 0;
  private int falsePositives = 0;
  private int falseNegatives = 0;
  private int trueNegative = 0;

  private int counter= 0;

  private String aclass;

  public StatisticValue(String aclass) {
    this.aclass = aclass;
  }

  public int getTruePositives() {
    return truePositives;
  }

  public int getFalsePositives() {
    return falsePositives;
  }

  public void incrementTruePositives() {
    truePositives++;
  }

  public void incrementFalsePositives() {
    falsePositives++;
  }

  public void incrementFalseNegatives() {
    falseNegatives++;
  }

  public double calcPrecision() {
    double precision = truePositives / (1.0 * truePositives + falsePositives);
    return precision;
  }

  public double calcRecall() {
    double accuracy = truePositives / (1.0 * truePositives + falseNegatives);
    return accuracy;
  }

  public double calcFMeasure() {
    double p = calcPrecision();
    double r = calcRecall();

    double fMeasure = 2 * p * r / (p + r);
    return fMeasure;

  }

  public void incrementTrueNegative() {
    trueNegative++;
  }

  public void incrementActualClassCounter(){
    counter++;
  }

  public int getCounter() {
    return counter;
  }

  public String getLabel() {
    return aclass;
  }
}
