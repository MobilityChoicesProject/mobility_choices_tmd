package at.fhv.transportClassifier.mainserver.bean;

import at.fhv.gis.OverpassDataException;
import at.fhv.gis.OverpassDataType;
import at.fhv.transportClassifier.mainserver.bean.gis.OverpassEvent;
import at.fhv.transportdetector.trackingtypes.BoundingBox;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import javax.annotation.Resource;
import javax.ejb.AsyncResult;
import javax.ejb.Asynchronous;
import javax.ejb.Singleton;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;
import javax.enterprise.concurrent.ManagedExecutorService;
import javax.enterprise.concurrent.ManagedScheduledExecutorService;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Singleton
public class OverpassRequestServiceBean implements OverpassRequestServiceLocal/**/{

  public static final String WAIT_FOR_NEXT_TRY = "WaitForNextTry";
  public static final String KILL_REQUEST = "KillRequest";
  private final long maxTimeMillis = 120000;
  private static Logger logger = LoggerFactory.getLogger(OverpassRequestServiceBean.class);


  @Resource
  TimerService timerService;

  @Inject
  Event<OverpassEvent> overpassEvent;

  @Resource
  ManagedScheduledExecutorService managedExecutorService;

  private HttpGet httpGet;
  private File file;


  protected HttpResponse getHttpResponse(HttpClient httpClient, HttpGet httpGet) throws IOException {

    Timer killRequest = timerService.createTimer(maxTimeMillis, KILL_REQUEST);
    this.httpGet = httpGet;
    HttpResponse execute;
    try{
       execute = httpClient.execute(httpGet);
    }finally {
      killRequest.cancel();
      this.httpGet=null;
    }
    return execute;
  }

  protected static final String host ="http://overpass-api.de/api/interpreter?data=";

  protected static final String busRouteStart = ("relation[route=bus][\"name\"!~\"^(.|\\r|\\n)*(F|f)ernbus(.|\\r|\\n)*$\"][\"operator\"!~\"^(.|\\r|\\n)*(F|f)ernbus(.|\\r|\\n)*$\"][\"network\"!~\"^(.|\\r|\\n)*(F|f)ernbus(.|\\r|\\n)*$\"](");
  protected static final String busRoute2 = (");(._;>;);out;");

  protected static final String railway1 = ("way[railway](");
  protected static final String railway2 = (");(._;>;);out;");

  protected static final String busstops1 = "(node[public_transport=stop_position][bus=yes](";
  protected static final String busstops2 = (");node[\"highway\"=\"bus_stop\"](");
  protected static final String busstops3 = ("););out;");

  protected static final String trainStationStop1 = "(node[public_transport=stop_position][train=yes](";
  protected static final String trainStationStop2 = ");node[\"railway\"=\"station\"](";
  protected static final String trainStationStop3 = "););out;";



  protected int maxTries = 3;
  protected File folder;
  protected DateTimeFormatter simpleDateFormat =DateTimeFormatter.ofPattern("dd_MM_yyyy");
  protected Random random = new Random(System.currentTimeMillis());


  private ConcurrentLinkedQueue< Future> queue = new ConcurrentLinkedQueue();


