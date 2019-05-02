package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import java.time.LocalDateTime;
import java.util.List;
import java.util.TreeMap;

/**
 * Created by Johannes on 02.05.2017.
 */
public class SimpleGaussKernelSmootherCache implements GaussKernelSmoother,
    GaussKernelSmootherCache {

  private GaussKernelSmoother smoother;
  private TreeMap<LocalDateTime, IGpsPoint> alreadyCalculatedCoordinates = new TreeMap<>();

  private boolean isCacheOn = true;

  public SimpleGaussKernelSmootherCache(GaussKernelSmoother gaussKernelSmoother){
    this.smoother = gaussKernelSmoother;
  }


  @Override
  public IGpsPoint calcPoint(LocalDateTime time, List<IGpsPoint> points, double sigma) {
    IGpsPoint coordinate = alreadyCalculatedCoordinates.get(time);
    if (coordinate == null){
      coordinate =smoother.calcPoint(time, points,sigma);
      if(isCacheOn){
        alreadyCalculatedCoordinates.put(time,coordinate);
      }
    }
    return coordinate;
  }

  @Override
  public IGpsPoint calcAndPutInCache(LocalDateTime time, List<IGpsPoint> points, double sigma){
    boolean isCacheOn = this.isCacheOn;
    switchOn(true);
    IGpsPoint coordinate = calcPoint(time, points, sigma);
    switchOn(isCacheOn);
    return coordinate;
  }

  @Override
  public IGpsPoint calcWithoutCaching(LocalDateTime time, List<IGpsPoint> points, double sigma){
    boolean isCacheOn = this.isCacheOn;
    switchOn(false);
    IGpsPoint coordinate = calcPoint(time, points, sigma);
    switchOn(isCacheOn);
    return coordinate;
  }


  @Override
  public void switchOn(boolean flag) {
    isCacheOn = flag;
  }

  @Override
  public boolean isOn() {
    return isCacheOn;
  }
}
