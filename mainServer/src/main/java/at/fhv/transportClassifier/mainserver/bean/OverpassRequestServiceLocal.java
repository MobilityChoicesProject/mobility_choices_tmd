package at.fhv.transportClassifier.mainserver.bean;

import at.fhv.gis.OverpassDataType;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import java.io.File;
import java.util.concurrent.Future;
import javax.ejb.Asynchronous;
import javax.ejb.Local;

@Local
public interface OverpassRequestServiceLocal {


  @Asynchronous
  Future<File> getFile1(BoundingBox boundingBox, OverpassDataType overpassDataType);

  @Asynchronous
  Future<File> getFile(BoundingBox boundingBox, OverpassDataType overpassDataType);

}
