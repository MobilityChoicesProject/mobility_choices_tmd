package at.fhv.transportClassifier.segmentsplitting;

import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.builder.SimpleAcceleratorState;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 22.03.2017.
 */
public class AccelerationExpectedValueVarianceCalculator {


  public List<ExpectedValueVarianceResult> Calc(List<AcceleratorState> acceleratorStateList,
      Duration timeFrame,
      int minDataPointsPerFrame) {
    if (acceleratorStateList.size() < 100) {
      throw new IllegalArgumentException("not enough data");
    }

    AcceleratorState firstAcState = acceleratorStateList.get(0);
    LocalDateTime timeFrameEnd = firstAcState.getTime().plus(timeFrame);

    AcceleratorState lastAccelerationState = null;
    AcceleratorState sum = null;
    AcceleratorState evSum = null;

    List<ExpectedValueVarianceResult> expectedValueVarianceResults = new ArrayList<>();

    int newI = -1;
    for (int i = 0; i < acceleratorStateList.size(); i++) {

      newI = -1;
      AcceleratorState acceleratorState = acceleratorStateList.get(i);
      timeFrameEnd = acceleratorState.getTime().plus(timeFrame);

      for (int j = 0; (i + j) < acceleratorStateList.size(); j++) {
        AcceleratorState newAcceleratorState = acceleratorStateList.get(i + j);

        long millis = Duration.between(acceleratorState.getTime(), newAcceleratorState.getTime())
            .toMillis();
        // missing ac data.
        if (millis > 300 && newI == -1) {
          newI = i + j - 1;
        }

        if (j == 0) {
          lastAccelerationState = newAcceleratorState;
          sum = new SimpleAcceleratorState(null, 0f, 0f, 0f);
          evSum = null;
          evSum = addToEvSum(evSum, newAcceleratorState);
        } else {
          if (newAcceleratorState.getTime().isBefore(timeFrameEnd)) {
            sum = sum(sum, lastAccelerationState, newAcceleratorState);
            evSum = addToEvSum(evSum, newAcceleratorState);
          } else {
            if (j < minDataPointsPerFrame) {
              break;
            } else {

              ExpectedValueVarianceResult expectedValueVarianceResult = calcExpectedValue(evSum, i,
                  i + j, acceleratorStateList);

              Duration duration = timeFrame.dividedBy(2);
              LocalDateTime middleTime = acceleratorState.getTime().plus(duration);
              expectedValueVarianceResult.setMiddletime(middleTime);

              double sumAcs =
                  sum.getXAcceleration() + sum.getYAcceleration() + sum.getZAcceleration();
              long nanos = timeFrame.toNanos();
              double seconds = nanos / 1000000000.0;
              double accelerationSumPerSecond = sumAcs / (seconds);
              double accelerationSumPerUnit = sumAcs / (1.0 * j);

              expectedValueVarianceResult.setAccelerationSumPerSecond(accelerationSumPerSecond);
              expectedValueVarianceResult.setAccelerationSumPerUnit(accelerationSumPerUnit);
              expectedValueVarianceResult.setCountOfDataPoints(j);
              expectedValueVarianceResults.add(expectedValueVarianceResult);

              break;
            }
          }
        }
      }
      if (newI != -1) {
        i = newI;
      }


    }
    return expectedValueVarianceResults;

  }

  private AcceleratorState sum(AcceleratorState acceleratorStatesSum,
      AcceleratorState lastAcceleratorStatesSum, AcceleratorState newAcceleratorStatesSum) {
    double xdiff = Math.abs(
        lastAcceleratorStatesSum.getXAcceleration() - newAcceleratorStatesSum.getXAcceleration());
    double ydiff = Math.abs(
        lastAcceleratorStatesSum.getYAcceleration() - newAcceleratorStatesSum.getYAcceleration());
    double zdiff = Math.abs(
        lastAcceleratorStatesSum.getZAcceleration() - newAcceleratorStatesSum.getZAcceleration());

    AcceleratorState acceleratorState = new SimpleAcceleratorState(null,
        acceleratorStatesSum.getXAcceleration() + xdiff,
        acceleratorStatesSum.getYAcceleration() + ydiff,
        acceleratorStatesSum.getZAcceleration() + zdiff);
    return acceleratorState;
  }

