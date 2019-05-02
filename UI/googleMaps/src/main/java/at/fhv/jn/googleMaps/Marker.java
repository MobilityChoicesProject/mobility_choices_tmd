package at.fhv.jn.googleMaps;

import javafx.beans.value.ObservableValue;

/**
 * Created by Johannes on 01.02.2017.
 */
public interface Marker {

    void show();
    void dispose();
    void hide();

    ObservableValue<Boolean> draggableProperty();

    boolean isDraggable();

    void setDraggable(boolean flag);

    void setPosition(DataPoint dataPoint);
    void setLabel(String label);



    State getState();
    DataPoint getPosition();

    void setIconPath(String iconPath);

    String getIconPath();

    ObservableValue<String> iconPathProperty();

    String getLabel();

    ObservableValue<State> stateProperty();
    ObservableValue<DataPoint> positionProperty();
    ObservableValue<String> labelProperty();


}
