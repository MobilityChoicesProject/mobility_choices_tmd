package at.fhv.jn.googleMaps;

import java.util.LinkedList;
import java.util.List;
import javafx.fxml.FXML;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;

public class GoogleMapsController {


    @FXML
    public WebView webViewId;
//    @FXML
//    public Button button1 ;
//    @FXML
//    public Button button2 ;
//    @FXML
//    public Button button3 ;
//    @FXML
//    public Button button4 ;
//    @FXML
//    public Button button5 ;
//    @FXML
//    public Button button6 ;
//    @FXML
//    public Button button7 ;

    private WebEngine engine;
    private Marker marker;


    public Route createRoute(List<DataPoint> dataPoints){
        LongRoute simpleRoute = new LongRoute();
        simpleRoute.init(dataPoints,engine);
        return  simpleRoute;
    }

    public Marker createMarker(DataPoint location){
        SimpleMarker marker = new SimpleMarker();
        marker.init(location,engine);
        return marker;
    }

    public void zoomTo(List<Route> routes){


        double minLatitude = 9999;
        double maxLatitude = 0;
        double minLongitude = 9999;
        double maxLongitude = 0;

        for (Route simpleRoute : routes) {


            if (simpleRoute.getTracks().size() == 0) {
                continue;
            }

            for (DataPoint dataPoint : simpleRoute.getTracks()) {
                if(minLatitude > dataPoint.getLatitude()){
                    minLatitude= dataPoint.getLatitude();
                }
                if(maxLatitude < dataPoint.getLatitude()){
                    maxLatitude = dataPoint.getLatitude();
                }

                if(minLongitude > dataPoint.getLongitude()){
                    minLongitude= dataPoint.getLongitude();
                }
                if(maxLongitude < dataPoint.getLongitude()){
                    maxLongitude = dataPoint.getLongitude();
                }
            }
        }
        zoomTo(minLatitude,minLongitude,maxLatitude,maxLongitude);
    }

    public void zoomTo(SimpleRoute route){

        LinkedList linkedList = new LinkedList();
        linkedList.add(route);
        zoomTo(linkedList);


    }

    public void zoomTo(double soutLatitude,double westLongitude,double northLatitude,double eastLongitude){
        double minLatitude = soutLatitude;
        double maxLatitude = northLatitude;
        double minLongitude = westLongitude;
        double maxLongitude = eastLongitude;

        String script ="var bounds = new google.maps.LatLngBounds();\n" +
                "bounds.extend(new google.maps.LatLng({lat: "+minLatitude+", lng: "+minLongitude+"}));\n" +
                "bounds.extend(new google.maps.LatLng({lat: "+maxLatitude+", lng: "+maxLongitude+"}));\n" +
                "map.fitBounds(bounds);\n";

        engine.executeScript(script);
    }

