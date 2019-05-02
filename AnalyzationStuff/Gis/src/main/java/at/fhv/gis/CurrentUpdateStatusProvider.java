package at.fhv.gis;

import at.fhv.transportdetector.trackingtypes.BoundingBox;
import at.fhv.transportdetector.trackingtypes.builder.SimpleBoundingBox;
import java.util.concurrent.locks.ReentrantLock;

public class CurrentUpdateStatusProvider {

  private ReentrantLock lock = new ReentrantLock();
  private int updatedTiles;
  private int allTiles;
  private BoundingBox boundingBox;
  private double latitudeTileSize;
  private double longitudeTileSize;
  private boolean updating;

  public void setUpdatedTiles(int tiles){
    lock.lock();
    updatedTiles = tiles;
    lock.unlock();
  }
  public void incrementUpdatedTiles(){
    lock.lock();
    updatedTiles=updatedTiles+1;
    lock.unlock();

  }

  public void setAllTiles(int allTiles){
    lock.lock();
    this.allTiles = allTiles;
    lock.unlock();

  }

  public void setBoundingBox(BoundingBox boundingBox){
    lock.lock();
    this.boundingBox = boundingBox;
    lock.unlock();

  }

  public void setLatitudeTileSize(double latitudeTileSize){
    lock.lock();
    this.latitudeTileSize = latitudeTileSize;
    lock.unlock();

  }
  public void setLongitudeTileSize(double longitudeTileSize){
    lock.lock();
    this.longitudeTileSize = longitudeTileSize;
    lock.unlock();
  }

  public void setRunning(boolean updating){
    lock.lock();
    this.updating = updating;
    lock.unlock();
  }



  public CurrentUpdateStatus getCurrentUpdateStatus() {
    lock.lock();

    CurrentUpdateStatus currentUpdateStatus = new CurrentUpdateStatus();
    try {
      if (updating) {
        currentUpdateStatus.setAllTiles(allTiles);
        currentUpdateStatus.setUpdatedTiles(updatedTiles);
        currentUpdateStatus.setLongitudeTileSize(longitudeTileSize);
        currentUpdateStatus.setLatitudeTileSize(latitudeTileSize);
        currentUpdateStatus.setBoundingBox(new SimpleBoundingBox(boundingBox));
      }
      currentUpdateStatus.setUpdating(updating);

    }finally {
      lock.unlock();
    }
    return currentUpdateStatus;
  }
}
