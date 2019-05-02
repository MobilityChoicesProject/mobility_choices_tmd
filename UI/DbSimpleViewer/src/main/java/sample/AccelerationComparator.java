package sample;

import at.fhv.xychart.Tuple;
import java.util.Comparator;

/**
 * Created by Johannes on 02.03.2017.
 */
public class AccelerationComparator implements Comparator<Tuple<Double, Double>> {

        private double minTickSize;

    public AccelerationComparator(double minTickSize) {
        this.minTickSize = minTickSize;
    }

    @Override
public int compare(Tuple<Double, Double> o1, Tuple<Double, Double> o2) {

            double diff =o1.getItem1()-o2.getItem1();
            if(Math.abs(diff)< minTickSize){
                return 0;
            }else{
                double ceiledValue= Math.ceil(diff);
                if(ceiledValue== -0.0){
                    return -1;
                }else {
                    return (int)ceiledValue;
                }
            }
        }
}
