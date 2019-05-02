package at.fhv.xychart;

/**
 * Created by Johannes on 23.01.2017.
 */
public abstract class SimpleDataPointsRecords implements DataPointRecords {

////    List<TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr> accelerometerTrackingPointsList;
//    List<Tuple<Double,Double>> dataPoints = new ArrayList<>();
//    private double lowerBound = 0;
//  private double upperBound = 0;
//
//  public SimpleDataPointsRecords(List<TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr> trackingPointPrs){
//            long timestamp = trackingPointPrs.get(0).getTimestamp();
//            long timeOffsetInMillis=0;
//            long lastTimeSTamp=0;
//            for (TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr trackingPointPr : trackingPointPrs) {
//                long currentTrackingTimestamp = trackingPointPr.getTimestamp();
//               long diffToLast = currentTrackingTimestamp-lastTimeSTamp;
//                if(diffToLast>0.03){
//                    int b= 3;
//                }
//                timeOffsetInMillis=  currentTrackingTimestamp- timestamp;
//                dataPoints.add(new Tuple<Double,Double>((double)timeOffsetInMillis/1000.0,
//                    getAxis(trackingPointPr)));
//
//
//                lastTimeSTamp = currentTrackingTimestamp;
//            }
//            upperBound = timeOffsetInMillis/1000.0;
//
//
//    }
//
//    protected abstract  double getAxis(TrackingDataProtos.TrackingPr.AccelerometerTrackingPointPr trackingPointPr);
//
//    @Override
//    public double getLowerBound() {
//        return lowerBound;
//    }
//
//
//    @Override
//    public double getUpperBound() {
//        return  upperBound;
//    }
//
//    @Override
//    public double getRecordsCount() {
//        return dataPoints.size();
//    }
//
//    @Override
//    public Double getValue(double indexSecondInBounds) {
//
//            if(indexSecondInBounds <= upperBound && indexSecondInBounds >= lowerBound){
//
//                int indexOfTuple =Collections.binarySearch(dataPoints, new Tuple<>(indexSecondInBounds, 0.0), new Comparator<Tuple<Double, Double>>() {
//                    @Override
//                    public int compare(Tuple<Double, Double> o1, Tuple<Double, Double> o2) {
//                       double diff =o1.getItem1()-o2.getItem1();
//                        if(Math.abs(diff)<getMinTickSize()){
//                            return 0;
//                        }else{
//                            double ceiledValue= Math.ceil(diff);
//                            if(ceiledValue== -0.0){
//                                return -1;
//                            }else if(ceiledValue== 0){
//                               return 0;
//                            }else {
//                                return (int)ceiledValue;
//                            }
//                        }
//
//
//                    }
//                });
//
//                if(indexOfTuple <0 ){
//                    int indexToadd = Math.abs(indexOfTuple+1);
//                    return dataPoints.get(indexToadd).getItem2();
//                }
//                return dataPoints.get(indexOfTuple).getItem2();
//    //            double sizeToIndexInBounds = indexSecondInBounds - lowerBound;
//    //            double index =  sizeToIndexInBounds / minTickSize;
//    //            int index1= (int)index;
//    //
//    //            return dataPoints.get(index1).getItem2();
//
//            }else{
//                throw new IllegalArgumentException("indexInBounds is not in range");
//            }
//    }
//
//    @Override
//    public double getMinTickSize() {
//        return 0.02;
//    }
}
