package at.fhv.jn.googleMaps;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{


        FXMLLoader loader = new FXMLLoader(
                getClass().getResource(
                    "../../../../../resources/GoogleMapsView.fxml"
                ));

        Parent root =loader.load();
        GoogleMapsController controller = loader.getController();
        primaryStage.setTitle("Hello World");
        primaryStage.setScene(new Scene(root, 300, 275));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
