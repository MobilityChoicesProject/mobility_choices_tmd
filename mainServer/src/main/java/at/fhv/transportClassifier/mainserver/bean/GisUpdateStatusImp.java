package at.fhv.transportClassifier.mainserver.bean;

import java.time.LocalDateTime;
import java.util.List;

public class GisUpdateStatusImp implements GisUpdateStatus {

  private boolean isUpdating;
  private boolean hasData;
  private LocalDateTime getLastUpdateTime;
  private double progressPercentage;
  private List<GisDataUpdateErrors> gisDataUpdateErrors;



  @Override
  public List<GisDataUpdateErrors> getUpdateErrors() {
    return gisDataUpdateErrors;
  }

  @Override
  public boolean hasData() {
    return hasData;
  }

  @Override
  public LocalDateTime getLastUpdateTime() {
    return getLastUpdateTime;
  }

  @Override
  public boolean isUpdatingNow() {
    return isUpdating;
  }

  @Override
  public double getUpdateProgressPercentage() {
    return progressPercentage;
  }

  public void setUpdating(boolean updating) {
    isUpdating = updating;
  }

  public void setHasData(boolean hasData) {
    this.hasData = hasData;
  }

  public void setGetLastUpdateTime(LocalDateTime getLastUpdateTime) {
    this.getLastUpdateTime = getLastUpdateTime;
  }

  public void setProgressPercentage(double progressPercentage) {
    this.progressPercentage = progressPercentage;
  }

  public void setGisDataUpdateErrors(
      List<GisDataUpdateErrors> gisDataUpdateErrors) {
    this.gisDataUpdateErrors = gisDataUpdateErrors;
  }
}
