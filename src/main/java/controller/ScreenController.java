package controller;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ScreenController {

    public static void switchScreen(String fxmlFile, Stage stage)
            throws IOException {

        Parent root = FXMLLoader.load(
                ScreenController.class.getResource("/view/" + fxmlFile)
        );

        Scene scene = new Scene(root);

        stage.setScene(scene);
        stage.show();
    }
}
