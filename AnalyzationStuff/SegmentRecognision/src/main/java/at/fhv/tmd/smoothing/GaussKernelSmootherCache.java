package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 22.05.2017.
 */
public interface GaussKernelSmootherCache extends SwitchAbleCache{

  IGpsPoint calcAndPutInCache(LocalDateTime time, List<IGpsPoint> points, double sigma);

  IGpsPoint calcWithoutCaching(LocalDateTime time, List<IGpsPoint> points, double sigma);

  void switchOn(boolean flag);

  boolean isOn();
}