  @Override
  @Asynchronous
  public Future<File> getFile1(BoundingBox boundingBox,OverpassDataType overpassDataType){

    Callable<Void> callable = () -> {
      createTemporaryFolder();

      String request = null;
      String queryData=null;

      queryData = getQueryData(boundingBox, overpassDataType);

      try {
        request = host+  URLEncoder.encode(queryData, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException("Seems like there is a bug.");
      }

      logger.info("send request to overpass server");
      sendRequestToServer1(request,0);
      logger.info("get response from overpass server");
      return null;
    };

    Future schedule = managedExecutorService.submit(callable);
    queue.add(schedule);


    try {
      while (!queue.isEmpty()){
        Future poll = queue.poll();
        poll.get();
      }

      schedule.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    CompletableFuture<File> fileCompletableFuture = CompletableFuture.supplyAsync(() -> {
     return file;
    }, managedExecutorService);


    return fileCompletableFuture;

  }

  @Override
  @Asynchronous
  public Future<File> getFile(BoundingBox boundingBox, OverpassDataType overpassDataType){

    Callable<Void> callable = () -> {
      createTemporaryFolder();

      String request = null;
      String queryData=null;

      queryData = getQueryData(boundingBox, overpassDataType);

      try {
        request = host+  URLEncoder.encode(queryData, "UTF-8");
      } catch (UnsupportedEncodingException e) {
        throw new IllegalStateException("Seems like there is a bug.");
      }

      logger.info("send request to overpass server");
       sendRequestToServer1(request,0);
      logger.info("get response from overpass server");
      return null;
    };

    Future schedule = managedExecutorService.submit(callable);
    queue.add(schedule);


    try {
      while (!queue.isEmpty()){
        Future poll = queue.poll();
        poll.get();
      }

      schedule.get();
    } catch (InterruptedException e) {
      e.printStackTrace();
    } catch (ExecutionException e) {
      e.printStackTrace();
    }

    return new AsyncResult<File>(file);
  }


  protected void createTemporaryFolder() {
    String property = System.getProperty("java.io.tmpdir");
    folder = new File(property, "overpassCache");
    logger.info("creating tmpdir {}",folder.getPath());
    folder.mkdirs();
    logger.info("created tmpdir {}",folder.getPath());
  }

  protected String getQueryData(BoundingBox boundingBox, OverpassDataType overpassDataType) {
    String queryData;
    if(overpassDataType == OverpassDataType.BusRoutes){
      queryData= busRouteStart + getString(boundingBox) + busRoute2;
    }else if(overpassDataType == OverpassDataType.BusStops){
      queryData= busstops1 + getString(boundingBox) + busstops2 + getString(boundingBox) +busstops3;
    }else if(overpassDataType == OverpassDataType.RailwayRoutes){
      queryData= railway1 + getString(boundingBox) + railway2 ;
    }else if(overpassDataType == OverpassDataType.RailwayStops){
      queryData= trainStationStop1 + getString(boundingBox) + trainStationStop2+ getString(boundingBox) +trainStationStop3;
    }else{
      throw new IllegalArgumentException("OverpassDataType not supported: "+overpassDataType.name());
    }
    return queryData;
  }


  private void sendRequestToServer1(String request,int numberOfTry) throws OverpassDataException {
    CloseableHttpClient httpClient = HttpClients.createDefault();

    HttpGet httpGet = new HttpGet(request);
    HttpResponse response = null;

    try {
      int statusCode=0;
      logger.info("overpass request {} try ",numberOfTry);
      response = getHttpResponse(httpClient, httpGet);

      statusCode= response.getStatusLine().getStatusCode();
      if (statusCode == 200) {
        saveFile(response,request);
      }else{
        if(numberOfTry<maxTries){
          if (statusCode == 429) {
            int timmeMillisToWait = (int) (random.nextDouble() * 15000 + 10000);
            logger.info("server rejected request because too many request have been made recently.");
            logger.info("wait {} millis util next try",timmeMillisToWait);

            ScheduledFuture<?> schedule = managedExecutorService.schedule(() -> {
              try {
                sendRequestToServer1(request, numberOfTry + 1);
              } catch (OverpassDataException e) {
                e.printStackTrace();
              }
            }, timmeMillisToWait, TimeUnit.MILLISECONDS);
            queue.add(schedule);

          }else{
            logger.info("request try failed with statuscode {}",statusCode);
            sendRequestToServer(request,numberOfTry+1);
          }
        }else{
          // could not do it with all tries

        }
      }

    }   catch (ClientProtocolException e1) {
      throw new OverpassDataException(e1);
    } catch (IOException e1) {
      throw new OverpassDataException(e1);
    }finally {
      try {
        httpClient.close();
      } catch (IOException e) {
        throw new OverpassDataException(e);
      }
    }
  }



  private void sendRequestToServer(String request,int numberOfTry) throws OverpassDataException {
    CloseableHttpClient httpClient = HttpClients.createDefault();

    HttpGet httpGet = new HttpGet(request);
    HttpResponse response = null;

    try {
      int statusCode=0;
        logger.info("overpass request {} try ",numberOfTry);
        response = getHttpResponse(httpClient, httpGet);

        statusCode= response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
          saveFile(response,request);
        }else{
          if(numberOfTry<maxTries){
            if (statusCode == 429) {
              int timmeMillisToWait = (int) (random.nextDouble() * 15000 + 10000);
              logger.info("server rejected request because too many request have been made recently.");
              logger.info("wait {} millis util next try",timmeMillisToWait);
              TimerInfo timerContext  = new TimerInfo();
              timerContext.setNumberOfTry(numberOfTry);
              timerContext.setRequestUrl(request);
              timerContext.setTimerGoal(WAIT_FOR_NEXT_TRY);
              timerService.createTimer(timmeMillisToWait,timerContext);
            }else{
              logger.info("request try failed with statuscode {}",statusCode);
              sendRequestToServer(request,numberOfTry+1);
            }
          }else{
            // could not do it with all tries
            OverpassEvent overpassEvent = new OverpassEvent();
            overpassEvent.setSuccesfull(false);
            this.overpassEvent.fire(overpassEvent);
          }
        }

    }   catch (ClientProtocolException e1) {
      throw new OverpassDataException(e1);
    } catch (IOException e1) {
      throw new OverpassDataException(e1);
    }finally {
      try {
        httpClient.close();
      } catch (IOException e) {
        throw new OverpassDataException(e);
      }
    }
  }

  private void saveFile(HttpResponse response,String request) throws OverpassDataException {

    try {
      InputStream content = null;
      HttpEntity entity = response.getEntity();
      content = entity.getContent();

      logger.info("writing overpass response to file");
      File file = writeToFile(content, createName(request));
      logger.info("written overpass response to file");

     this.file = file;

    } catch (IOException e) {
      logger.error("There was an exception during the saving of the overpass file",e);
      throw new OverpassDataException(e);
    }

  }

  @Timeout
  public void timeout(Timer timer){
    Serializable info = timer.getInfo();
    TimerInfo timerInfo = (TimerInfo)info;

    String timerGoal = timerInfo.getTimerGoal();

    if(WAIT_FOR_NEXT_TRY.equals(timerGoal)){
      logger.info("waited for next try");
      try {
        sendRequestToServer(timerInfo.getRequestUrl(),timerInfo.getNumberOfTry()+1);
      } catch (OverpassDataException e) {
        logger.error("During the overpass request there was an exception",e);
        OverpassEvent overpassEvent = new OverpassEvent();
        overpassEvent.setFile(null);
        overpassEvent.setSuccesfull(false);
        this.overpassEvent.fire(overpassEvent);
      }
    }
    if(KILL_REQUEST.equals(timerGoal)){
      if(httpGet!= null){
        logger.info("aborting httpGet");
        httpGet.abort();
        logger.info("aborted httpGet");

      }
    }
  }



  private String createName(String request){
    int hashCode = request.hashCode();
    String name = hashCode+"_"+ LocalDateTime.now().format(simpleDateFormat);
    return name;
  }

  private File writeToFile(InputStream inputStream,String targetFile) throws IOException {

    File file = new File(folder,targetFile);
    if(file.exists()) {
      file.delete();
    }

    OutputStream outStream = new FileOutputStream(file.getAbsolutePath());
    byte[] buffer = new byte[8 * 1024];
    int bytesRead;
    while ((bytesRead = inputStream.read(buffer,0,8*1024)) != -1)
    {
      outStream.write(buffer, 0, bytesRead);
    }
    outStream.close();
    return file;
  }

  private String getString(BoundingBox boundingBox){
    StringBuilder stringBuilder = new StringBuilder();

    stringBuilder.append(boundingBox.getSouthLatitude());
    stringBuilder.append(",");
    stringBuilder.append(boundingBox.getWestLongitude());
    stringBuilder.append(",");
    stringBuilder.append(boundingBox.getNorthLatitude());
    stringBuilder.append(",");
    stringBuilder.append(boundingBox.getEastLongitude());

    return stringBuilder.toString();
  }



  protected static class TimerInfo implements Serializable{
    private int numberOfTry;
    private String requestUrl;
    private String timerGoal;

    public int getNumberOfTry() {
      return numberOfTry;
    }

    public void setNumberOfTry(int numberOfTry) {
      this.numberOfTry = numberOfTry;
    }

    public String getRequestUrl() {
      return requestUrl;
    }

    public void setRequestUrl(String requestUrl) {
      this.requestUrl = requestUrl;
    }

    public String getTimerGoal() {
      return timerGoal;
    }

    public void setTimerGoal(String timerGoal) {
      this.timerGoal = timerGoal;
    }
  }




}
