package sample;

import java.io.IOException;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.paint.Color;
import sample.modul.Modul;

/**
 * Created by Johannes on 04.02.2017.
 */
public class ModulCellController extends ListCell<Modul> {



    @FXML
    private Node containerId;

    @FXML
    private Label modulNameId;
    private FXMLLoader mLLoader;


    @Override
    protected void updateItem(Modul modul, boolean empty) {
        super.updateItem(modul, empty);

        if(empty || modul == null) {

            setText(null);
            setGraphic(null);
        } else {

            if (mLLoader == null) {
                mLLoader = new FXMLLoader(getClass().getResource(
                    "/ModulCell.fxml"));
                mLLoader.setController(this);

                try {
                    mLLoader.load();
                    modulNameId.setTextFill(Color.BLACK);
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }

            modulNameId.setText(modul.getName());
            setGraphic(containerId);

        }

    }





}
