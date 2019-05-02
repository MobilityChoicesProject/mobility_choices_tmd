package at.fhv.gis.entities.db;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.GeometryFactory;
import com.vividsolutions.jts.geom.Point;
import com.vividsolutions.jts.geom.PrecisionModel;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

/**
 * Created by Johannes on 23.05.2017.
 */
@Entity
@Table(name = "point")
public class GisPoint {

  @Id
  @GeneratedValue(generator="increment")
  @GenericGenerator(name="increment", strategy = "increment")
  private long id;

  private long area_id;

  private Point position;

  private int pointType;

  @Column(name = "gisdataupdatestatus_id")
  private long gisDataUpdateId;

  public int getPointType() {
    return pointType;
  }

  public void setPointType(int pointType) {
    this.pointType = pointType;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public long getArea_id() {
    return area_id;
  }

  public void setArea_id(long area_id) {
    this.area_id = area_id;
  }

  public Point getPosition() {
    return position;
  }

  public void setPosition(Point position) {
    this.position = position;
  }

  public void setPositionByDouble(double latitude,double longitude){
    com.vividsolutions.jts.geom.Point p = new GeometryFactory(new PrecisionModel(), 0).createPoint(new Coordinate(longitude, latitude));
    position =p;
  }

  public double getLatitude(){
    return position.getY();
  }
  public double getLongitude(){
    return position.getX();
  }
  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }

    GisPoint gisPoint = (GisPoint) o;

    if (getId() != gisPoint.getId()) {
      return false;
    }
    if (getArea_id() != gisPoint.getArea_id()) {
      return false;
    }
    return getPosition().equals(gisPoint.getPosition());
  }

  @Override
  public int hashCode() {
    int result = (int) (getId() ^ (getId() >>> 32));
    result = 31 * result + (int) (getArea_id() ^ (getArea_id() >>> 32));
    result = 31 * result + getPosition().hashCode();
    return result;
  }

  public void setGisDataUpdateId(long gisDataUpdateId) {
    this.gisDataUpdateId = gisDataUpdateId;
  }

  public long getGisDataUpdateId() {
    return gisDataUpdateId;
  }
}
