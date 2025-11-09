package org.example.mini.view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Spinner;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.example.mini.model.game.Game;

public class StartView {
    private final Stage stage;

    public StartView(Stage stage) {
        this.stage = stage;
    }

    public void show() {
        Label lbl = new Label("Select number of CPU players:");
        Spinner<Integer> cpuSpinner = new Spinner<>(1, 4, 1);
        Button btnStart = new Button("Start Game");

        VBox root = new VBox(10, lbl, cpuSpinner, btnStart);
        root.setAlignment(Pos.CENTER);
        Scene scene = new Scene(root, 400, 300);

        stage.setTitle("50tazo - Start");
        stage.setScene(scene);
        stage.show();

        btnStart.setOnAction(e -> {
            int cpuCount = cpuSpinner.getValue();
            Game game = new Game(cpuCount);
            GameView gameView = new GameView(game, stage);
            game.start();
            gameView.show();
        });
    }
}
