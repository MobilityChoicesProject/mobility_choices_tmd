package at.fhv.transportdetector.trackingtypes.builder;


import at.fhv.tmd.common.IGpsPoint;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 15.02.2017.
 */
public class SimpleBoundingBox implements at.fhv.transportdetector.trackingtypes.BoundingBox {


    private double southLatitude;
    private double westLongitude;
    private double northLatitude;
    private double eastLongitude;

    public SimpleBoundingBox(double southLatitude, double westLongitude, double northLatitude, double eastLongitude) {
        checkIfValidLatitude(southLatitude);
        checkIfValidLatitude(northLatitude);
        checkIfValidLongitude(eastLongitude);
        checkIfValidLongitude(westLongitude);

        this.southLatitude = southLatitude;
        this.westLongitude = westLongitude;
        this.northLatitude = northLatitude;
        this.eastLongitude = eastLongitude;
    }

    public SimpleBoundingBox(BoundingBox boundingBox){
       this(boundingBox.getSouthLatitude(),boundingBox.getWestLongitude(),boundingBox.getNorthLatitude(),boundingBox.getEastLongitude());
    }

    public SimpleBoundingBox(List<IExtendedGpsPoint> includedPoints){
        if(includedPoints.size()== 0){
            throw new IllegalArgumentException("at least one point must be included");
        }
        IExtendedGpsPoint gpsPoint = includedPoints.get(0);
        BoundingBox temp = new SimpleBoundingBox(gpsPoint.getLatitude(),gpsPoint.getLongitude(),gpsPoint.getLatitude(),gpsPoint.getLongitude());
        BoundingBox boundingBox =  extendBoundingBox(temp,includedPoints);
        this.southLatitude = boundingBox.getSouthLatitude();
        this.westLongitude = boundingBox.getWestLongitude();
        this.northLatitude = boundingBox.getNorthLatitude();
        this.eastLongitude = boundingBox.getEastLongitude();
    }

    @Override
    public BoundingBox extendBoundingBox(IExtendedGpsPoint gpsPoint){
        ArrayList arrayList = new ArrayList(1);
        arrayList.add(gpsPoint);
        return extendBoundingBox(this,arrayList);
    }
    @Override
    public BoundingBox extendBoundingBox(List<IExtendedGpsPoint> includedPoints){
       return  extendBoundingBox(this,includedPoints);
    }

    private BoundingBox extendBoundingBox(BoundingBox box, List<IExtendedGpsPoint> includedPoints) {
        Double minLatitude= box.getSouthLatitude(), maxLatitude = box.getNorthLatitude(),minLongitude = box.getWestLongitude(),maxLongitude = box.getEastLongitude();
        for (IExtendedGpsPoint includedPoint : includedPoints) {
            checkIfValidLongitude(includedPoint.getLongitude());
            checkIfValidLatitude(includedPoint.getLatitude());
                if(minLatitude > includedPoint.getLatitude()){
                    minLatitude = includedPoint.getLatitude();
                }else if(maxLatitude < includedPoint.getLatitude()){
                    maxLatitude = includedPoint.getLatitude();
                }

                if(minLongitude > includedPoint.getLongitude()){
                    minLongitude = includedPoint.getLongitude();
                }else if(maxLongitude < includedPoint.getLongitude()){
                    maxLongitude = includedPoint.getLongitude();
                }
        }
        return new SimpleBoundingBox(minLatitude,minLongitude,maxLatitude,maxLongitude);
    }

    private void checkIfValidLatitude(double value){
        if(value <0 ||value > 90){
            throw new IllegalArgumentException("Simple Bounding box only supports 0 - 90 latitude degree");
        }
    }

    private void checkIfValidLongitude(double value){
        if(value <0 ||value > 180){
            throw new IllegalArgumentException("Simple Bounding box only supports 0 - 180 longitude degree");
        }
    }



    @Override
    public double getSouthLatitude() {
        return southLatitude;
    }

    @Override
    public double getWestLongitude() {
        return westLongitude;
    }

    @Override
    public double getNorthLatitude() {
        return northLatitude;
    }

    @Override
    public double getEastLongitude() {
        return eastLongitude;
    }

    @Override
    public boolean contains(BoundingBox possibleContainedBoundingBox){
        BoundingBox box = possibleContainedBoundingBox;


//        double westLongitude = getWestLongitude();
//        if(getEastLongitude() > 0 && westLongitude <0 ){
//            westLongitude = 360+westLongitude;  //will be minus operation, because westlongitude is minus
//        }
    return  contains(possibleContainedBoundingBox,this);

    }

    @Override
    public boolean contains(IGpsPoint coordinate) {
        boolean insideLatitude = coordinate.getLatitude() > southLatitude && coordinate.getLatitude() < northLatitude;
        boolean insideLongitude = coordinate.getLongitude() > westLongitude && coordinate.getLatitude() < eastLongitude;



        return insideLatitude && insideLongitude;
    }

    @Override
    public boolean contains(double latitude, double longitude){
        boolean insideLatitude = latitude > southLatitude && latitude < northLatitude;
        boolean insideLongitude = longitude > westLongitude && longitude < eastLongitude;



        return insideLatitude && insideLongitude;
    }

    @Override
    public boolean containsWithLowerBorders(double latitude, double longitude) {
        boolean insideLatitude = latitude >= southLatitude && latitude < northLatitude;
        boolean insideLongitude = longitude >= westLongitude && longitude < eastLongitude;



        return insideLatitude && insideLongitude;    }

    private boolean contains(BoundingBox innerBox,BoundingBox outerBox){
        boolean eastLongitudeValid = innerBox.getEastLongitude() <= outerBox.getEastLongitude();
        boolean westLongitudeValid = innerBox.getWestLongitude() >= outerBox.getWestLongitude();

        boolean southLatitudeValid = innerBox.getSouthLatitude() >= outerBox.getSouthLatitude();
        boolean northLatitudeValid = innerBox.getNorthLatitude() <= outerBox.getNorthLatitude();


        return eastLongitudeValid && westLongitudeValid && southLatitudeValid && northLatitudeValid;
    }
}
