package sample;

import at.fhv.transportClassifier.segmentsplitting.Segment;
import java.io.IOException;
import java.time.format.DateTimeFormatter;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

/**
 * Created by Johannes on 04.02.2017.
 */
public class ListCellSegment extends ListCell<Segment> {

    private DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy - HH:mm:s");

    @FXML
    private Label startTimeLabel;
    @FXML
    private Label endTimeLabel;
    @FXML
    private Label durationLabel;
    @FXML
    private Label transportationMode;
    @FXML
    private VBox containerId;
    @FXML
    private Label originLabel;
    private FXMLLoader mLLoader;


    @Override
    protected void updateItem(Segment segment, boolean empty) {
        super.updateItem(segment, empty);

        if(empty || segment == null) {

            setText(null);
            setGraphic(null);

        } else {

            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource(
                    "/src/main/resources/CellSegment.fxml"));
                mLLoader.setController(this);


                try {
                    mLLoader.load();
                    startTimeLabel.setTextFill(Color.BLACK);
                    endTimeLabel.setTextFill(Color.BLACK);
                    transportationMode.setTextFill(Color.BLACK);
                    originLabel.setTextFill(Color.BLACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            String startTimeStr = segment.getStartTime().format(dateTimeFormatter);
            String endTimeStr = segment.getEndTime().format(dateTimeFormatter);
            startTimeLabel.setText(startTimeStr);
            endTimeLabel.setText(endTimeStr);

            durationLabel.setText(segment.getDuration().toString());

            String transportationModeString = segment.getPreType().name();
            transportationMode.setText(transportationModeString);

            originLabel.setText(segment.getOrigin());
            setText(null);
            setGraphic(containerId);

        }

    }



}
