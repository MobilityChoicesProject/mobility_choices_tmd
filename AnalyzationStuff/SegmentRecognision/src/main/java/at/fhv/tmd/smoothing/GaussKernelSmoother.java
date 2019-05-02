package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 21.05.2017.
 */
public interface GaussKernelSmoother extends Serializable{

  IGpsPoint calcPoint(LocalDateTime time, List<IGpsPoint> points, double sigma);
}
