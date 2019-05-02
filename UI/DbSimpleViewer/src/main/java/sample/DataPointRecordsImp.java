package sample;

import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.xychart.DataPointRecords;
import at.fhv.xychart.Tuple;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Johannes on 02.03.2017.
 */
abstract public class DataPointRecordsImp implements DataPointRecords {

    private final LocalDateTime startTimestamp;
    List<Tuple<Double,Double>> dataPoints = new ArrayList<>();
    private double lowerBound = 0;
  private double upperBound = 0;

  public DataPointRecordsImp(List<AcceleratorState> accelerationValues,double factor,LocalDateTime startTimestamp){
        this.startTimestamp = startTimestamp;
        LocalDateTime timestamp = accelerationValues.get(0).getTime();
        long timeOffsetInMillis=0;

        for (AcceleratorState accelerationValue : accelerationValues) {
            LocalDateTime currentTrackingTimestamp = accelerationValue.getTime();
            Duration duration = Duration.between(startTimestamp,currentTrackingTimestamp);

            timeOffsetInMillis = duration.toMillis();
            timeOffsetInMillis = ( long)(timeOffsetInMillis*factor);

            dataPoints.add(new Tuple<Double,Double>((double)timeOffsetInMillis/1000.0,
                getAxis(accelerationValue)));
        }
        upperBound = timeOffsetInMillis/1000.0;
    }

    public abstract double getAxis(AcceleratorState accelerationTracking);

    @Override
    public double getLowerBound() {
        return lowerBound;
    }

    @Override
    public double getUpperBound() {
        return upperBound;
    }

    @Override
    public double getRecordsCount() {
        return dataPoints.size();
    }

    @Override
    public Double getValue(double indexInBounds) {

        if(indexInBounds <= upperBound && indexInBounds >= lowerBound){

            int indexOfTuple = Collections.binarySearch(dataPoints, new Tuple<>(indexInBounds, 0.0), new AccelerationComparator(getMinTickSize()));

            if(indexOfTuple <0 ){
                int indexToadd = Math.abs(indexOfTuple+1);

                Double seconds = dataPoints.get(indexToadd).getItem1();
                double diff = seconds - indexInBounds;
                if(diff>4){
                    return null;
                }else{
                    return dataPoints.get(indexToadd).getItem2();
                }
            }
            return dataPoints.get(indexOfTuple).getItem2();

        }else{
            throw new IllegalArgumentException("indexInBounds is not in range");
        }
    }

    @Override
    public double getMinTickSize() {
        return 0.2;
    }
}
