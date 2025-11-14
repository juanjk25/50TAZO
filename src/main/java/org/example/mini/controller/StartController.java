package org.example.mini.controller;


import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BackgroundPosition;
import javafx.scene.layout.BackgroundRepeat;
import javafx.scene.layout.BackgroundSize;
import javafx.scene.layout.StackPane;   // <-- import for rootPane
import javafx.stage.Stage;

import java.io.IOException;

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
     * Root container of the start view.
     * <p>
     * This {@link StackPane} is injected from the FXML file and is used as the
     * main layout node on which the background image is applied.
     * </p>
     */
    @FXML
    private StackPane rootPane;

    /**
     * Initializes the start view controller after the FXML has been loaded.
     * <p>
     * This method is automatically invoked by the {@code FXMLLoader}. It attempts
     * to load the background image for the start screen (for example
     * {@code start_casino.png}) and apply it as a {@link Background} to the
     * {@link #rootPane}. If the image cannot be loaded, a neutral gradient
     * background is used as a fallback.
     * </p>
     */
    @FXML
    private void initialize() {
        // Load and apply card back (or start_casino) as background
        try {
            Image bgImage = new Image(
                    getClass().getResourceAsStream("/org/example/mini/view/images/_/start_casino.png")
            );

            BackgroundImage backgroundImage = new BackgroundImage(
                    bgImage,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundRepeat.NO_REPEAT,
                    BackgroundPosition.CENTER,
                    new BackgroundSize(
                            BackgroundSize.AUTO,
                            BackgroundSize.AUTO,
                            true,   // cover width
                            true,   // cover height
                            false,
                            true    // contain
                    )
            );

            rootPane.setBackground(new Background(backgroundImage));
        } catch (Exception e) {
            System.err.println("Could not load start background image: " + e.getMessage());
            // Fallback neutral color
            rootPane.setStyle("-fx-background-color: linear-gradient(to bottom, #f5f5f5, #dcdcdc);");
        }
    }

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

        // Obtain the current stage from the event source
        Stage stage = (Stage) ((javafx.scene.Node) event.getSource())
                .getScene()
                .getWindow();

        FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/org/example/mini/view/GameView.fxml")
        );
        Parent root = loader.load();

        // Obtain the controller and initialize the game with the number of CPUs
        GameController gameController = loader.getController();
        gameController.initializeGame(numCPUs); // Indicate the number of CPU to play

        // ADD THE ICON TO THE GAME WINDOW
        try {
            Image icon = new Image(
                    getClass().getResourceAsStream("/org/example/mini/view/images/_/poker-cards.png")
            );
            if (icon != null) {
                stage.getIcons().add(icon);
            } else {
                System.err.println("Icon resource not found: /org/example/mini/view/images/_/poker-cards.png");
            }
        } catch (Exception e) {
            System.err.println("No se pudo cargar el icono: " + e.getMessage());
            // Do not stop the application if there is an error with the icon
        }

        // Change the scene to the game view
        stage.setTitle("Cincuentazo - Juego");
        stage.setScene(new Scene(root));
        stage.show();
    }
}