package org.example.mini.view;

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

public class StartView {
    private final Stage stage;

    public StartView(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void startGameWithFXML(int cpuCount) {
        try {
            // USAR EL MISMO FXML QUE EL DEBUG
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mini/view/GameView.fxml"));
            Parent root = loader.load();

            GameController gameController = loader.getController();
            Game game = new Game(cpuCount);
            game.start();
            gameController.init(game);

            stage.setTitle("Cincuentazo - " + cpuCount + " CPUs");
            stage.setScene(new Scene(root));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public void show() {
        Label lbl = new Label("Select number of CPU players:");
        Spinner<Integer> cpuSpinner = new Spinner<>(1, 3, 1);
        Button btnStart = new Button("Start Game");

        VBox root = new VBox(10, lbl, cpuSpinner, btnStart);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("50tazo - Start");
        stage.setScene(scene);
        stage.show();

        btnStart.setOnAction(e -> {
            int cpuCount = cpuSpinner.getValue();

            // EN LUGAR DE: GameView gameView = new GameView(game, stage);
            // USA:
            startGameWithFXML(cpuCount);
        });
    }
}
