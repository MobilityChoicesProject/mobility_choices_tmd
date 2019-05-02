package at.fhv.transportClassifier.scheffknechtgpx;

import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.builder.SimpleGpsPoint;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Johannes on 16.02.2017.
 */
public class XmlHandler extends DefaultHandler {

    boolean transportationMode = false;
    boolean trackingSegmentStarted = false;
    private boolean trackingPointOpen;
    private boolean trackingPointTimeOpen;
    private List<SimpleGpsPoint> gpsPoints = new ArrayList<>();
    private SimpleGpsPoint lastAddedGpsPoint ;
    private LocalDateTime startTime = null;
    private LocalDateTime endTime = null;
    private TransportType transportType;

    public List<SimpleGpsPoint> getGpsPoints() {
        return gpsPoints;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public TransportType getTransportType() {
        return transportType;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
        super.startElement(uri, localName, qName, attributes);

        if("transportationmode".equals(localName)){
            transportationMode= true;
        }else if("trkseg".equals(localName)){
            trackingSegmentStarted= true;
        }else if(trackingSegmentStarted){

            if("trkpt".equals(localName)){
                trackingPointOpen = true;
                String lat = attributes.getValue("lat");
                String lon = attributes.getValue("lon");
                double latitude = Double.parseDouble(lat);
                double longitude = Double.parseDouble(lon);

                SimpleGpsPoint point = new SimpleGpsPoint();
                point.setLatitude(latitude);
                point.setLongitude(longitude);
                lastAddedGpsPoint  = point;
                gpsPoints.add(point);

            }else if(trackingPointOpen){
                if("time".equals(localName)){
                     trackingPointTimeOpen= true;
                }
            }



        }


    }

    @Override
    public void characters(char[] ch, int start, int length) throws SAXException {
        super.characters(ch, start, length);
        if(transportationMode){
           String transportationModeS = getString(ch, start, length);
             transportType = getTransportationType(transportationModeS);
        }

        if(trackingPointTimeOpen){
            String time = getString(ch,start,length);
            time = time.substring(0,time.length()-1);
            try{
                LocalDateTime parse = LocalDateTime.parse(time);
                if(startTime == null){
                    startTime= parse;
                }
                lastAddedGpsPoint.setSensorTime(parse);
            }catch (Exception ex){
                ex.printStackTrace();
            }

        }

    }

    private TransportType getTransportationType(String transportationModeS) {
        if("bike".equals(transportationModeS)){
            return TransportType.BIKE;
        }else if("car".equals(transportationModeS)){
            return TransportType.CAR;
        }else if("bus".equals(transportationModeS)){
            return TransportType.BUS;
        }else if("train".equals(transportationModeS)){
            return TransportType.TRAIN;
        }else if("walking".equals(transportationModeS)||"walk".equals(transportationModeS)){
            return TransportType.WALK;
        }else{
            throw new RuntimeException("Not supported transportmode: "+transportationModeS);
        }
    }

    private String getString(char[] ch, int start, int length) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(ch,start,length);
        String s = stringBuilder.toString();
        return s;
    }


    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        super.endElement(uri, localName, qName);
        if("transportationmode".equals(localName)){
            transportationMode= false;
        }else if("trkseg".equals(localName)){
            endTime = lastAddedGpsPoint.getSensorTime();
            trackingSegmentStarted = false;
        }else if(trackingSegmentStarted){
            if("trkpt".equals(localName)){
                trackingPointOpen = false;
            }else if(trackingPointOpen){
                if("time".equals(localName)){
                    trackingPointTimeOpen= false;
                }
            }
        }

    }
}
