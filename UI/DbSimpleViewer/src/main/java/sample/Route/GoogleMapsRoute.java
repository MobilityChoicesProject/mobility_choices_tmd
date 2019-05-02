package sample.Route;

import at.fhv.jn.googleMaps.DataPoint;
import at.fhv.jn.googleMaps.GoogleMapsController;
import at.fhv.jn.googleMaps.Route;
import at.fhv.jn.googleMaps.SimpleDataPoint;
import at.fhv.jn.googleMaps.Thickness;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import javafx.scene.paint.Color;

/**
 * Created by Johannes on 02.03.2017.
 */
public class GoogleMapsRoute {


    private Map<TrackingSegment,Route> segments = new HashMap<>();
    private GoogleMapsController googleMapsController;
    private Color color;
    private Color hightlightColor;
    public void init(GoogleMapsController googleMapsController, TrackingSegmentBag trackingSegmentBag, Color color,Color hightlightColor){
        this.googleMapsController = googleMapsController;
        this.hightlightColor= hightlightColor;
        this.color = color;
        int counter = 0;
        for (TrackingSegment trackingSegment : trackingSegmentBag.getSegments()) {

            List<DataPoint> dataPoints = new ArrayList<>();
            for (IExtendedGpsPoint point : trackingSegment.getGpsPoints()) {
                dataPoints.add(new SimpleDataPoint(point.getLatitude(),point.getLongitude()));
            }
            Route route =null;
            try{
                System.out.println(counter+++"                 "+dataPoints.size());
                 route = googleMapsController.createRoute(dataPoints);

            }catch (Error error){
                error.printStackTrace();
            }
            route.setColor(color);
            route.setThickness(Thickness.Normal);
            segments.put(trackingSegment,route);
        }

    }

    public void zoomTo(){
        LinkedList<Route> routes = new LinkedList<>();
        for (Route simpleRoute : segments.values()) {
            routes.add(simpleRoute);
        }
        googleMapsController.zoomTo(routes);
    }


    public void highlightSegment(TrackingSegment trackingSegment){
        Route simpleRoute = segments.get(trackingSegment);
        simpleRoute.setThickness(Thickness.Normal);
        simpleRoute.setColor(hightlightColor);
    }

    public void highlightOnlyTrackingSegment(TrackingSegment trackingSegment){
        dehilightAll();
        if(trackingSegment !=null){
            highlightSegment(trackingSegment);
        }
    }

    private void dehilightAll(){
        for (Route simpleRoute : segments.values()) {
            simpleRoute.setThickness(Thickness.extraFine);
            simpleRoute.setColor(color);
        }
    }

    public void hide(){
        for (Route simpleRoute : segments.values()) {
            simpleRoute.hide();
        }
    }

    public void show(){
        for (Route simpleRoute : segments.values()) {
            simpleRoute.show();
        }
    }

    public void dispose(){
        for (Route simpleRoute : segments.values()) {
            simpleRoute.dispose();
        }
    }

}
