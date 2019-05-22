package cz.cuni.mff.kurankot.busreservationsystem;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * Main class, starts window form application
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("sample.fxml"));
        primaryStage.setTitle("BusReservationSystem");
        primaryStage.setScene(new Scene(root, 450, 500));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }
}
