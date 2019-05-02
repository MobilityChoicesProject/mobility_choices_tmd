package at.fhv.gis.entities.db;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import java.time.LocalDateTime;
import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * Created by Johannes on 24.05.2017.
 */
@Entity
@Table(name = "area")
public class GisArea {

  @Id
  @GeneratedValue(generator="increment")
  @GenericGenerator(name="increment", strategy = "increment")
  long id;

  String type;

  LocalDateTime updateTime;

  Point southWest;

  Point northEast;

  @OneToOne(fetch = FetchType.EAGER)
  @JoinColumn(name="gisdataupdate_idGisDataUpdate")
  private GisDataUpdateEntity gisUpdate;




  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public LocalDateTime getUpdateTime() {
    return updateTime;
  }

  public void setUpdateTime(LocalDateTime updateTime) {
    this.updateTime = updateTime;
  }

  public Point getSouthWest() {
    return southWest;
  }

  public void setSouthWest(Point southWest) {
    this.southWest = southWest;
  }

  public Point getNorthEast() {
    return northEast;
  }

  public void setNorthEast(Point northEast) {
    this.northEast = northEast;
  }

  public void setBoundingbox(double soutLatitude,double northLatitude,double westLongitude,double eastLongitude){
    com.vividsolutions.jts.geom.Point southWest = new GeometryFactory(new PrecisionModel(), 0).createPoint(new Coordinate(westLongitude, soutLatitude));
    com.vividsolutions.jts.geom.Point northEAst = new GeometryFactory(new PrecisionModel(), 0).createPoint(new Coordinate(eastLongitude, northLatitude));
    setNorthEast(northEAst);
    setSouthWest(southWest);
  }

  public void setGisUpdate(GisDataUpdateEntity gisUpdate) {
    this.gisUpdate = gisUpdate;
  }

  public GisDataUpdateEntity getGisUpdate() {
    return gisUpdate;
  }
}
