package at.fhv.tmddemoservice;


import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.processFlow.TmdResult;
import at.fhv.tmd.processFlow.TmdSegment;
import at.fhv.tmd.processFlow.TransportTypeProbability;
import at.fhv.tmddemoservice.jsonEntities.*;
import at.fhv.tmddemoservice.queue.ClassifyThread;
import at.fhv.transportClassifier.common.CoordinateUtil;
import at.fhv.transportClassifier.mainserver.api.TmdServiceLocal;
import at.fhv.transportdetector.trackingtypes.TransportType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import javax.annotation.security.PermitAll;
import javax.ejb.EJB;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.util.EntityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Path("/TMD_Service")
public class TMDService {

    private static final int THREAD_COUNTER = 2;
    private static ThreadPoolExecutor _executorService = (ThreadPoolExecutor) Executors.newFixedThreadPool(THREAD_COUNTER);


    private static String REST_PATH = "api/tmd/queueresponse";


    // we need both the IP and DNS name because of the HTTPS certificates
    private final static String MC_SERVER_PROD_IP = "SERVER-PROD-IP";
    private final static String MC_SERVER_TEST_IP = "SERVER-TEST-IP";
    private final static String MC_SERVER_PROD_DNS = "SERVER-PROD-DNS";
    private final static String MC_SERVER_TEST_DNS = "SERVER-TEST-DNS";

    private DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//    private DateTimeFormatter formatterNoTimeZone = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss");
//    private DateTimeFormatter formatterWithTimeZone = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.S'Z'");

    private static Logger logger = LoggerFactory.getLogger(TMDService.class);

    @EJB
    private TmdServiceLocal tmdServiceLocal;

    public static Duration minDuration = Duration.ofMinutes(2);

    public TMDService() {
    }

    @PermitAll
    @GET
    @Path("/ping")
    public Response ping() {
        return Response.status(200).entity("Ping successful").build();
    }

    @PermitAll
    @POST
    @Path("/mergeSameSegments")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response mergeSameSegments(InputStream incomingData) {
        String incomingDataString;
        try {
            incomingDataString = readIncomingData(incomingData);
        } catch (Exception e) {
            return Response.status(Status.INTERNAL_SERVER_ERROR).build();
        }
        try {
            Gson gson = new GsonBuilder().setPrettyPrinting().create();

            SegmentEntity[] segmentEntities = gson.fromJson(incomingDataString, SegmentEntity[].class);

            List<SegmentEntity> segmentEntityList = new ArrayList<>(Arrays.asList(segmentEntities));

            int size = segmentEntityList.size();
            for (int i = 0; i < size; i++) {
                SegmentEntity currentSegment = segmentEntityList.get(i);

                String currentTransportType = currentSegment.getTransportMode();

                int j = i + 1;
                LinkedList<SegmentEntity> segmentsToMerge = new LinkedList<>();
                SegmentEntity nextSegment = null;
                if (j < size) {
                    nextSegment = segmentEntityList.get(j);
                }
                while (j < size && nextSegment != null && Objects.equals(currentTransportType, nextSegment.getTransportMode()) && !nextSegment.isWaypoint()) {
                    segmentsToMerge.add(nextSegment);
                    j++;
                    if (j < size) {
                        nextSegment = segmentEntityList.get(j);
                    }
                }
                if (!segmentsToMerge.isEmpty()) {
                    segmentsToMerge.add(currentSegment);
                    SegmentEntity mergeResult = mergeSegments(segmentsToMerge);
                    for (int k = j - 1; k >= i; k--) {
                        segmentEntityList.remove(k);
                    }
                    segmentEntityList.add(i, mergeResult);
                }
                size = segmentEntityList.size();
            }

            segmentEntityList.get(segmentEntityList.size() - 1).setEndpoint(true);
            String jsonReturnStr = gson.toJson(segmentEntityList);
            logger.info("Track successfully merged");
            return Response.status(200).type(MediaType.APPLICATION_JSON).entity(jsonReturnStr).build();
        } catch (Exception e) {
            logger.error("There was an exception during the merge process", e);
            ResponseEntity responseEntity = new ResponseEntity();
            responseEntity.setRequestId(-1);
            responseEntity.setStatus("Error");

            return Response.status(400).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
        }
    }

