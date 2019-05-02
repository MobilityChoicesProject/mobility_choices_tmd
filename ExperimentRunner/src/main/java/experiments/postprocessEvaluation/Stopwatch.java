package experiments.postprocessEvaluation;

/**
 * Created by Johannes on 13.08.2017.
 */
public class Stopwatch {


  long timeMillis = 0;

  long lastTimeMillis ;

  private boolean running = false;
  public void start(){
    long currentTimeMillis = System.currentTimeMillis();
    lastTimeMillis =currentTimeMillis;
    running = true;

  }


  public void stop(){
    long currentTimeMillis = System.currentTimeMillis();
    long diff = currentTimeMillis - lastTimeMillis;
    timeMillis +=diff;
    running = false;
  }

  public long millis(){
    if(running){
      long currentTimeMillis = System.currentTimeMillis();
      long diff = currentTimeMillis - lastTimeMillis;
       return timeMillis+diff;

    }else{
      return timeMillis;
    }
  }



}
