package sample;

import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.TrackingInfo;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.light.LeightweightTracking;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.Set;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Created by Johannes on 04.02.2017.
 */
public class ListCellTrackingFile extends ListCell<LeightweightTracking> {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:s");

    @FXML
    private Label dateTime;
    @FXML
    private Label transportations;
    @FXML
    private VBox containerId;

    @FXML
    private Label editedLabel;
    private FXMLLoader mLLoader;


    @Override
    protected void updateItem(LeightweightTracking trackingFile, boolean empty) {
        super.updateItem(trackingFile, empty);

        if(empty || trackingFile == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource(
                    "/TrackingFileCell.fxml"));
                mLLoader.setController(this);


                try {
                    mLLoader.load();
                    dateTime.setTextFill(Color.BLACK);
                    transportations.setTextFill(Color.BLACK);
                    editedLabel.setTextFill(Color.BLACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            String format = trackingFile.getStartTimestamp().format(dateTimeFormatter);
            dateTime.setText(format);

            String transportationModeString = getTransportationModeString(trackingFile.getTransportTypes());
            transportations.setText(transportationModeString);

            if(trackingFile.isAcceleratorDataAvailable()){
                containerId.setStyle("-fx-background-color: greenyellow;");
            }else{
                containerId.setStyle("-fx-background-color: whitesmoke;");
            }
            int version = getVersion(trackingFile);
            if(version==4){
                containerId.setStyle("-fx-background-color: forestgreen;");

            }
            editedLabel.setText("");
            for (TrackingInfo trackingInfo : trackingFile.getTrackingInfos()) {
                 String infoName = trackingInfo.getInfoName();
                if (infoName.equals("manuallyEdited")) {
                    editedLabel.setText("M");
                }
            }


            setText(null);
            setGraphic(containerId);

        }

    }


    private int getVersion(LeightweightTracking tracking){
        for (TrackingInfo trackingInfo : tracking.getTrackingInfos()) {

            if(trackingInfo.getInfoName().equals(Constants.FH_GPS_LOGGER_VERSION)){
                String infoValue = trackingInfo.getInfoValue();
                int version = Integer.parseInt(infoValue);
                return version;
            }
        }
        return -1;
    }

    private String getTransportationModeString(Set<TransportType> transportTypes){
        StringBuilder stringBuilder = new StringBuilder();
        Iterator<TransportType> iterator = transportTypes.iterator();
        while(iterator.hasNext()){
            TransportType transportType =iterator.next();
            stringBuilder.append(transportType.name());

            if(iterator.hasNext()){
                stringBuilder.append("; ");
            }
        }
        return stringBuilder.toString();
    }







}