    private SegmentEntity mergeSegments(LinkedList<SegmentEntity> segmentsToMerge) {
        SegmentEntity result = new SegmentEntity();

        segmentsToMerge.sort(Comparator.comparing(s -> s.getStart().getTimestamp()));

        result.setWaypoint(segmentsToMerge.getFirst().isWaypoint());
        result.setStartTime(segmentsToMerge.getFirst().getStartTime());
        result.setEndtime(segmentsToMerge.getLast().getEndtime());

        result.setStart(segmentsToMerge.getFirst().getStart());
        result.setEnd(segmentsToMerge.getLast().getEnd());
        result.setTransportMode(segmentsToMerge.getFirst().getTransportMode());

        LinkedList<CoordinateEntity> coordinates = new LinkedList<>();
        HashMap<String, Double> resultProbabilities = new HashMap<>();
        double distance = 0;
        double duration = 0;
        for (SegmentEntity currentSegment : segmentsToMerge) {
            for (ProbabilityEntity transportTypeProbability : currentSegment.getProbabilities()) {
                if (!resultProbabilities.containsKey(transportTypeProbability.getTransportMode())) {
                    resultProbabilities.put(transportTypeProbability.getTransportMode(), 0.0);
                }
                Double currentProbability = resultProbabilities.get(transportTypeProbability.getTransportMode());
                Double newProbability = currentProbability + Double.parseDouble(transportTypeProbability.getProbability().replace(",", ".")) / segmentsToMerge.size();
                resultProbabilities.put(transportTypeProbability.getTransportMode(), newProbability);
            }
            distance += currentSegment.getDistance();
            duration += currentSegment.getDuration();
            coordinates.addAll(currentSegment.getCoordinates());
        }
        List<ProbabilityEntity> probabilities = new ArrayList<>();
        for (String transportMode : resultProbabilities.keySet()) {
            probabilities.add(new ProbabilityEntity(transportMode, round(resultProbabilities.get(transportMode)).toString()));
        }
        result.setProbabilities(probabilities);
        result.setCoordinates(coordinates);
        result.setDistance(distance);
        result.setDuration(duration);

        return result;
    }


    private Double round(Double val) {
        return new BigDecimal(val.toString()).setScale(2, RoundingMode.HALF_UP).doubleValue();
    }

    @PermitAll
    @POST
    @Path("/classifyQueue")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response classifyQueue(InputStream incomingData, @Context HttpServletRequest httpServletRequest) {
        ResponseEntity responseEntity = new ResponseEntity();
        try {
            RequestEntity requestEntity = generateRequestEntity(incomingData);
            String userID = requestEntity.getUserId();


            responseEntity.setTmdVersion(getTmdVersion());
            responseEntity.setUserId(userID);
            responseEntity.setRequestId(-1);

            responseEntity.setDate(requestEntity.getDate());
            responseEntity.setAccessToken(requestEntity.getAccessToken());
            responseEntity.setPushToken(requestEntity.getPushToken());

            //check if the data is valid
            ArrayList<IGpsPoint> gpsPoints = new ArrayList<>(requestEntity.getTrajectory());
            Tuple<Boolean, String> booleanStringTuple = checkInputData(gpsPoints);
            if (!booleanStringTuple.getItem1()) {
                responseEntity.setStatus(booleanStringTuple.getItem2());
                return Response.status(Status.NOT_ACCEPTABLE).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
            }

            //get ip address from client
            String ipAddress = httpServletRequest.getHeader("X-FORWARDED-FOR");
            if (ipAddress == null) {
                ipAddress = httpServletRequest.getRemoteAddr();
            }
            String trackName = "Track for " + requestEntity.getUserId() + " (" + new Date() + ")";
            int size = _executorService.getQueue().size();

            ClassifyThread thread = new ClassifyThread(trackName, ipAddress, size) {
                @Override
                public void process() {
                    TmdResult tmdResult = tmdServiceLocal.process(gpsPoints);

                    List<SegmentEntity> segmentEntities = mapToRsponse(tmdResult);

                    responseEntity.setStatus("OK");
                    responseEntity.setSegments(segmentEntities);

                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String jsonReturnStr = gson.toJson(responseEntity);


                    try {
                        String url;
                        switch (_ipAddress) {
                            case MC_SERVER_TEST_IP:
                                //Test
                                url = "https://" + MC_SERVER_TEST_DNS + ":3000/" + REST_PATH;
                                break;
                            case MC_SERVER_PROD_IP:
                                //Prod
                                url = "https://" + MC_SERVER_PROD_DNS + ":3000/" + REST_PATH;
                                break;
                            default:
                                url = "http://" + _ipAddress + ":3000/" + REST_PATH;
                                break;
                        }

                        SSLContextBuilder builder = new SSLContextBuilder();
                        builder.loadTrustMaterial(null, new TrustSelfSignedStrategy());
                        SSLConnectionSocketFactory sslsf = new SSLConnectionSocketFactory(
                                builder.build());
                        CloseableHttpClient httpclient = HttpClients.custom().setSSLSocketFactory(
                                sslsf).build();

                        HttpPost httpPost = new HttpPost(url);
                        httpPost.setEntity(new StringEntity(jsonReturnStr, ContentType.create("application/json")));

                        try (CloseableHttpResponse response = httpclient.execute(httpPost)) {
                            logger.info("Track sucessfully sent to node server.");
                            logger.info("Received statuscode: " + response.getStatusLine().getStatusCode());
                            HttpEntity entity2 = response.getEntity();
                            // ensure it is fully consumed
                            EntityUtils.consume(entity2);
                        }
                    } catch (Exception e) {
                        logger.error("There was an Exception when sending the classified track back to the node server.", e);
                    }
                }
            };
            _executorService.execute(thread);
        } catch (IOException e) {
            e.printStackTrace();
            responseEntity.setStatus("Error");
            return Response.status(400).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
        }

        responseEntity.setStatus("Accepted");
        return Response.status(202).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
    }

