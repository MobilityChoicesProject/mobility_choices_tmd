package at.fhv.tmd.common;

/**
 * Created by Johannes on 12.03.2017.
 */
public class Speed {



    public Speed(double speedKmPerHour) {
        this.speedKmPerHour = speedKmPerHour;
    }


    private double speedKmPerHour;


    public double getKmPerHour(){
        return speedKmPerHour;
    }


    public double getMeterPerSecond(){
        return speedKmPerHour*1000/60/60;
    }


    public Speed plus(Speed speed) {
        return new Speed(speedKmPerHour+speed.getKmPerHour());

    }

    public boolean isFaster(Speed speed) {

        return getKmPerHour()>speed.getKmPerHour();
    }
}
