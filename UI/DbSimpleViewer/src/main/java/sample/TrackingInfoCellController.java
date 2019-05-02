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
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;

/**
 * Created by Johannes on 04.02.2017.
 */
public class TrackingInfoCellController extends ListCell<TrackingInfo> {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:s");


    @FXML
    private Node containerId;

    @FXML
    private Label leftLabel;
    @FXML
    private Label rightLabel;

    private FXMLLoader mLLoader;


    @Override
    protected void updateItem(TrackingInfo trackingFile, boolean empty) {
        super.updateItem(trackingFile, empty);

        if(empty || trackingFile == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource(
                    "/src/main/resources/TrackingInfoCell.fxml"));
                mLLoader.setController(this);


                try {
                    mLLoader.load();
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            String infoName = trackingFile.getInfoName();
            String infoValue = trackingFile.getInfoValue();

            leftLabel.setText(infoName);
            rightLabel.setText(infoValue);

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
