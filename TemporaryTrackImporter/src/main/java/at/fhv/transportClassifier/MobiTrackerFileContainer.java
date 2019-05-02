package at.fhv.transportClassifier;

import at.fhv.tmd.common.Tuple;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by Johannes on 19.05.2017.
 *
 * Contains all files to put a whole Tracking together
 */
public class MobiTrackerFileContainer {

  private String commonName;
  private List<Tuple<Integer,File>> files = new ArrayList<>();
  private boolean isSorted =false;

  public boolean isSameTracking(File file){
    String name = file.getName();
    int indexOfCommonPart = name.indexOf('_', 0);
    String commonPartString = name.substring(indexOfCommonPart);
    return commonPartString.equals(commonName);
  }

  public static boolean isMobiTrackerFile(File file){
    String name = file.getName();
    return name.contains("rc.");

  }

  public MobiTrackerFileContainer(File file){
    String name = file.getName();
    if (!isMobiTrackerFile(file)) {
      throw new IllegalArgumentException("File seems not to be a valid MobiTracker File");
    }
    int indexOfCommonPart = name.indexOf('_', 0);
    String commonPartString = name.substring(indexOfCommonPart);
    commonName = commonPartString;
    String numberStr = name.substring(0, indexOfCommonPart);
    int number = Integer.parseInt(numberStr);
    files.add(new Tuple<>(number,file));

  }


  public void addFile(File file){

    String name = file.getName();

    if(!isSameTracking(file)){
      throw new IllegalArgumentException("File '"+name+"' seems not to be part of the same tracking '"+commonName+"'");
    }
    int indexOfCommonPart = name.indexOf('_', 0);

    String numberStr = name.substring(0, indexOfCommonPart);
    int number = Integer.parseInt(numberStr);

    isSorted = false;
    files.add(new Tuple<Integer,File>(number,file));
  }

  public List<File> getAllFiles(){
    if(!isSorted){
      files.sort((o1, o2) -> o1.getItem1().compareTo(o2.getItem1()));
      isSorted = true;
    }

    List<File> returnFiles =new ArrayList<>();
    for (Tuple<Integer, File> file : files) {
      returnFiles.add(file.getItem2());
    }
    return returnFiles;
  }

}
