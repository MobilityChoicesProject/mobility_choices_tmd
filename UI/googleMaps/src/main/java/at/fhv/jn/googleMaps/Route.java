package at.fhv.jn.googleMaps;

import java.util.List;
import javafx.beans.value.ObservableValue;
import javafx.scene.paint.Color;

/**
 * Created by Johannes on 31.01.2017.
 */
public interface Route {

    void show();
    void hide();
    void dispose();

    void setColor(Color color);

    void setThickness(Thickness thickness);
    Thickness getThickness();
    ObservableValue<Thickness> getThicknessProperty();

    Color getColor();

    ObservableValue<State> stateProperty();
    ObservableValue<Color> colorProperty();

  List<DataPoint> getTracks();
}
