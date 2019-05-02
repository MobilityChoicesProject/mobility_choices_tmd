package at.fhv.transportClassifier.mainserver.bean;

import java.time.LocalDateTime;
import java.util.List;

public interface GisUpdateStatus {

  List<GisDataUpdateErrors> getUpdateErrors();
  boolean hasData();

  LocalDateTime getLastUpdateTime();
  boolean isUpdatingNow();
  double getUpdateProgressPercentage();


}
