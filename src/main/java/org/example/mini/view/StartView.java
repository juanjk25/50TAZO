package org.example.mini.view;
// Package that contains this class (part of the "view" layer)

import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Parent;
import javafx.fxml.FXMLLoader;
import org.example.mini.model.game.Game;
import org.example.mini.controller.GameController;

/**
 * StartView class
 *
 * This class represents the initial screen of the "Cincuentazo" game.
 * It allows the user to select the number of CPU players and start the game.
 */
public class StartView {
    // Main application window
    private final Stage stage;

    // Constructor: receives and stores the main Stage
    public StartView(Stage stage) {
        this.stage = stage;
    }

    /**
     * Starts the game using the FXML interface (GameView.fxml),
     * with the selected number of CPU players.
     *
     * @param cpuCount number of CPU players chosen by the user
     */
    @FXML
    private void startGameWithFXML(int cpuCount) {
        try {
            // Load the FXML file for the main game view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mini/view/GameView.fxml"));
            Parent root = loader.load();

            // Get the controller associated with the FXML view
            GameController gameController = loader.getController();

            // Create a new Game instance with the selected number of CPUs
            Game game = new Game(cpuCount);
            game.start(); // Start the game logic

            // Initialize the controller with the game instance
            gameController.init(game);

            // Configure and display the main game window
            stage.setTitle("Cincuentazo - " + cpuCount + " CPUs");
            stage.setScene(new Scene(root));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

        } catch (Exception e) {
            // Print stack trace if an error occurs while loading the view or starting the game
            e.printStackTrace();
        }
    }

    /**
     * Displays the start screen where the user selects
     * the number of CPU players and starts the game.
     */
    public void show() {
        // Create UI elements
        Label lbl = new Label("Select number of CPU players:"); // Instruction text
        Spinner<Integer> cpuSpinner = new Spinner<>(1, 3, 1);   // Spinner for selecting between 1 and 3 CPUs
        Button btnStart = new Button("Start Game");              // Button to start the game

        // Arrange elements vertically in a VBox
        VBox root = new VBox(10, lbl, cpuSpinner, btnStart);
        root.setAlignment(Pos.CENTER); // Center all elements
        Scene scene = new Scene(root, 400, 300); // Create a new scene

        // Configure the main window (stage)
        stage.setTitle("50tazo - Start");
        stage.setScene(scene);
        stage.show();

        // Define the button action
        btnStart.setOnAction(e -> {
            // Get the number of CPUs selected by the user
            int cpuCount = cpuSpinner.getValue();

            // Start the game using the FXML view
            startGameWithFXML(cpuCount);
        });
    }
}
