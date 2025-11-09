package org.example.mini;

import javafx.application.Application;
import javafx.stage.Stage;
import org.example.mini.view.StartView;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {
        StartView startView = new StartView(primaryStage);
        startView.show(); // show the initial menu view
    }

    public static void main(String[] args) {
        launch(args);
    }
}