    private RequestEntity generateRequestEntity(InputStream incomingData) throws IOException {
        String incomingDataString = readIncomingData(incomingData);

        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        RequestEntity requestEntity = gson.fromJson(incomingDataString, RequestEntity.class);
        //sort trajectory
        requestEntity.getTrajectory().sort(Comparator.comparing(GpsPointEntity::getTime));
        return requestEntity;
    }


    @PermitAll
    @POST
    @Path("/classify")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response classify(InputStream incomingData) {

        try {
            String incomingDataString;
            try {
                incomingDataString = readIncomingData(incomingData);
            } catch (Exception e) {
                return Response.status(Status.INTERNAL_SERVER_ERROR).build();
            }


            Gson gson = new GsonBuilder().setPrettyPrinting().create();
            RequestEntity requestEntity = gson.fromJson(incomingDataString, RequestEntity.class);

            String userID = requestEntity.getUserId();

            ResponseEntity responseEntity = new ResponseEntity();
            responseEntity.setTmdVersion(getTmdVersion());
            responseEntity.setUserId(userID);
            responseEntity.setRequestId(-1);

            //sort trajectory
            requestEntity.getTrajectory().sort(Comparator.comparing(GpsPointEntity::getTime));

            ArrayList<IGpsPoint> gpsPoints = new ArrayList<>(requestEntity.getTrajectory());

            Tuple<Boolean, String> booleanStringTuple = checkInputData(gpsPoints);

            if (!booleanStringTuple.getItem1()) {
                responseEntity.setStatus(booleanStringTuple.getItem2());
                return Response.status(Status.NOT_ACCEPTABLE).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
            }

            TmdResult tmdResult = tmdServiceLocal.process(gpsPoints);

            List<SegmentEntity> segmentEntities = mapToRsponse(tmdResult);

            responseEntity.setStatus("OK");
            responseEntity.setSegments(segmentEntities);


            String jsonReturnStr = gson.toJson(responseEntity);
            return Response.status(200).type(MediaType.APPLICATION_JSON).entity(jsonReturnStr).build();
        } catch (IOException e) {
            logger.error("There was an exception while reading files for versioning.");
            ResponseEntity responseEntity = new ResponseEntity();
            responseEntity.setRequestId(-1);
            responseEntity.setStatus("Error");
            return Response.status(400).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
        } catch (Exception ex) {
            logger.error("There was an exception during the work of the TMD Service", ex);
            ResponseEntity responseEntity = new ResponseEntity();
            responseEntity.setRequestId(-1);
            responseEntity.setStatus("Error");

            return Response.status(400).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();

        } catch (OutOfMemoryError error) {
            logger.error("There was an OutOfMemoryError exception during the work of the TMD Service", error);
            ResponseEntity responseEntity = new ResponseEntity();
            responseEntity.setRequestId(-1);
            responseEntity.setStatus("Error");

            return Response.status(400).type(MediaType.APPLICATION_JSON).entity(responseEntity).build();
        }


    }

    private String readIncomingData(InputStream incomingData) throws IOException {
        StringBuilder stringBuilder = new StringBuilder();
        BufferedReader in = new BufferedReader(new InputStreamReader(incomingData));
        String line;
        while ((line = in.readLine()) != null) {
            stringBuilder.append(line);
        }
        return stringBuilder.toString();
    }

    private String getTmdVersion() throws IOException {
        String tmdVersion;
        Properties buildProps = new Properties();
        buildProps.load(TMDService.class.getClassLoader().getResourceAsStream("buildNumber.properties"));
        String buildNumber = buildProps.getProperty("buildNumber");

        Properties versionProperties = new Properties();
        versionProperties.load(TMDService.class.getClassLoader().getResourceAsStream("version.properties"));
        String projectVersion = versionProperties.getProperty("version");

        String version = projectVersion + "." + buildNumber;
        if (versionProperties.getProperty("buildNumber").equals("")) {
            tmdVersion = version + "-DEBUG";
        } else {
            tmdVersion = version;
        }
        return tmdVersion;
    }


