package at.fhv.jn.googleMaps;

import java.util.ArrayList;
import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;
import javafx.scene.web.WebEngine;

/**
 * Created by Johannes on 15.06.2017.
 */
public class LongRoute implements  Route{


  private List<DataPoint> tracks;
  private WebEngine webEngine;
  private List<SimpleRoute> simpleRoutes = new ArrayList<>();
  private Thickness thickness;


  public void init(List<DataPoint> tracks,WebEngine engine){
      this.tracks = tracks;
      this.webEngine = engine;


    int size = tracks.size();
    int partSize = 10000;
    int parts = size / partSize;
    boolean addOne = size % partSize != 0;
    if(addOne){
      parts++;

    }

    List<List<DataPoint>> dataPointLists = new ArrayList<>();

    for(int i = 0; i< parts;i++){
      int startIndex = i * partSize;
      int endIndex = startIndex + partSize;
      if(endIndex>= size){
        endIndex = size;
      }
      List<DataPoint> dataPoints1 = new ArrayList<>();
      for(int j = startIndex;j < endIndex;j++){
        dataPoints1.add(tracks.get(j));
      }
      dataPointLists.add(dataPoints1);
    }

    for (List<DataPoint> dataPointList : dataPointLists) {
      SimpleRoute simpleRoute  = new SimpleRoute();
      simpleRoute.init(dataPointList,webEngine);
      simpleRoutes.add(simpleRoute);
    }

  }


  @Override
  public void show() {

    for (SimpleRoute simpleRoute : simpleRoutes) {
      simpleRoute.show();
    }

  }

  @Override
  public void hide() {
    for (SimpleRoute simpleRoute : simpleRoutes) {
      simpleRoute.hide();
    }
  }

  @Override
  public void dispose() {
    for (SimpleRoute simpleRoute : simpleRoutes) {
      simpleRoute.dispose();
    }
  }

  @Override
  public void setColor(Color color) {
    for (SimpleRoute simpleRoute : simpleRoutes) {
      simpleRoute.setColor(color);
    }
  }

  @Override
  public void setThickness(Thickness thickness) {
    for (SimpleRoute simpleRoute : simpleRoutes) {
      simpleRoute.setThickness(thickness);
    }
    this.thickness = thickness;
  }

  @Override
  public Thickness getThickness() {
    return thickness;
  }

  @Override
  public ObservableValue<Thickness> getThicknessProperty() {
    return null;
  }

  @Override
  public Color getColor() {
    return null;
  }

  @Override
  public ObservableValue<State> stateProperty() {
    return null;
  }

  @Override
  public ObservableValue<Color> colorProperty() {
    return null;
  }

  @Override
  public List<DataPoint> getTracks() {
    return this.tracks;
  }
}
