package experiments.classificationEvaluation;

/**
 * Created by Johannes on 26.07.2017.
 */
public class Evaluation {

  private double distanceSum = 0.0;
  private double durationSum = 0.0;
  private double distanceOverlapping = 0.0;
  private double durationOverlapping = 0.0;

  private double drools_distanceSum = 0.0;
  private double drools_durationSum = 0.0;
  private double drools_distanceOverlapping = 0.0;
  private double drools_durationOverlapping = 0.0;

  private double signalShortage_distanceSum = 0.0;
  private double signalShortage_durationSum = 0.0;

  synchronized public void addDistanceSum(double sum) {
    distanceSum = distanceSum + sum;
  }

  synchronized public void addDurationSum(double sum) {
    durationSum = durationSum + sum;
  }

  synchronized public void addDistanceOverlapping(double sum) {
    distanceOverlapping = distanceOverlapping + sum;
  }

  synchronized public void addDurationOverlapping(double sum) {
    durationOverlapping = durationOverlapping + sum;
  }

  synchronized public void drools_addDistanceSum(double sum) {
    drools_distanceSum = drools_distanceSum + sum;
  }

  synchronized public void drools_addDurationSum(double sum) {
    drools_durationSum = drools_durationSum + sum;
  }

  synchronized public void drools_addDistanceOverlapping(double sum) {
    drools_distanceOverlapping = drools_distanceOverlapping + sum;
  }

  synchronized public void drools_addDurationOverlapping(double sum) {
    drools_durationOverlapping = drools_durationOverlapping + sum;
  }


  public double getDrools_distanceSum() {
    return drools_distanceSum;
  }

  public double getDrools_durationSum() {
    return drools_durationSum;
  }

  public double getDrools_distanceOverlapping() {
    return drools_distanceOverlapping;
  }

  public double getDrools_durationOverlapping() {
    return drools_durationOverlapping;
  }

  public double getDistanceSum() {
    return distanceSum;
  }

  public double getDurationSum() {
    return durationSum;
  }

  public double getDistanceOverlapping() {
    return distanceOverlapping;
  }

  public double getDurationOverlapping() {
    return durationOverlapping;
  }

  synchronized public void signalShortage_addDurationSum(double durationSum) {
    signalShortage_durationSum = signalShortage_durationSum + durationSum;
  }

  synchronized public void signalShortage_addDistanceSum(double durationSum) {
    signalShortage_distanceSum = signalShortage_distanceSum + durationSum;
  }

  public double getSignalShortage_distanceSum() {
    return signalShortage_distanceSum;
  }

  public double getSignalShortage_durationSum() {
    return signalShortage_durationSum;
  }
}
