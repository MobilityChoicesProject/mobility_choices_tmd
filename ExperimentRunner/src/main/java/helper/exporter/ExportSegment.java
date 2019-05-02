package helper.exporter;

import at.fhv.tmd.common.ICoordinate;
import at.fhv.tmd.common.IGpsPoint;
import java.util.List;

public class ExportSegment {

  private List<? extends IGpsPoint> gpsPoints;

  public void setGpsPoints(List<? extends IGpsPoint> gpsPoints) {
    this.gpsPoints = gpsPoints;
  }

  public List<? extends IGpsPoint> getGpsPoints() {
    return gpsPoints;
  }
}
