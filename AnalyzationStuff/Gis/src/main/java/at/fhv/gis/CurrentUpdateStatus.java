package at.fhv.gis;

import at.fhv.transportdetector.trackingtypes.BoundingBox;

public class CurrentUpdateStatus {

  private boolean updating;
    private int updatedTiles;
  private int allTiles;
  private BoundingBox boundingBox;
  private double latitudeTileSize;
  private double longitudeTileSize;

  public boolean isUpdating() {
    return updating;
  }

  public void setUpdating(boolean updating) {
    this.updating = updating;
  }


  public int getUpdatedTiles() {
    return updatedTiles;
  }

  public void setUpdatedTiles(int updatedTiles) {
    this.updatedTiles = updatedTiles;
  }

  public int getAllTiles() {
    return allTiles;
  }

  public void setAllTiles(int allTiles) {
    this.allTiles = allTiles;
  }

  public BoundingBox getBoundingBox() {
    return boundingBox;
  }

  public void setBoundingBox(BoundingBox boundingBox) {
    this.boundingBox = boundingBox;
  }

  public double getLatitudeTileSize() {
    return latitudeTileSize;
  }

  public void setLatitudeTileSize(double latitudeTileSize) {
    this.latitudeTileSize = latitudeTileSize;
  }

  public double getLongitudeTileSize() {
    return longitudeTileSize;
  }

  public void setLongitudeTileSize(double longitudeTileSize) {
    this.longitudeTileSize = longitudeTileSize;
  }
}
