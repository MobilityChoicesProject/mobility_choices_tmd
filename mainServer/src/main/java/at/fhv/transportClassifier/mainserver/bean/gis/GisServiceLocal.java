package at.fhv.transportClassifier.mainserver.bean.gis;

import at.fhv.gis.CurrentUpdateStatus;
import at.fhv.gis.entities.db.GisDataUpdate;
import java.util.List;
import javax.ejb.Local;

@Local
public interface GisServiceLocal {

  void start();

  void cancel();

  void resume();

  CurrentUpdateStatus getCurrentStatus();

  List<? extends GisDataUpdate> get10LastStatus();
}
