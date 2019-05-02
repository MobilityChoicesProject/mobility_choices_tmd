package at.fhv.tmd.smoothing;

import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceDefaultCache;
import at.fhv.transportClassifier.common.configSettings.ConfigServiceImp;
import java.util.List;

/**
 * Created by Johannes on 22.05.2017.
 */
public enum CoordinateInterpolatorFactory {
  Optimized,
  Original;


  public static CoordinateInterpolator create(CoordinateInterpolatorFactory flag,List<IGpsPoint> coordinates,ConfigService configService){
    if(flag == Optimized){

      double calcRangeFactor = configService.getValue(ConfigServiceDefaultCache.kernelSmoother_calcRangeFactor);
      double sigma = configService.getValue(ConfigServiceDefaultCache.kernelSmoother_sigma);

      OptimizedGaussKernelSmoother optimizedGaussKernelSmoother = new OptimizedGaussKernelSmoother(
          new SimpleCoordinateFactory());

      optimizedGaussKernelSmoother.setMaxRangeFactor(calcRangeFactor);
      SimpleCoordinateInterpolator simpleCoordinateInterpolator = new SimpleCoordinateInterpolator(
          new SimpleGaussKernelSmootherCache(optimizedGaussKernelSmoother), coordinates);
      simpleCoordinateInterpolator.setKernelBandwidth(sigma);
      return new RoundMillisCoordinateInterpolator(coordinates,simpleCoordinateInterpolator);
    }else{
      return new RoundMillisCoordinateInterpolator(coordinates,new SimpleCoordinateInterpolator(new SimpleGaussKernelSmootherCache(new OriginalGaussKernelSmoother(new SimpleCoordinateFactory())),coordinates));
    }
  }




}
