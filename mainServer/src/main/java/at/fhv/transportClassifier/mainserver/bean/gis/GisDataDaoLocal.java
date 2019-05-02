package at.fhv.transportClassifier.mainserver.bean.gis;

import at.fhv.gis.entities.db.GisArea;
import at.fhv.gis.entities.db.GisDataUpdateEntity;
import at.fhv.gis.entities.db.GisPoint;
import java.util.List;
import javax.ejb.Local;

@Local
public interface GisDataDaoLocal {

  void initGisData(GisDataUpdateEntity gisDataUpdateEntity);

  void updateGisData(GisDataUpdateEntity gisDataUpdateEntity, GisArea gisArea,
      Iterable<GisPoint> gisPoints);

  void activateStatus(GisDataUpdateEntity gisDataUpdateEntity);

  List<GisDataUpdateEntity> getLast10DataUpdates();
}
