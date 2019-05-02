package sample;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.Tracking;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;
import sample.modul.ModulContext;

/**
 * Created by Johannes on 19.04.2017.
 */
public class RouteSelectorAndSaver {

  private Tracking CurrenTracking;
  public SimpleBooleanProperty hasSelectedRouteProperty = new SimpleBooleanProperty(false);
  private Tracking selectedTracking;
  private ModulContext context;
  private     Button saveButton = new Button("SAVE Route");

  private String name;
  private String selectedTrackingName;
  public Node getNode(ModulContext context){
    saveButton.setDisable(true);
    this.context = context;
    VBox vBox = new VBox();

    saveButton.setOnAction(event -> {
      context.addTracking(name,CurrenTracking);
      saveButton.setDisable(true);
      CurrenTracking = null;

    });
    ChoiceBox cb = new ChoiceBox();
    cb.setItems(context.getTrackingList());
    cb.setConverter(new StringConverter() {

      @Override
      public String toString(Object object) {
        Tuple<String,Tracking> tuple = (Tuple<String,Tracking>)object;

        return tuple.getItem1();
      }

      @Override
      public Object fromString(String string) {
        return null;
      }
    });

    cb.getSelectionModel().select(0);
    cb.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
      if(newValue == null){
        selectedTrackingName = "";
        selectedTracking = null;
        hasSelectedRouteProperty.set(false);

      }else{
        Tuple<String,Tracking> trackingTuple = (Tuple<String,Tracking>) newValue;

        selectedTrackingName = trackingTuple.getItem1();
        selectedTracking = trackingTuple.getItem2();

        hasSelectedRouteProperty.set(true);
      }


    });

    vBox.getChildren().add(cb);
    vBox.getChildren().add(saveButton);
    return vBox;
  }

  public Tracking getSelectedTracking() {
    return selectedTracking;
  }

  public void setTracking(Tracking tracking, String name){
    CurrenTracking =  tracking;
    this.name = selectedTrackingName+ "_"+name;
    saveButton.setDisable(false);

  }





}
