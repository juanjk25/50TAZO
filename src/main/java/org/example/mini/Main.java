package org.example.mini;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.example.mini.controller.GameController;
import org.example.mini.model.game.Game;
import org.example.mini.view.StartView;

/**
 * Main class
 *
 * Entry point of the "Cincuentazo" JavaFX application.
 * It launches either the Start Menu or the Game window directly,
 * depending on the configuration values.
 */
public class Main extends Application {

    // Set this to 'false' to skip the start menu and go straight into the game
    private static final boolean SHOW_START_MENU = true;

    // Number of CPU players used when testing without the start menu
    private static final int TEST_CPU_COUNT = 2; // Options: 1, 2, or 3

    @Override
    public void start(Stage primaryStage) {
        if (SHOW_START_MENU) {
            // Normal mode: show the start menu first
            StartView startView = new StartView(primaryStage);
            startView.show();
        } else {
            // Test mode: skip menu and launch game directly
            startGameDirectly(primaryStage, TEST_CPU_COUNT);
        }
    }

    /**
     * Starts the game directly without showing the start menu.
     * Useful for quick testing during development.
     *
     * @param stage     the main application window
     * @param cpuCount  number of CPU players
     */
    private void startGameDirectly(Stage stage, int cpuCount) {
        try {
            // Load the FXML for the main game view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/example/mini/view/GameView.fxml"));
            Parent root = loader.load();

            // Get the controller and prepare the game
            GameController gameController = loader.getController();

            // Create and initialize a new Game instance
            Game game = new Game(cpuCount);
            game.start(); // Shuffle, deal cards, and initialize the table

            // Initialize the controller with the game
            gameController.init(game);

            // Create the main game scene
            Scene scene = new Scene(root, 400, 500);

            // Optional: load a CSS stylesheet for custom styling
            scene.getStylesheets().add(
                    getClass().getResource("org/example/mini/view/css/game-style.css").toExternalForm()
            );

            // Configure and display the main game window
            stage.setTitle("Cincuentazo - Test Mode (" + cpuCount + " CPUs)");
            stage.setScene(new Scene(root));
            stage.setMinWidth(800);
            stage.setMinHeight(600);
            stage.show();

            // Print test mode info to console
            System.out.println("=== TEST MODE ACTIVE ===");
            System.out.println("Players: 1 human + " + cpuCount + " CPUs");
            System.out.println("Initial table sum: " + game.getTable().getTableSum());
            System.out.println("Cards in deck: " + game.getDeck().size());
            System.out.println("========================");

        } catch (Exception e) {
            System.err.println("Error while starting the game directly:");
            e.printStackTrace();

            // Fallback: show the start menu if an error occurs
            StartView startView = new StartView(stage);
            startView.show();
        }
    }

    public static void main(String[] args) {
        // Print configuration info before starting
        System.out.println("SHOW_START_MENU: " + SHOW_START_MENU);
        System.out.println("TEST_CPU_COUNT: " + TEST_CPU_COUNT);

        // Launch the JavaFX application
        launch(args);
    }
}
