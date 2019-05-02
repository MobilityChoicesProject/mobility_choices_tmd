package at.fhv.jn.googleMaps;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.web.WebEngine;

/**
 * Created by Johannes on 02.02.2017.
 */
public class SimpleMarker implements Marker {


    private static int counter = 0;
    private WebEngine webEngine;
    private SimpleObjectProperty<DataPoint> locationProperty = new SimpleObjectProperty<>();
    private SimpleObjectProperty<State> stateProperty = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Boolean> draggableProperty = new SimpleObjectProperty<>(false);
    private String id;
    private SimpleObjectProperty<String> iconPathProperty = new SimpleObjectProperty<>();
    private SimpleObjectProperty<String> labelProperty = new SimpleObjectProperty<>();


    public void init(DataPoint location, WebEngine webEngine) {
        this.webEngine = webEngine;
        locationProperty.set(location);
        id = generateId();
        String latLng = GpsPointsJavascriptUtil.ConvertToJson(location);
        String script =
                "var marker = new google.maps.Marker({\n" +
                        "    position: " + latLng + ",\n" +
                        "    map: map,\n" +
                        "    title: ' ',\n" +
                        "draggable: false,\n" +
                        "  });" +
                        "\n" +
                        "" +
                        "if(typeof markerDic === 'undefined'){" +
                        "   markerDic = {};" +
                        "}" +
                        "markerDic." + id + " = marker; \n";

        webEngine.executeScript(script);
    }

    @Override
    public void show() {
        String script = "var marker =markerDic." + id + ";\n" +
                "marker.setOptions({visible : " + true + "});";
        webEngine.executeScript(script);
        stateProperty.set(State.visible);
    }

    @Override
    public void dispose() {
        String script = "var marker =markerDic." + id + ";\n" +
                "marker.setMap(null);\n" +
                "markerDic." + id + " = null;";
        webEngine.executeScript(script);
        stateProperty.set(State.disposed);
    }

    @Override
    public void hide() {
        String script = "var marker =markerDic." + id + ";\n" +
                "marker.setOptions({visible : " + false + "});";
        webEngine.executeScript(script);
        stateProperty.set(State.hidden);
    }

    @Override
    public ObservableValue<Boolean> draggableProperty() {
        return draggableProperty;
    }

    @Override
    public boolean isDraggable() {
        return draggableProperty.get();
    }

    @Override
    public void setDraggable(boolean flag) {
        String script3 = "var marker =markerDic." + id + ";\n" +
                "marker.setOptions({draggable : " + flag + "});";
        webEngine.executeScript(script3);
        draggableProperty.set(flag);
    }

    @Override
    public State getState() {
        return null;
    }

    @Override
    public DataPoint getPosition() {
        return null;
    }

    @Override
    public void setPosition(DataPoint dataPoint) {
        String dataPointStr = GpsPointsJavascriptUtil.ConvertToJson(dataPoint);
        String script=null;
        if(draggableProperty.get()){
            script= "var marker =markerDic." + id + ";\n" +
                    "marker.setPosition(" + dataPointStr + ");";
        }else{
            script = "var marker =markerDic." + id + ";\n" +
                    "marker.setOptions({draggable : true });" +
                    "" +
                    "setTimeout(function(){" +
                    "marker.setPosition(" + dataPointStr + ");" +
                    "" +
                    "setTimeout(function(){" +
                    "marker.setOptions({draggable : false });" +
                    "},3);" +
                    "" +
                    "},3);";

        }


        webEngine.executeScript(script);

    }


    @Override
    public void setIconPath(String iconPath){

        String script = "var marker =markerDic." + id + ";\n" +
                "marker.setIcon("+iconPath+");";
        webEngine.executeScript(script);
        iconPathProperty.set(iconPath);

    }

    @Override
    public String getIconPath(){
        return iconPathProperty.get();
    }

    @Override
    public ObservableValue<String> iconPathProperty(){
        return iconPathProperty;
    }

    @Override
    public String getLabel() {
        return labelProperty.get();
    }

    @Override
    public void setLabel(String label) {

        String script = "var marker =markerDic." + id + ";\n" +
                "marker.setOptions({label : \"" + label + "\"});";
        webEngine.executeScript(script);
        labelProperty.set(label);
    }

    @Override
    public ObservableValue<State> stateProperty() {
        return null;
    }

    @Override
    public ObservableValue<DataPoint> positionProperty() {
        return null;
    }

    @Override
    public ObservableValue<String> labelProperty() {
        return null;
    }

    private String generateId() {
        StringBuilder str = new StringBuilder();
        int rounds = counter / 24;
        int modulo = counter % 24;
        char c = (char) ('a' + modulo);
        str.append(c);
        for (int i = 0; i < rounds; i++) {
            int temp = rounds / 24;
            modulo = temp % 24;
            c = (char) ('a' + modulo);
            str.insert(0, c);
        }
        counter++;
        return str.toString();
    }
}