  private AcceleratorState addToEvSum(AcceleratorState acceleratorStatesSum,
      AcceleratorState newAcceleratorState) {
    if (acceleratorStatesSum == null) {
      return newAcceleratorState;
    }
    double xSum = acceleratorStatesSum.getXAcceleration() + newAcceleratorState.getXAcceleration();
    double ySum = acceleratorStatesSum.getYAcceleration() + newAcceleratorState.getYAcceleration();
    double zSum = acceleratorStatesSum.getZAcceleration() + newAcceleratorState.getZAcceleration();
    return new SimpleAcceleratorState(null, xSum, ySum, zSum);

  }


  private ExpectedValueVarianceResult calcExpectedValue(AcceleratorState evSum, int jPlusIMin,
      int jPlusIMax, List<AcceleratorState> acceleratorStateList) {
    double xExpectedValue = evSum.getXAcceleration() / (jPlusIMax - jPlusIMin);
    double yExpectedValue = evSum.getYAcceleration() / (jPlusIMax - jPlusIMin);
    double zExpectedValue = evSum.getZAcceleration() / (jPlusIMax - jPlusIMin);

    AcceleratorState varianceSum = null;
    for (int k = jPlusIMin; k < jPlusIMax; k++) {
      AcceleratorState acceleratorState = acceleratorStateList.get(k);
      double x = acceleratorState.getXAcceleration() - xExpectedValue;
      double y = acceleratorState.getYAcceleration() - yExpectedValue;
      double z = acceleratorState.getZAcceleration() - zExpectedValue;
      x = x * x;
      y = y * y;
      z = z * z;
      SimpleAcceleratorState simpleAcceleratorState = new SimpleAcceleratorState(null, x, y, z);
      varianceSum = addToEvSum(varianceSum, simpleAcceleratorState);
    }

    ExpectedValueVarianceResult expectedValueVarianceResult = new ExpectedValueVarianceResult();
    expectedValueVarianceResult.setxExpectedValue(xExpectedValue);
    expectedValueVarianceResult.setyExpectedValue(yExpectedValue);
    expectedValueVarianceResult.setzExpectedValue(zExpectedValue);

    int amount = jPlusIMax - jPlusIMin;
    expectedValueVarianceResult.setxVariance(varianceSum.getXAcceleration() / amount);
    expectedValueVarianceResult.setyVariance(varianceSum.getYAcceleration() / amount);
    expectedValueVarianceResult.setzVariance(varianceSum.getZAcceleration() / amount);

    return expectedValueVarianceResult;
  }


  public static class ExpectedValueVarianceResult {

    private double xExpectedValue;
    private double yExpectedValue;
    private double zExpectedValue;

    private double xVariance;
    private double yVariance;
    private double zVariance;

    private double AccelerationSumPerSecond;

    private int countOfDataPoints;
    private double accelerationSumPerUnit;

    public int getCountOfDataPoints() {
      return countOfDataPoints;
    }

    public void setCountOfDataPoints(int countOfDataPoints) {
      this.countOfDataPoints = countOfDataPoints;
    }

    public double getAccelerationSumPerSecond() {
      return AccelerationSumPerSecond;
    }

    public void setAccelerationSumPerSecond(double setAccelerationSumPerSecond) {
      this.AccelerationSumPerSecond = setAccelerationSumPerSecond;
    }

    private LocalDateTime middletime;

    public LocalDateTime getMiddletime() {
      return middletime;
    }

    public void setMiddletime(LocalDateTime middletime) {
      this.middletime = middletime;
    }

    public double getxExpectedValue() {
      return xExpectedValue;
    }

    public void setxExpectedValue(double xExpectedValue) {
      this.xExpectedValue = xExpectedValue;
    }

    public double getyExpectedValue() {
      return yExpectedValue;
    }

    public void setyExpectedValue(double yExpectedValue) {
      this.yExpectedValue = yExpectedValue;
    }

    public double getzExpectedValue() {
      return zExpectedValue;
    }

    public void setzExpectedValue(double zExpectedValue) {
      this.zExpectedValue = zExpectedValue;
    }

    public double getxVariance() {
      return xVariance;
    }

    public void setxVariance(double xVariance) {
      this.xVariance = xVariance;
    }

    public double getyVariance() {
      return yVariance;
    }

    public void setyVariance(double yVariance) {
      this.yVariance = yVariance;
    }

    public double getzVariance() {
      return zVariance;
    }

    public void setzVariance(double zVariance) {
      this.zVariance = zVariance;
    }

    public void setAccelerationSumPerUnit(double accelerationSumPerUnit) {
      this.accelerationSumPerUnit = accelerationSumPerUnit;
    }

    public double getAccelerationSumPerUnit() {
      return accelerationSumPerUnit;
    }
  }


}
