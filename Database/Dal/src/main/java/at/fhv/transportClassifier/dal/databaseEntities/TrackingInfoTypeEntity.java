package at.fhv.transportClassifier.dal.databaseEntities;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
/**
 * Created by Johannes on 13.02.2017.
 */
@Entity
@Table(name = "trackinginfotype")
public class TrackingInfoTypeEntity {

    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Id
    @Column(name = "idTrackingInfoType")
    private int id;

    @Column(name="name")
    private String name;

    public TrackingInfoTypeEntity() {
    }

    public TrackingInfoTypeEntity(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
