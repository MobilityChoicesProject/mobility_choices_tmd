package at.fhv.transportClassifier.mainserver.bean;

import java.time.LocalDateTime;

public class GisDataUpdateErrors {

  private boolean isDismissed;

  private LocalDateTime time;

  private String errorMessage;



  public boolean isDismissed() {
    return isDismissed;
  }

  public void setDismissed(boolean dismissed) {
    isDismissed = dismissed;
  }

  public LocalDateTime getTime() {
    return time;
  }

  public void setTime(LocalDateTime time) {
    this.time = time;
  }

  public String getErrorMessage() {
    return errorMessage;
  }

  public void setErrorMessage(String errorMessage) {
    this.errorMessage = errorMessage;
  }
}
