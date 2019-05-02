package helper;

import at.fhv.context.TrackingContext;
import at.fhv.transportdetector.trackingtypes.Constants;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.HashMap;

/**
 * Created by Johannes on 31.07.2017.
 */
public class TmdServiceCache {

  private boolean enabled = true;


  String path;
  private HashMap<Long,File> fileCache  = new HashMap<>();

  public TmdServiceCache(String folderStr){
    String cacheFolder = PropertyHelper.getValue(Constants.dataFolder_cache);
    path=cacheFolder+folderStr;
  }


  public boolean isEnabled() {
    return enabled;
  }

  public void setEnabled(boolean enabled) {
    this.enabled = enabled;
  }

  public void initCache(){
    File folder = new File(path);
    if(!folder.exists()){
      folder.mkdirs();
    }
    File[] files = folder.listFiles();
    if(files== null){
      return;
    }
    for (File file : files) {
      String name = file.getName();
      int endIndex = name.indexOf('.');
      String substring = name.substring(0, endIndex);
      long id = Integer.parseInt(substring);
      fileCache.put(id,file);
    }
  }



  public TrackingContext getFromCache(Long id) throws IOException, ClassNotFoundException {

    if(!enabled){
      return null;
    }
    File file = fileCache.get(id);
    if(file!= null){
      TrackingContext trackingContext;
      FileInputStream fis = new FileInputStream(file);
      ObjectInputStream ois = new ObjectInputStream(fis);
      trackingContext = (TrackingContext) ois.readObject();
      ois.close();
      fis.close();
      return trackingContext;

    }else{
      return null;
    }
  }


  public void saveToCache(TrackingContext trackingContext) throws IOException {
    if(!isEnabled()){
      return;
    }
    String fileName = trackingContext.getTrackingId() + ".ser";
    String filePath = path+fileName;

    FileOutputStream fos =
        new FileOutputStream(filePath);
    ObjectOutputStream oos = new ObjectOutputStream(fos);
    oos.writeObject(trackingContext);
    oos.close();
    fos.close();
  }


}
