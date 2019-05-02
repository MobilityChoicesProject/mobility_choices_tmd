package at.fhv.tmd.common;

/**
 * Created by Johannes on 13.03.2017.
 */
public class Distance {

    private double km;


    public Distance(double km) {
        this.km = km;
    }

    public double getKm() {
        return km;
    }

    public double getMeter(){
        return km*1000;
    }

    public Distance plus(Distance distance){
        return new Distance(this.km+distance.getKm());
    }

    public Distance minus(Distance distance) {
        return new Distance(this.km-distance.getKm());

    }

    public boolean isLongerThan(Distance distance){
        return Double.compare(km, distance.km) > 0;
    }
}
