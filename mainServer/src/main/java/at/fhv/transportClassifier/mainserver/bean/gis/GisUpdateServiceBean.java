package at.fhv.transportClassifier.mainserver.bean.gis;

import at.fhv.gis.CurrentUpdateStatus;
import at.fhv.gis.GisDataCreationService;
import at.fhv.gis.GisDataDao;
import at.fhv.gis.Overpass.GisDataCreationException;
import at.fhv.gis.entities.db.GisDataUpdate;
import at.fhv.transportClassifier.mainserver.transaction.BeanTransaction;
import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.ejb.ConcurrencyManagement;
import javax.ejb.ScheduleExpression;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.ejb.TransactionManagement;
import javax.ejb.TransactionManagementType;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.transaction.UserTransaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
@ConcurrencyManagement(value = javax.ejb.ConcurrencyManagementType.BEAN)
@TransactionManagement(value= TransactionManagementType.BEAN)
public class GisUpdateServiceBean implements GisUpdateServiceLocal {

  private static Logger logger = LoggerFactory.getLogger(GisUpdateServiceBean.class);


  public static final String INIT = "Init";
  public static final String START = "Start";
  public static final String STOP = "Stop";
  public static final String RESUME = "Resume";

  @Resource
  private TimerService timerService;


  @PersistenceContext(unitName = "persistence_context_mysql")
  private EntityManager em;

  @Resource
  private UserTransaction ut;


  private Timer calendarTimer;
  private GisDataCreationService gisDataCreationService;
  private GisDataDao gisDataDao;

  public void test(){
    ManagedScheduledExecutorService managedExecutorService = null;

    ScheduledFuture<?> schedule = managedExecutorService.schedule(() -> {
    }, 100, TimeUnit.SECONDS);
    try {
      schedule.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

  }


  @PostConstruct
  public void initialize(){
    gisDataDao = new GisDataDao();
    gisDataCreationService= new GisDataCreationService(gisDataDao);

    SchedulePlan schedulePlan = new SchedulePlan();
    schedulePlan.setHour("3");
    schedulePlan.setDayOfMonth("1");
    schedulePlan.setYear("*");
    setTimerSchedule(schedulePlan);
    timerService.createTimer(3000, INIT);


  }

  @Override
  public List<? extends GisDataUpdate> get10LastStatus(){
    List<? extends GisDataUpdate> lastUpdates  = new LinkedList<>();

    try{
      gisDataDao.init(em,new BeanTransaction(ut));
      lastUpdates = gisDataCreationService.get10LastUpdates();

    }catch (GisDataCreationException ex){
      logger.error("There was an error during the execution of the get10LastSTatusMethod",ex);
    }
    return lastUpdates;

  }


  @Override
  public CurrentUpdateStatus getCurrentUpdateStatus(){
    return gisDataCreationService.getCurrentUpdateStatus();
  }


  @Override
  public void startUpdate(){
    timerService.createTimer(10, START);
  }

  @Override
  public void stopUpdate(){
    timerService.createTimer(10, STOP);
  }

  @Override
  public void resumeUpdate(){
    timerService.createTimer(10, RESUME);
  }


  @Override
  public void setTimerSchedule(SchedulePlan schedulePlan){
    ScheduleExpression expression = schedulePlan.generateScheduleExpression();
    Timer newCalendarTimer = timerService.createCalendarTimer(expression);
    if(calendarTimer!= null){
      calendarTimer.cancel();
      calendarTimer= null;
    }
    calendarTimer = newCalendarTimer;

  }


  @Timeout
  public void onTimerTimeout(Timer timer){

    gisDataDao.init(em,new BeanTransaction(ut));

    Serializable info = timer.getInfo();
    String origin  = (String) info;
    if(INIT.endsWith(origin)){
      gisDataCreationService.init();
    }else if(START.equals(origin)){
      gisDataCreationService.start();

    }else if(STOP.equals(origin)){
      gisDataCreationService.stop();

    }else if(RESUME.equals(origin)){
      gisDataCreationService.resume();

    }
  }


}
