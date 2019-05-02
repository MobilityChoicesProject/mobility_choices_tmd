package at.fhv.gis.entities.db;

import java.time.LocalDateTime;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Table;
import org.hibernate.annotations.GenericGenerator;

@Entity
@Table(name = "gisdataupdate")
public class GisDataUpdateEntity implements GisDataUpdate {


  @javax.persistence.Id
  @GeneratedValue(generator="increment")
  @GenericGenerator(name="increment", strategy = "increment")
  @Column(name = "idGisDataUpdate")
  private long Id;

  @Column(name = "timestamp")
  private LocalDateTime timestamp;

  @Column(name = "southLatitude")
  private double southLatitude;
  @Column(name = "northLatitude")
  private double northLatitude;
  @Column(name = "westLongitude")
  private double westLongitude;
  @Column(name = "eastLongitude")
  private double eastLongitude;

  @Enumerated(EnumType.STRING)
  @Column(name = "Status")
  private GisDataUpdateStatusEntity Status;

  @Column(name = "latitudeTileSize")
  private double latitudeTileSize;

  @Column(name = "longitudeTileSize")
  private double longitudeTileSize;

  @Column(name = "numberOfTiles")
  private int numberOfTiles;

  @Column(name = "numberOfUpdatedTiles")
  private int numberOfUpdatedTiles;


  @Override
  public long getId() {
    return Id;
  }

  public void setId(long id) {
    Id = id;
  }

  @Override
  public LocalDateTime getTimestamp() {
    return timestamp;
  }

  public void setTimestamp(LocalDateTime timestamp) {
    this.timestamp = timestamp;
  }

  @Override
  public double getSouthLatitude() {
    return southLatitude;
  }


  @Override
  public double getNorthLatitude() {
    return northLatitude;
  }

  public void setNorthLatitude(double northLatitude) {
    this.northLatitude = northLatitude;
  }

  @Override
  public double getWestLongitude() {
    return westLongitude;
  }

  public void setWestLongitude(double westLongitude) {
    this.westLongitude = westLongitude;
  }

  @Override
  public double getEastLongitude() {
    return eastLongitude;
  }

  public void setEastLongitude(double eastLongitude) {
    this.eastLongitude = eastLongitude;
  }

  @Override
  public GisDataUpdateStatusEntity getStatus() {
    return Status;
  }

  public void setStatus(GisDataUpdateStatusEntity status) {
    this.Status = status;
  }


  public void setSouthLatitude(double southLatitude) {
    this.southLatitude = southLatitude;
  }

  public double getLatitudeTileSize() {
    return latitudeTileSize;
  }

  public void setLatitudeTileSize(double latitudeFrameSize) {
    this.latitudeTileSize = latitudeFrameSize;
  }

  public double getLongitudeTileSize() {
    return longitudeTileSize;
  }

  public void setLongitudeTileSize(double longitudeFrameSize) {
    this.longitudeTileSize = longitudeFrameSize;
  }

  public int getNumberOfTiles() {
    return numberOfTiles;
  }

  public void setNumberOfBoxes(int allFrames) {
    this.numberOfTiles = allFrames;
  }

  @Override
  public int getNumberOfUpdatedTiles() {
    return numberOfUpdatedTiles;
  }

  public void setNumberOfUpdatedFrames(int updatedFrames) {
    this.numberOfUpdatedTiles = updatedFrames;
  }
}
