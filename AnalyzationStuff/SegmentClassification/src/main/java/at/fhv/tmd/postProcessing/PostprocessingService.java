package at.fhv.tmd.postProcessing;

import at.fhv.context.TrackingContext;
import at.fhv.transportClassifier.common.configSettings.ConfigService;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Johannes on 19.07.2017.
 */
public class PostprocessingService implements ConfigServiceUpdateable {

  List<PostprocessTask> tasks;
  private ConfigService configService;

  public void updateConfigService(ConfigService configService){
    this.configService = configService;
  }

  public void setTasks(List<PostprocessTask> tasks){
    this.tasks =new ArrayList<>(tasks);
    this.tasks.sort((o1, o2) -> o1.getPriorityLevel()-o2.getPriorityLevel());
  }


  public void process(TrackingContext trackingContext){

    for (PostprocessTask task : tasks) {
      if(task instanceof ConfigServiceUpdateable){
        ((ConfigServiceUpdateable) task).updateConfigService(configService);
      }

      task.process(trackingContext);
    }


  }


}