    @FXML
    public void initialize(){
        engine = webViewId.getEngine();
        engine.loadContent(simplePage);

//        button1.setOnAction(event -> {
//            engine.executeScript(" var flightPlanCoordinates = [\n" +
//                    "    {lat: 37.772, lng: -122.214},\n" +
//                    "    {lat: 21.291, lng: -157.821},\n" +
//                    "    {lat: -18.142, lng: 178.431},\n" +
//                    "    {lat: -27.467, lng: 153.027}\n" +
//                    "  ];\n" +
//                    "  var flightPath = new google.maps.Polyline({\n" +
//                    "    path: flightPlanCoordinates,\n" +
//                    "    geodesic: true,\n" +
//                    "    strokeColor: '#FF0000',\n" +
//                    "    strokeOpacity: 1.0,\n" +
//                    "    strokeWeight: 3\n" +
//                    "  });\n" +
//                    "\n" +
//                    "  flightPath.setMap(map);");
//
//
//        });
//
//        button2.setOnAction(event -> {
//            engine.executeScript(" var flightPlanCoordinates = [\n" +
//                    "    {lat: 37.772, lng: -122.214},\n" +
//                    "    {lat:  21.291, lng: -122.214},\n" +
//                    "    {lat: 21.291, lng: 178.431},\n" +
//                    "    {lat: -18.142, lng: 178.431},\n" +
//                    "    {lat: -27.467, lng: 153.027}\n" +
//                    "  ];\n" +
//                    "  var flightPath = new google.maps.Polyline({\n" +
//                    "    path: flightPlanCoordinates,\n" +
//                    "    geodesic: true,\n" +
//                    "    strokeColor: '#FF0000',\n" +
//                    "    strokeOpacity: 1,\n" +
//                    "    strokeWeight: 1\n" +
//                    "  });\n" +
//                    "\n" +
//                    "  flightPath.setMap(map);");
//
//
//        });
//
//        button3.setOnAction(event -> {
//
//            List<DataPoint> dataPoints = new LinkedList<DataPoint>();
//            dataPoints.add(new SimpleDataPoint(37.772,-122.214));
//            dataPoints.add(new SimpleDataPoint(21.291,-122.214));
//            dataPoints.add(new SimpleDataPoint(-18.142,178.431));
//            dataPoints.add(new SimpleDataPoint( -27.467,153.027));
//             route = createRoute(dataPoints);
//            marker = createMarker(new SimpleDataPoint(-14.142,178.431) );
//        });
//
//        button4.setOnAction(event -> {
//            marker.hide();
//            route.hide();
//        });
//        button5.setOnAction(event -> {
//            if(route.getColor() == Color.RED){
//                route.setThickness(Thickness.extraFine);
//                route.setColor(Color.GREEN);
//                marker.setPosition(new SimpleDataPoint(-18.142,178.431));
//                marker.setLabel("Baum");
//            }else{
//                route.setThickness(Thickness.ExtraBold);
//                route.setColor(Color.RED);
//                marker.setPosition(new SimpleDataPoint(37.772,-122.214));
//                marker.setLabel("");
//
//            }
//
//        });
//        button6.setOnAction(event -> {
//            route.dispose();
//            marker.dispose();
//        });
//        button7.setOnAction(event -> {
//           if(marker.isDraggable()){
//               marker.setDraggable(false);
//           }else{
//               marker.setDraggable(true);
//
//           }
//        });
    }


    public static String simplePage = "<!DOCTYPE html>\n" +
            "<html>\n" +
            "  <head>\n" +
            "    <title>Simple Map</title>\n" +
            "    <meta name=\"viewport\" content=\"initial-scale=1.0\">\n" +
            "    <meta charset=\"utf-8\">\n" +
            "    <style>\n" +
            "      html, body {\n" +
            "        height: 100%;\n" +
            "        margin: 0;\n" +
            "        padding: 0;\n" +
            "      }\n" +
            "      #map {\n" +
            "        height: 100%;\n" +
            "      }\n" +
            "    </style>\n" +
            "  </head>\n" +
            "  <body>\n" +
            "    <div id=\"map\"></div>\n" +
            "    <script>\n" +
            "\n" +
            "var map;\n" +
            "function initMap() {\n" +
            "  map = new google.maps.Map(document.getElementById('map'), {\n" +
            "    center: {lat: -12.397, lng: 150.644},\n" +
            "    zoom: 5\n" +
            "  });\n" +
            "}\n" +
            "\n" +
            "function getPolyline(flightPlanCoordinates){" +
            "return new google.maps.Polyline({\n" +
            "    path: flightPlanCoordinates,\n" +
            "    geodesic: true,\n" +
            "    strokeColor: '#FF0000',\n" +
            "    strokeOpacity: 1.0,\n" +
            "    strokeWeight: 2\n" +
            "  });"+
            "}"+
            "    </script>\n" +
            "    <script src=\"https://maps.googleapis.com/maps/api/js?key=AIzaSyDQ82DNEQX82O8ATd97PwifEZ_a8J2OSJs&callback=initMap\"\n" +
            "        async defer></script>\n" +
            "  </body>\n" +
            "</html>";



}
