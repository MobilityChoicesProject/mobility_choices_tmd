package at.fhv.jn.googleMaps;

import java.util.List;
import java.util.Random;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;

/**
 * Created by Johannes on 01.02.2017.
 */
public class SimpleRoute implements  Route{

    private String id;

    public List<DataPoint> getTracks() {
        return tracks;
    }

    private List<DataPoint> tracks;
    private static Random random = new Random();
    private SimpleObjectProperty<Color> colorProperty = new SimpleObjectProperty<>();
    private SimpleObjectProperty<State> stateObservableValue = new SimpleObjectProperty<>();
    private SimpleObjectProperty<Thickness> thicknessSimpleObjectProperty = new SimpleObjectProperty<>();

    private WebEngine engine;

    public SimpleRoute(){
        id = generateId();
        double r = random.nextDouble();
        double g = random.nextDouble();
        double b = random.nextDouble();
        double a = 1.0;
        Color color = new Color(r,g,b,a);
        colorProperty.set(color);

    }



    public void init(List<DataPoint> tracks,WebEngine engine){
        this.tracks = tracks;
        String trackJson = GpsPointsJavascriptUtil.ConvertToJson(tracks);
        String color = ColorUtil.ConvertToRGBString(colorProperty.get());
        String script =" var pathCoordinates = " +
                trackJson+
                ";\n" +
                "  var polyLine = new google.maps.Polyline({\n" +
                "    path: pathCoordinates,\n" +
                "    geodesic: true,\n" +
                "    strokeColor: '"+color+"',\n" +
                "    strokeOpacity: 1.0,\n" +
                "    strokeWeight: 2\n" +
                "  });\n" +
                "\n" +
                "" +
                "if(typeof routeDic === 'undefined'){" +
                "   routeDic = {};" +
                "}" +
                "routeDic."+id +" = polyLine; \n"+
                "polyLine.setMap(map);";

        engine.executeScript(script);
        this.engine =engine;
    }

    private void checkInitionalation(){
        if(tracks == null){
            throw new RuntimeException("Not Initialized");
        }
    }
    private void checkDisposedState(){
        if(stateObservableValue.get() == State.disposed){
            throw new RuntimeException("Route is already disposed");
        }
    }


    @Override
    public void show() {
        checkInitionalation();
        checkDisposedState();
        String script ="var polyLine =routeDic." +id+";\n" +
                "polyLine.setOptions({strokeOpacity : "+ colorProperty.get().getOpacity()+ "});";

        engine.executeScript(script);
        stateObservableValue.set(State.visible);

    }


    @Override
    public void hide() {
        checkInitionalation();
        checkDisposedState();

        String script ="var polyLine =routeDic." +id+";\n" +
                "polyLine.setOptions({strokeOpacity : "+ 0.0+ "});";
        engine.executeScript(script);
        stateObservableValue.set(State.hidden);

    }


    @Override
    public void dispose() {
        checkInitionalation();
        checkDisposedState();

        String script ="var polyLine =routeDic." +id+";\n" +
                "polyLine.setMap(null);\n" +
                "routeDic." +id+" = null;";
        engine.executeScript(script);
        stateObservableValue.set(State.disposed);


    }

    @Override
    public void setColor(Color color) {
        checkInitionalation();
        checkDisposedState();
        colorProperty.set(color);

        String colorStr = ColorUtil.ConvertToRGBString(colorProperty.get());
        String script ="var polyLine =routeDic." +id+";\n" +
                "polyLine.setOptions({strokeColor : "+ "\""+colorStr+"\""+"});";
        engine.executeScript(script);

    }

    @Override
    public void setThickness(Thickness thickness) {
        checkInitionalation();
        checkDisposedState();
        thicknessSimpleObjectProperty.set(thickness);
        int value = thickness.getValue();

        String script ="var polyLine =routeDic." +id+";\n" +
                "polyLine.setOptions({strokeWeight : "+ value+"});";

        engine.executeScript(script);

    }

    @Override
    public Thickness getThickness(){
        return thicknessSimpleObjectProperty.get();
    }
    @Override
    public ObservableValue<Thickness> getThicknessProperty(){
        return thicknessSimpleObjectProperty;
    }

    @Override
    public Color getColor() {
        return colorProperty.get();
    }

    @Override
    public ObservableValue<State> stateProperty() {
        return stateObservableValue;
    }

    @Override
    public ObservableValue<Color> colorProperty() {
        return colorProperty;
    }

    private static int counter = 0;
    private String generateId(){
        StringBuilder str = new StringBuilder();
        int rounds = counter /10;
        int modulo = counter % 10;
        char c = (char) ('a' +modulo);
        str.append(c);
        for(int i = 0; i < rounds; i++){
            int temp = rounds/10;
            modulo = temp %10;
             c = (char) ('a' +modulo);
             str.insert(0,c);
        }
        counter++;
        return str.toString();
    }

}
