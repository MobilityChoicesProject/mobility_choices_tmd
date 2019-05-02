package at.fhv.transportClassifier.mainserver.bean.gis;

import java.io.*;

public class OverpassEvent {

  public boolean succesfull;
  public File file;

  public boolean isSuccesfull() {
    return succesfull;
  }

  public void setSuccesfull(boolean succesfull) {
    this.succesfull = succesfull;
  }

  public File getFile() {
    return file;
  }

  public void setFile(File file) {
    file = file;
  }
}
