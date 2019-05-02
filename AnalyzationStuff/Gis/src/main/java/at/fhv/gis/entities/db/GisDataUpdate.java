package at.fhv.gis.entities.db;

import java.time.LocalDateTime;

public interface GisDataUpdate {

  long getId();

  LocalDateTime getTimestamp();

  double getNorthLatitude();

  double getWestLongitude();

  double getEastLongitude();

  GisDataUpdateStatusEntity getStatus();

  double getSouthLatitude();

  double getLatitudeTileSize();

  double getLongitudeTileSize();

  int getNumberOfTiles();

  int getNumberOfUpdatedTiles();
}
