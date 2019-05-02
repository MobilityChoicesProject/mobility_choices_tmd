package at.fhv.transportClassifier.mainserver.bean.gis;

import at.fhv.gis.CurrentUpdateStatus;
import at.fhv.gis.entities.db.GisDataUpdate;
import java.util.List;
import javax.ejb.Local;

@Local
public interface GisUpdateServiceLocal {

  List<? extends GisDataUpdate> get10LastStatus();

  CurrentUpdateStatus getCurrentUpdateStatus();

  void startUpdate();

  void stopUpdate();

  void resumeUpdate();

  void setTimerSchedule(SchedulePlan schedulePlan);
}
