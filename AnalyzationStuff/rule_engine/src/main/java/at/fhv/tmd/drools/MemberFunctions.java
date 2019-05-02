package at.fhv.tmd.drools;

/**
 * Created by Johannes on 07.06.2017.
 */
public class MemberFunctions {


  public static double pieceWiseLinear(double min,double max, double minValue,double maxValue,double input){

    if(!(min < max)){
      throw new IllegalArgumentException("Min has to be smaller than max");
    }

    if(input<=min){
      return minValue;
    }
    if(input>=max){
      return maxValue;
    }

    return calcalueBetween(min,max,minValue,maxValue,input);
  }



  public static double trapezoid(double min1,double max1, double max2, double min2, double minValue,double maxValue,double input){
    if(input<=  min1){
      return minValue;
    }
    if(input>= max1 && input <= max2){
      return maxValue;
    }
    if(input>= min2){
      return minValue;
    }

    if(input>min1 && input<max1){
      double value = calcalueBetween(min1, max1, minValue, maxValue, input);
      return value;
    }
    if(input>max2 && input<min2){
      double value = calcalueBetween(max2 , min2, maxValue, minValue, input);
      return value;
    }

    throw new RuntimeException("AlgorithmError. Should never reach this code");
  }


  private static double calcalueBetween(double min,double max, double minValue,double maxValue,double input){
    double minMaxDiff = max - min;

    double minInputDiff = input-min;

    double fraction = minInputDiff / minMaxDiff;

    double minMaxValueDiff = maxValue - minValue;

    double value = minValue+minMaxValueDiff*fraction;

    return value;
  }






}
