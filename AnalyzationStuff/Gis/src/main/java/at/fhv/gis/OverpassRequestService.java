package at.fhv.gis;

import at.fhv.transportdetector.trackingtypes.BoundingBox;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by Johannes on 26.05.2017.
 */
public class OverpassRequestService {
  private static Logger logger = LoggerFactory.getLogger(OverpassRequestService.class);


//  protected static final String busRouteStart ="http://overpass-api.de/api/interpreter?data=relation[route=bus][\"name\"!~\"^(.|\\r|\\n)*(F|f)ernbus(.|\\r|\\n)*$\"][\"operator\"!~\"^(.|\\r|\\n)*(F|f)ernbus(.|\\r|\\n)*$\"][\"network\"!~\"^(.|\\r|\\n)*(F|f)ernbus(.|\\r|\\n)*$\"](";
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
//  protected File folder = new File("D://overpassCache");
  protected File folder;
  protected DateTimeFormatter simpleDateFormat =DateTimeFormatter.ofPattern("dd_MM_yyyy");
  protected Random random = new Random(32);

  public File getFile(BoundingBox boundingBox,OverpassDataType overpassDataType)
      throws OverpassDataException {

    String property = System.getProperty("java.io.tmpdir");
    folder = new File(property, "overpassCache");
    logger.info("creating tmpdir {}",folder.getPath());
    folder.mkdirs();
    logger.info("created tmpdir {}",folder.getPath());

    String request = null;
    String queryData=null;

    queryData = getQueryData(boundingBox, overpassDataType);

    try {
      request = host+  URLEncoder.encode(queryData, "UTF-8");
    } catch (UnsupportedEncodingException e) {
     throw new IllegalStateException("Seems like there is a bug.");
    }

    try{
      logger.info("send request to overpass server");
      InputStream inputStream = sendRequestToServer(request);
      logger.info("get response from overpass server");

      logger.info("writing overpass response to file");
      File file = writeToFile(inputStream, createName(request));
      logger.info("written overpass response to file");
      return file;

    } catch (IOException e) {
      logger.info("There was an IOException",e);
      throw new OverpassDataException(e);
    }

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


  private InputStream sendRequestToServer(String request) throws OverpassDataException {

    CloseableHttpClient httpClient = HttpClients.createDefault();

    logger.info("create HttpRequest for overpass: {}",request);

    HttpGet httpGet = new HttpGet(request);
    HttpResponse response = null;
    boolean succesfull = false;
    try {
      int statusCode=0;
      for (int i = 0; i < maxTries; i++) {

        logger.info("overpass request {} try ",i);
        response = getHttpResponse(httpClient, httpGet);

        statusCode= response.getStatusLine().getStatusCode();
        if (statusCode == 200) {
          succesfull = true;
          break;
        }
        if (statusCode == 429) {
          logger.info("server rejected request because too many request have been made recently.");
          int timmeMillisToWait = (int) (random.nextDouble() * 15000 + 10000);
          logger.info("wait {} millis util next try",timmeMillisToWait);
          Thread.sleep(timmeMillisToWait);
          logger.info("waited {} millis util next try",timmeMillisToWait);
        }else{
          logger.info("request try failed with statuscode {}",statusCode);
        }
      }

      if (!succesfull) {
        logger.info("could not load data from overpass. Statuscode: {}",+statusCode);
        throw new OverpassDataException("could not load data from overpass. Statuscode: "+statusCode);
      }

      HttpEntity entity = response.getEntity();
      InputStream content = entity.getContent();
      return content;

    } catch (InterruptedException e1) {
      throw new OverpassDataException(e1);
    }  catch (ClientProtocolException e1) {
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

  protected HttpResponse getHttpResponse(HttpClient httpClient, HttpGet httpGet) throws IOException {
    HttpResponse response;
    response = httpClient.execute(httpGet);
    return response;
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






}
