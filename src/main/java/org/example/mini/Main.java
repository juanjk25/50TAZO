package org.example.mini;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
 // or StartController

/**
 * Entry point of the "Cincuentazo" JavaFX application.
 */
public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the start view FXML (its fx:controller is StartController)
        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/mini/view/StartView.fxml")
        );
        Parent root = loader.load();

        // Configure main window
        Scene scene = new Scene(root);
        primaryStage.setTitle("Cincuentazo - Start");

        // Optional: set application icon (same logo used in the game window)
        try {
            Image icon = new Image(
                    getClass().getResourceAsStream("/org/example/mini/view/images/_/poker-cards.png")
            );
            if (icon != null) {
                primaryStage.getIcons().add(icon);
            } else {
                System.err.println("Icon resource not found: /org/example/mini/view/images/_/poker-cards.png");
            }
        } catch (Exception e) {
            System.err.println("Could not load app icon: " + e.getMessage());
        }

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}