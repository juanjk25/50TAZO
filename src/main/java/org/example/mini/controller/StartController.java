package org.example.mini.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.Parent;



public class StartController {

    /**
     * Manage the transition to the game screen with the selected number of CPU players
     * @param event
     * @throws IOException
     */
    @FXML
    private void startGame1(ActionEvent event) throws IOException {
        switchToGame(event, 1);
    }

    /**
     * Manage the transition to the game screen with the selected number of CPU players
     * @param event
     * @throws IOException
     */
    @FXML
    private void startGame2(ActionEvent event) throws IOException {
        switchToGame(event, 2);
    }

    /**
     * Manage the transition to the game screen with the selected number of CPU players
     * @param event
     * @throws IOException
     */
    @FXML
    private void startGame3(ActionEvent event) throws IOException {
        switchToGame(event, 3);
    }

    /**
     * Switch to the game view and initialize the game with the specified number of CPU players
     * @param event
     * @param numCPUs
     * @throws IOException
     */
    private void switchToGame(ActionEvent event, int numCPUs) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mini/view/GameView.fxml"));
        Parent root = loader.load();

        // Obtains the controller and initializes the game with the number of CPUs
        GameController gameController = loader.getController();
        gameController.initializeGame(numCPUs); // Pasamos el número de CPUs

        // Changes the scene to the game view
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Cincuentazo - Juego");
        stage.setScene(new Scene(root));
        stage.show();
    }
}