package at.fhv.xychart;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 23.01.2017.
 */
public class DataPointsRecordStub implements DataPointRecords {


    List<Tuple<Double,Double>> dataPoints = new ArrayList<>();
    private double lowerBound;
    private double upperBound;

    public DataPointsRecordStub() {
        SecureRandom random = new SecureRandom();
        for (int i = 0; i < 30000; i++) {
            int negative = random.nextBoolean()?1:-1;
            dataPoints.add(new Tuple<Double, Double>(i / 5.0, random.nextDouble() * 100*negative));
        }
        lowerBound= dataPoints.get(0).getItem1();
        upperBound= dataPoints.get(dataPoints.size()-1).getItem1();
    }


    @Override
    public double getLowerBound() {
        return lowerBound;
    }


    @Override
    public double getUpperBound() {
        return  upperBound;
    }

    @Override
    public double getRecordsCount() {
        return dataPoints.size();
    }

    @Override
    public Double getValue(double indexInBounds) {

        if(indexInBounds <= upperBound && indexInBounds >= lowerBound){

            double minTickSize = getMinTickSize();


            double sizeToIndexInBounds = indexInBounds - lowerBound;
            double index =  sizeToIndexInBounds / minTickSize;
            int index1= (int)index;

            return dataPoints.get(index1).getItem2();

        }else{
            throw new IllegalArgumentException("indexInBounds is not in range");
        }
    }

    @Override
    public double getMinTickSize() {
        return 0.2;
    }
}
