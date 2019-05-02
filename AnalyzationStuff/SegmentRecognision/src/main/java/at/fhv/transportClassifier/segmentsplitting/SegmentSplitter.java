package at.fhv.transportClassifier.segmentsplitting;

import at.fhv.tmd.common.Speed;
import at.fhv.tmd.common.Tuple;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.segmentsplitting.AccelerationExpectedValueVarianceCalculator.ExpectedValueVarianceResult;
import at.fhv.transportdetector.trackingtypes.AcceleratorState;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.builder.SimpleAcceleratorState;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by Johannes on 12.03.2017.
 */
public class SegmentSplitter {

    CoordinateUtil coordinateUtil = new CoordinateUtil();
    public List<LocalDateTime> split(Tracking tracking){
        List<LocalDateTime> splittingTimes = new ArrayList<>();

        splittingTimes = splitSpeed(tracking.getGpsPoints());


        return splittingTimes;
    }



    protected List<LocalDateTime> splitSpeed(List<IExtendedGpsPoint> gpsPoints){

        List<LocalDateTime> possibleSplits = new ArrayList<>();
        List<Tuple<LocalDateTime,Speed>> speedList = new ArrayList<>();

        Iterator<IExtendedGpsPoint> iterator = gpsPoints.iterator();
        IExtendedGpsPoint lastPoint = iterator.next();
        while (iterator.hasNext()){
            IExtendedGpsPoint thisPoint = iterator.next();

            Speed speed = CoordinateUtil.calcSpeedBetween1(lastPoint, thisPoint);
            speedList.add(new Tuple(lastPoint.getSensorTime(),speed));
        }



        List<Speed> averaged3Speeds = new ArrayList<>();
        Iterator<Tuple<LocalDateTime,Speed>> speedIterator = speedList.iterator();

        int amountToAverage = 3;

        double averageSum=0;
        Boolean isBelow1 = null;
        int size = speedList.size();
        int rounds = speedList.size() / amountToAverage;
        for(int i = 0; i< rounds;i++){


            for(int j = 0; j< amountToAverage;j++){
                Tuple<LocalDateTime, Speed> localDateTimeSpeedTuple = speedList.get(i*amountToAverage+j);
                averageSum += localDateTimeSpeedTuple.getItem2().getMeterPerSecond();
            }

            double average = averageSum/amountToAverage;

            if(average < 2){
                if(isBelow1 == null || !isBelow1){
                    possibleSplits.add(speedList.get(i * amountToAverage).getItem1());
                    isBelow1 = true;
                }
            }else{
                if(isBelow1 == null || isBelow1){
                    possibleSplits.add(speedList.get(i * amountToAverage).getItem1());
                    isBelow1 = false;
                }
            }

        }

        return possibleSplits;
    }



    List<LocalDateTime> splitAcceleration(List<AcceleratorState> acceleratorStates){

        int size = acceleratorStates.size();
        if (size< 100) {
            throw new IllegalArgumentException("Not enought accelerationData");
        }

        int amountToCalc = 10;
        AcceleratorState lastAccelerationState = null;

        int half = amountToCalc/2;
        for(int i = 0;i < size;i++){


            double xDiffSum=0,yDiffSum=0,zDiffSum=0;
            for(int j = 0; j < amountToCalc;j++){
                int index = i *amountToCalc+j ;
                if( index==0){
                    lastAccelerationState= acceleratorStates.get(0);
                }
                    AcceleratorState currentState = acceleratorStates.get(index);
                    xDiffSum+= Math.abs(
                        currentState.getXAcceleration() - lastAccelerationState.getXAcceleration());

                    yDiffSum+= Math.abs(
                        currentState.getYAcceleration() - lastAccelerationState.getYAcceleration());

                    zDiffSum+= Math.abs(
                        currentState.getZAcceleration() - lastAccelerationState.getZAcceleration());

                    if(half == j){

                    }


                    lastAccelerationState = currentState;

            }



        }


        throw new RuntimeException("Not implemented");
    }



    public List<LocalDateTime> splitGpsSpeedThreshold(List<IExtendedGpsPoint> gpsPoints, Speed threshold){

        Iterator<IExtendedGpsPoint> iterator = gpsPoints.iterator();
        IExtendedGpsPoint current = iterator.next();

        List<LocalDateTime> localDateTimes = new ArrayList<>();
        Speed lastthresholdCrossingSpeed = null;

        while(iterator.hasNext()){
            IExtendedGpsPoint next = iterator.next();
            Speed speed = CoordinateUtil.calcSpeedBetween1(current, next);

            if(speed.getKmPerHour() <= threshold.getKmPerHour()){
                if(lastthresholdCrossingSpeed == null || lastthresholdCrossingSpeed.getKmPerHour() > threshold.getKmPerHour()){
                    lastthresholdCrossingSpeed = speed;
                    LocalDateTime mostAccurateTime = current.getMostAccurateTime();
                    localDateTimes.add(mostAccurateTime);
                }

            }else{
                if(lastthresholdCrossingSpeed == null || lastthresholdCrossingSpeed.getKmPerHour() <= threshold.getKmPerHour()){
                    lastthresholdCrossingSpeed = speed;
                    LocalDateTime mostAccurateTime = current.getMostAccurateTime();
                    localDateTimes.add(mostAccurateTime);
                }
            }
            current = next;
        }

        return localDateTimes;
    }


    public List<LocalDateTime> splitAccelerationSumPerSecond(List<AcceleratorState> acceleratorStates){

        AccelerationExpectedValueVarianceCalculator accelerationExpectedValueVarianceCalculator = new AccelerationExpectedValueVarianceCalculator();
        List<ExpectedValueVarianceResult> calc = accelerationExpectedValueVarianceCalculator
            .Calc(acceleratorStates, Duration.ofSeconds(10), 25);

        double threshold = 200;
        boolean belowThreshold = false;
        List<LocalDateTime> foundSplitPoints= new ArrayList<>();
        ExpectedValueVarianceResult lastDiffPerFrame= null;


        for (ExpectedValueVarianceResult diffPerFrame : calc) {
            if(lastDiffPerFrame== null){
                lastDiffPerFrame = diffPerFrame;
                foundSplitPoints.add(lastDiffPerFrame.getMiddletime());
            }else{

                long millis = Duration.between(lastDiffPerFrame.getMiddletime(), diffPerFrame.getMiddletime()).toMillis();

                boolean gapBetweenFrame = millis > 3100;

                if(gapBetweenFrame){
                    foundSplitPoints.add(diffPerFrame.getMiddletime());

                    double sum = diffPerFrame.getAccelerationSumPerSecond();
                    belowThreshold = sum < threshold;
                }else{
                    double sum = diffPerFrame.getAccelerationSumPerSecond();
                    boolean isNowBelowThreshold = sum < threshold;
                    if(belowThreshold &&  !isNowBelowThreshold){
                        // get above threshold
                        foundSplitPoints.add(diffPerFrame.getMiddletime());
                        belowThreshold = isNowBelowThreshold;

                    }else if(!belowThreshold &&  isNowBelowThreshold){
                        // get below threshold

                        foundSplitPoints.add(diffPerFrame.getMiddletime());
                        belowThreshold = isNowBelowThreshold;
                    }

                }
                lastDiffPerFrame = diffPerFrame;

            }
        }


        return foundSplitPoints;

    }








}
