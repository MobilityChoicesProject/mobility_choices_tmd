package at.fhv.tmd.smoothing;

import java.math.BigDecimal;
import java.math.MathContext;

/**
 * Created by Johannes on 21.05.2017.
 */
public class GaussKernelHelper {

  protected static double gausianKernelFunction(double t, double tj, double sigma) {

    return Math.exp(-((t-tj)*(t-tj)/(2*sigma*sigma)));
  }




}
