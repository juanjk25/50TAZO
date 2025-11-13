package org.example.mini.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.Parent;

/**
 * Controller for the start (menu) screen.
 * <p>
 * This controller handles the user's choice of how many CPU opponents
 * to play against and transitions from the start view to the main game view.
 * Each public handler method corresponds to a button in the start UI.
 * </p>
 */
public class StartController {

    /**
     * Starts a new game with one CPU opponent and switches to the game view.
     *
     * @param event the action event triggered by the UI (e.g. button click)
     * @throws IOException if the game view FXML cannot be loaded
     */
    @FXML
    private void startGame1(ActionEvent event) throws IOException {
        switchToGame(event, 1);
    }

    /**
     * Starts a new game with two CPU opponents and switches to the game view.
     *
     * @param event the action event triggered by the UI (e.g. button click)
     * @throws IOException if the game view FXML cannot be loaded
     */
    @FXML
    private void startGame2(ActionEvent event) throws IOException {
        switchToGame(event, 2);
    }

    /**
     * Starts a new game with three CPU opponents and switches to the game view.
     *
     * @param event the action event triggered by the UI (e.g. button click)
     * @throws IOException if the game view FXML cannot be loaded
     */
    @FXML
    private void startGame3(ActionEvent event) throws IOException {
        switchToGame(event, 3);
    }


    /**
     * Loads the game view, initializes the {@code GameController} with the
     * chosen number of CPU players and replaces the current scene.
     *
     * @param event    the action event from the start screen control
     * @param numCPUs  the number of CPU players to initialize in the game
     * @throws IOException if the FXML for the game view cannot be loaded
     */
    private void switchToGame(ActionEvent event, int numCPUs) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mini/view/GameView.fxml"));
        Parent root = loader.load();

        // Obtains the controller and initializes the game with the number of CPUs
        GameController gameController = loader.getController();
        gameController.initializeGame(numCPUs); // Indicate the number of CPU to play

        // Changes the scene to the game view
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Cincuentazo - Juego");
        stage.setScene(new Scene(root));
        stage.show();
    }
}