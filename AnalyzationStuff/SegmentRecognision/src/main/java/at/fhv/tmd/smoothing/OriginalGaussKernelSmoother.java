package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by Johannes on 02.05.2017.
 */
public class OriginalGaussKernelSmoother implements GaussKernelSmoother {

  private CoordinateFactory factory;

  public OriginalGaussKernelSmoother(CoordinateFactory factory){
    this.factory = factory;
  }


  @Override
  public IGpsPoint calcPoint(LocalDateTime time, List<IGpsPoint> points, double sigma){

    IGpsPoint firstPoint = points.get(0);
    LocalDateTime zeroTime = firstPoint.getTime();

    Duration between = Duration.between(zeroTime, time);
    long timeInSeconds = between.toMillis() / 1000;

    double xsum = 0;
    double ysum = 0;
    double accuracySum = 0;
    double sumOfGausianKernelFunction = 0;
    for (IGpsPoint point : points) {
      double x_c_t = point.getLatitude();
      double y_c_t = point.getLongitude();
      double accuracy_c_t = point.getAccuracy();


      long millis = Duration.between(zeroTime, point.getTime()).toMillis();
      double tj = millis/1000.0;
      double  w_t = GaussKernelHelper.gausianKernelFunction(timeInSeconds,tj,sigma);

      xsum += x_c_t*w_t;
      ysum += y_c_t*w_t;
      accuracySum += accuracy_c_t*w_t;
      sumOfGausianKernelFunction += w_t;

    }
    double x = xsum/sumOfGausianKernelFunction;
    double y = ysum/sumOfGausianKernelFunction;
    double accuracy = accuracySum/sumOfGausianKernelFunction;
    IGpsPoint coordinate = factory.createCoordinate(x, y, accuracy, time);

    return coordinate;
  }












}
