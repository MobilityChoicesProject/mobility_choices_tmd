package sample;

import java.io.IOException;
import java.text.DecimalFormat;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

/**
 * Created by Johannes on 04.02.2017.
 */
public class GpsPointListCell extends ListCell<GpsPointUiWrapper> {

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

    private FXMLLoader mLLoader;


    @Override
    protected void updateItem(GpsPointUiWrapper gpsPoint, boolean empty) {
        super.updateItem(gpsPoint, empty);

        if(empty || gpsPoint == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource(
                    "/GpsPointCell.fxml"));
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
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

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
