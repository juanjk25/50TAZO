package org.example.mini.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.application.Platform;

import org.example.mini.model.card.Card;
import org.example.mini.model.Table;
import org.example.mini.model.game.Game;
import org.example.mini.model.player.HumanPlayer;
import org.example.mini.model.player.MachinePlayer;
import org.example.mini.util.TurnMonitorThread;

/**
 * GameView class
 *
 * This class builds and manages the main game window for "Cincuentazo".
 * It displays the current game state (table, hands, turns) and handles
 * player interactions like clicking or dragging cards.
 */
public class GameView {

    // --- Core game references ---
    private final Game game;   // Current game logic instance
    private final Stage stage; // Main window for this view

    // Thread to monitor whose turn it is
    private TurnMonitorThread turnMonitor;

    // Flag to know if the human player can make a move
    private boolean playerCanPlay = false;

    // --- UI components ---
    private Label lblTurn;       // Displays the current player's name
    private Label lblTableSum;   // Shows the current table sum
    private ImageView imgLastCard; // Shows the last card played
    private HBox handContainer;  // Displays the human player's cards

    // Constructor: initializes with the active game and window
    public GameView(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
    }

    /**
     * Builds and displays the main game window.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // --- Top section: status info (turn + table sum) ---
        VBox topBox = new VBox(5);
        lblTurn = new Label("Turn: " + game.getCurrentPlayer().getName());
        lblTableSum = new Label("Table sum: " + game.getTable().getTableSum());
        topBox.getChildren().addAll(lblTurn, lblTableSum);
        topBox.setAlignment(Pos.CENTER);

        // --- Center section: last card played ---
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        imgLastCard = new ImageView();
        imgLastCard.setFitWidth(100);
        imgLastCard.setFitHeight(140);
        updateTable(); // Show the current table state
        centerBox.getChildren().add(imgLastCard);

        // --- Bottom section: human player's hand ---
        handContainer = new HBox(10);
        handContainer.setAlignment(Pos.CENTER);
        updateHand(); // Load player cards visually

        // Add all sections to the root layout
        root.setTop(topBox);
        root.setCenter(centerBox);
        root.setBottom(handContainer);

        // Create the main scene and show it
        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("50tazo - Game");
        stage.show();

        // --- Start turn monitoring thread ---
        turnMonitor = new TurnMonitorThread(
                game,
                () -> { // When it's the human player's turn
                    playerCanPlay = true;
                    handContainer.setDisable(false);
                },
                () -> { // When it's a CPU player's turn
                    playerCanPlay = false;
                    handContainer.setDisable(true);
                }
        );
        turnMonitor.start();
    }

    /**
     * Updates the table display: sum and last card image.
     */
    public void updateTable() {
        Table table = game.getTable();
        lblTableSum.setText("Table sum: " + table.getTableSum());

        Card last = table.getLastCard();
        if (last != null) {
            try {
                Image image = new Image(getClass().getResourceAsStream(last.getImagePath()));
                imgLastCard.setImage(image);
            } catch (Exception e) {
                System.out.println("Error loading image: " + last.getImagePath());
            }
        } else {
            imgLastCard.setImage(null);
        }
    }

    /**
     * Updates the human player's hand on screen and assigns click/drag events.
     */
    public void updateHand() {
        handContainer.getChildren().clear();

        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);

        // Clear any previous drag events on the table area
        imgLastCard.setOnDragOver(null);
        imgLastCard.setOnDragDropped(null);

        // --- Render each card in the player's hand ---
        for (Card card : human.getHand()) {
            try {
                Image image = new Image(getClass().getResourceAsStream(card.getImagePath()));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(90);
                imageView.setFitHeight(130);
                imageView.setPreserveRatio(true);

                // --- Direct click on card ---
                imageView.setOnMouseClicked(e -> {
                    // Only allow play if it's the player's turn
                    if (playerCanPlay) {
                        playerCanPlay = false;      // Lock play immediately
                        handContainer.setDisable(true);
                        playHumanCard(card);
                    }
                });

                handContainer.getChildren().add(imageView);
            } catch (Exception e) {
                System.out.println("Error showing card: " + card.getImagePath());
            }
        }

        // --- Define drag-and-drop behavior for the table image ---
        imgLastCard.setOnDragOver(e -> {
            if (playerCanPlay && e.getGestureSource() != imgLastCard && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        imgLastCard.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (playerCanPlay && db.hasString()) {  // Validate turn again
                playerCanPlay = false;
                handContainer.setDisable(true);
                playHumanCard(human.getHand().stream()
                        .filter(c -> (c.getValue() + "-" + c.getSuit()).equals(db.getString()))
                        .findFirst()
                        .orElse(null));
                e.setDropCompleted(true);
            }
            e.consume();
        });
    }

    /**
     * Handles when the human player clicks or drops a card to play it.
     */
    private void playHumanCard(Card card) {
        // Block if it's not the player's turn
        if (!playerCanPlay) return;

        playerCanPlay = false;
        handContainer.setDisable(true);

        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);
        boolean valid = game.getTable().placeCard(card);

        // Invalid move (sum exceeds 50)
        if (!valid) {
            lblTableSum.setText("Cannot exceed 50!");
            playerCanPlay = true;
            handContainer.setDisable(false);
            return;
        }

        // Valid move: remove card and refresh UI
        human.getHand().remove(card);
        updateTable();
        updateHand();

        game.nextTurn();
        updateTurn();

        // Trigger CPU players' turns
        runCpuTurns();
    }

    /**
     * Runs CPU player turns in a background thread.
     */
    private void runCpuTurns() {
        Thread cpuThread = new Thread(() -> {
            // Keep looping while CPUs have turns and the game isn't over
            while (game.getCurrentPlayer() instanceof MachinePlayer && !game.isGameOver()) {
                MachinePlayer cpu = (MachinePlayer) game.getCurrentPlayer();
                Card move = cpu.playCard(game.getTable().getTableSum());

                if (move != null) {
                    boolean valid = game.getTable().placeCard(move);
                    if (!valid) {
                        // CPU tried to play an invalid move
                        Platform.runLater(() -> lblTableSum.setText("CPU move skipped (would exceed 50)"));
                        game.nextTurn();
                        continue;
                    }

                    // Apply the valid move visually in the JavaFX thread
                    Platform.runLater(() -> {
                        game.getTable().placeCard(move);
                        updateTable();
                        updateTurn();
                    });
                }

                // Proceed to the next turn and pause slightly
                game.nextTurn();

                try {
                    Thread.sleep(1200); // Small delay for realism
                } catch (InterruptedException ignored) {}
            }

            // Once CPUs are done, return control to the human player
            Platform.runLater(() -> {
                updateTurn();
                playerCanPlay = true;
                handContainer.setDisable(false);
                updateHand(); // Rebuild UI and clean events
            });
        });

        cpuThread.setDaemon(true); // Daemon thread stops when app closes
        cpuThread.start();
    }

    /**
     * Updates the label showing the current player's name.
     */
    public void updateTurn() {
        lblTurn.setText("Turn: " + game.getCurrentPlayer().getName());
    }
}
