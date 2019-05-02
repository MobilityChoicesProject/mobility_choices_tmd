package sample;

import at.fhv.transportdetector.trackingtypes.TransportType;
import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Created by Johannes on 04.02.2017.
 */
public class EditabeleGpsPointListCell extends ListCell<GpsPointUiWrapper> {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm:s");
    DecimalFormat df = new DecimalFormat("#.###");


    @FXML
    private VBox containerId;

    @FXML
    HBox innerContainer;
    @FXML
    private Label index;
    @FXML
    private Label dateTime;
    @FXML
    private Label runningTime;
    @FXML
    private Label accuracy;

    @FXML
    private Label speed;

    @FXML
    private Label transportType;

    @FXML
    private ComboBox comboBox;

    private FXMLLoader mLLoader;

    private GpsPointUiWrapper _lastPoint;

    @Override
    protected void updateItem(GpsPointUiWrapper gpsPoint, boolean empty) {
        super.updateItem(gpsPoint, empty);

        if(empty || gpsPoint == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource(
                    "/EditableGpsPointCell.fxml"));
                mLLoader.setController(this);
                try {
                    mLLoader.load();
                    dateTime.setTextFill(Color.BLACK);
                    index.setTextFill(Color.BLACK);
                    runningTime.setTextFill(Color.BLACK);
                    transportType.setTextFill(Color.BLACK);
                    accuracy.setTextFill(Color.BLACK);
                    speed.setTextFill(Color.BLACK);
                    index.setFont(new Font(18));

                    comboBox.getItems().add("");
                    for (TransportType type : TransportType.values()) {
                        comboBox.getItems().add(type.name());
                    }

                    comboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                        boolean focused = comboBox.isFocused();
                        if(focused){
                            String s = newValue.toString();
                            _lastPoint.setNewTransportType(s);
                        }
                    });
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            _lastPoint= gpsPoint;
            String newTransportType = gpsPoint.getNewTransportType();
            if(newTransportType == null){
                newTransportType = "";
            }else if(newTransportType.length() >1){
                newTransportType = newTransportType;
            }
            comboBox.getSelectionModel().select(newTransportType);

            String format = gpsPoint.getGpsPoint().getSensorTime().format(dateTimeFormatter);
            dateTime.setText(format);

            String speedText = df.format(gpsPoint.getSpeed().getKmPerHour())+" Km/h";
            Double accuracy = gpsPoint.getGpsPoint().getAccuracy();
            if(accuracy != null){
                String accuracyText  = " A: "+ df.format(accuracy);
                this.accuracy.setText(accuracyText);

            }else{
                this.accuracy.setText("");

            }
            speed.setText(speedText);
            index.setText(gpsPoint.getIndex()+"  ");

            transportType.setText(gpsPoint.getTransportType().name());

            if(gpsPoint.isEvenColor()){
                containerId.setStyle("-fx-background-color: azure;");
            }else{
                containerId.setStyle("-fx-background-color: beige;");
            }

            setText(null);
            setGraphic(containerId);

        }

    }





}
