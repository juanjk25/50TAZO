package org.example.mini.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.Parent;



public class StartController {

    @FXML
    private void startGame1(ActionEvent event) throws IOException {
        switchToGame(event, 1);
    }

    @FXML
    private void startGame2(ActionEvent event) throws IOException {
        switchToGame(event, 2);
    }

    @FXML
    private void startGame3(ActionEvent event) throws IOException {
        switchToGame(event, 3);
    }

    private void switchToGame(ActionEvent event, int numCPUs) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mini/view/GameView.fxml"));
        Parent root = loader.load();

        // Obtener el controlador del juego
        GameController gameController = loader.getController();
        gameController.initializeGame(numCPUs); // Pasamos el número de CPUs

        // Cambiar la escena
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource()).getScene().getWindow();
        stage.setTitle("Cincuentazo - Juego");
        stage.setScene(new Scene(root));
        stage.show();
    }
}