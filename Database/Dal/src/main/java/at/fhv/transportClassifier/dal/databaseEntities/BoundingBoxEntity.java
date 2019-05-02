package at.fhv.transportClassifier.dal.databaseEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * Created by Johannes on 15.02.2017.
 */
@Entity
@Table(name = "BoundingBox")
public class BoundingBoxEntity {

    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Id
    @Column(name = "idBoundingBox")
    private int id;



    @Column(name="southLatitude")
    private double southLatitude;
    @Column(name="westLongitude")
    private double westLongitude;

    @Column(name="northLatitude")
    private double northLatitude;

    @Column(name="eastLongitude")
    private double eastLongitude;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public double getSouthLatitude() {
        return southLatitude;
    }

    public void setSouthLatitude(double southLatitude) {
        this.southLatitude = southLatitude;
    }

    public double getWestLongitude() {
        return westLongitude;
    }

    public void setWestLongitude(double westLongitude) {
        this.westLongitude = westLongitude;
    }

    public double getNorthLatitude() {
        return northLatitude;
    }

    public void setNorthLatitude(double northLatitude) {
        this.northLatitude = northLatitude;
    }

    public double getEastLongitude() {
        return eastLongitude;
    }

    public void setEastLongitude(double eastLongitude) {
        this.eastLongitude = eastLongitude;
    }
}
