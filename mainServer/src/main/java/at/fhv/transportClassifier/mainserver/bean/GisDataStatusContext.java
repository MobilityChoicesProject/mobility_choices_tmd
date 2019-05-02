package at.fhv.transportClassifier.mainserver.bean;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import javax.persistence.EntityManager;

public class GisDataStatusContext {

  private boolean isInitialized= false;
  private List<GisDataUpdateErrors> gisDataUpdateErrorList = new ArrayList<>();


  private boolean isUpdatingNow= false;
  private double percentage;
  private double progressPercentage;
  private LocalDateTime lastUpdateTime;
  private boolean hasData;


  public void startUpdate(){
    isUpdatingNow=true;
  }

  public void stopUpdate(){
    isUpdatingNow=false;
  }


  public void setProgress(int done,int all){
     percentage = ((double)done)/all;
  }


  private void init(EntityManager entityManager){
    if(!isInitialized){
      isInitialized= true;


      // update lastUpdateTime



      // update hasData



    }else{

    }

  }

  public GisUpdateStatus getStatus(){

    GisUpdateStatusImp gisUpdateStatus = new GisUpdateStatusImp();
    gisUpdateStatus.setUpdating(isUpdatingNow);
    gisUpdateStatus.setHasData(hasData);
    gisUpdateStatus.setGetLastUpdateTime(lastUpdateTime);
    gisUpdateStatus.setProgressPercentage(progressPercentage);
    gisUpdateStatus.setGisDataUpdateErrors(gisDataUpdateErrorList);
    return gisUpdateStatus;
  }




}
