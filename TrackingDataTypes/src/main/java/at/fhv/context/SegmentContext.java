package at.fhv.context;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.NoSuchElementException;

/**
 * Created by Johannes on 19.07.2017.
 */
public class SegmentContext implements Serializable{
  private static final long serialVersionUID = -4011541940424312738l;

  public static final String FEATURES= "features";
  public static final String CLASSIFICATION_RESULT= "classification_result";
  public static final String PRE_TYPE= "PRE_TYPE";
  public static final String POST_PROCESS_RESULT= "POST_PROCESS_RESULT";

  private SegmentContext previousContext;
  private SegmentContext nextContext;

  private HashMap<String,Serializable> data = new HashMap<>();
  private LocalDateTime endTime;
  private LocalDateTime startTime;

  public void setPreviousContext(SegmentContext previousContext) {
    this.previousContext = previousContext;
  }

  public void setNextContext(SegmentContext nextContext) {
    this.nextContext = nextContext;
  }

  public boolean hasPreviousContext(){
    return previousContext!= null;
  }

  public boolean hasNextContext(){
    return nextContext!= null;
  }

  public SegmentContext getPreviousContext() {
    return previousContext;
  }

  public SegmentContext getNextContext() {
    return nextContext;
  }

  public void addData(String dataName,Serializable data){
    this.data.put(dataName,data);
  }

  public boolean hasData(String dataName){
    return data.get(dataName) != null;
  }

  public <T> T getData(String dataName){
    Object o = data.get(dataName);
    T tReturn;
    try{
     tReturn = (T)o;
   }catch (Exception ex){
     throw new NoSuchElementException("No element for dataName:'"+dataName+"' and the generic type");
   }
   return tReturn;
  }


  public LocalDateTime getEndTime() {
    return endTime;
  }

  public LocalDateTime getStartTime() {
    return startTime;
  }

  public void setStartTime(LocalDateTime startTime) {
    this.startTime = startTime;
  }

  public void setEndTime(LocalDateTime endTime) {
    this.endTime = endTime;
  }
}