    private List<SegmentEntity> mapToRsponse(TmdResult tmdResult) {
        List<SegmentEntity> segmentEntities = new ArrayList<>();
        for (TmdSegment tmdSegment : tmdResult) {
            SegmentEntity segmentEntity = new SegmentEntity();
            String segmentStartTime = tmdSegment.getStartTime().format(formatter);
            String segmentEndTime = tmdSegment.getEndTime().format(formatter);

            segmentEntity.setStartTime(segmentStartTime);
            segmentEntity.setEndtime(segmentEndTime);
            List<CoordinateEntity> coordinateEntities = new ArrayList<>();

            IGpsPoint previousGpsPoint = null;
            double distance = 0;
            for (IGpsPoint coordinate : tmdSegment.getCoordinates()) {
                CoordinateEntity coordinateEntity = new CoordinateEntity();
                coordinateEntity.setLat(coordinate.getLatitude());
                coordinateEntity.setLng(coordinate.getLongitude());
                coordinateEntity.setTime(coordinate.getTime().toString());
                coordinateEntities.add(coordinateEntity);

                if (previousGpsPoint != null) {
                    distance += CoordinateUtil.haversineDistance(previousGpsPoint, coordinate).getKm();
                }

                previousGpsPoint = coordinate;
            }
            //set the distance for this segment
            segmentEntity.setDistance(distance);

            try {
                if (!getTmdVersion().contains("DEBUG")) {
                    segmentEntity.setCoordinates(coordinateEntities);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            List<ProbabilityEntity> probabilityEntities = new ArrayList<>();
            for (TransportTypeProbability transportTypeProbability : tmdSegment
                    .getTransportTypeProbabilities()) {
                ProbabilityEntity probabilityEntity = new ProbabilityEntity();
                String name = transportTypeProbability.getTransportType().name();
                if (transportTypeProbability.getTransportType() == TransportType.OTHER) {
                    name = "NON_VEHICLE";
                }
                probabilityEntity.setTransportMode(name);
                probabilityEntity.setProbability(String.format(Locale.ROOT, "%.2f", transportTypeProbability.getProbability()));
                probabilityEntities.add(probabilityEntity);
            }
            segmentEntity.setProbabilities(probabilityEntities);


            segmentEntities.add(segmentEntity);
        }

        return segmentEntities;

    }


    private Tuple<Boolean, String> checkInputData(List<IGpsPoint> trajectory) {

        LocalDateTime time = trajectory.get(0).getTime();
        LocalDateTime time1 = trajectory.get(trajectory.size() - 1).getTime();
        Duration between = Duration.between(time, time1).abs();
        if (minDuration.compareTo(between) > 0) {
            return new Tuple<>(false, "SHORT_DURATION_ERROR");
        }

        if (trajectory.size() < 60) {
            return new Tuple<>(false, "NOT_ENOUGH_POINTS_ERROR");
        }

        Duration sixSeconds = Duration.ofSeconds(6);
        int numberAbove6Seconds = 0;
        int numberOfAllDurations = 0;
        boolean inBoundingBox = true;

        IGpsPoint lastGpsPoint = null;
        for (IGpsPoint iGpsPoint : trajectory) {
            Double latitude = iGpsPoint.getLatitude();
            Double longitude = iGpsPoint.getLongitude();
            if (latitude <= 46.5 || latitude > 48.5) {
                inBoundingBox = false;
            }
            if (longitude <= 6.5 || longitude >= 15.5) {
                inBoundingBox = false;
            }

            if (lastGpsPoint != null) {

                Duration abs = Duration.between(iGpsPoint.getTime(), lastGpsPoint.getTime()).abs();
                boolean durationBiggerThan6Seconds = sixSeconds.compareTo(abs) < 0;
                if (durationBiggerThan6Seconds) {
                    numberAbove6Seconds++;
                }
                numberOfAllDurations++;
            }

            lastGpsPoint = iGpsPoint;
        }

        double numberAbove6SecondsPercentage = numberAbove6Seconds / (1.0 * numberOfAllDurations);
        if (numberAbove6SecondsPercentage > 0.5) {
            return new Tuple<>(false, "DATA_TOO_SPARE_ERROR");
        }
// uncomment those lines to activate the bounding box again
//        if (!inBoundingBox) {
//            return new Tuple<>(false, "NOT_IN_BOUNDING_BOX_ERROR");
//        }

        return new Tuple<>(true, null);
    }


}
