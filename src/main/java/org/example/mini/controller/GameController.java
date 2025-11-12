package org.example.mini.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import org.example.mini.model.Table;
import org.example.mini.model.card.Card;
import org.example.mini.model.game.Game;
import org.example.mini.model.player.*;
import org.example.mini.util.TableMonitorThread;
import org.example.mini.util.TurnMonitorThread;


import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;
import javafx.scene.Node;

/**
 * Main controller for the game view.
 * Handles the interaction between the JavaFX UI and the {@link Game} model.
 *
 * Responsibilities:
 * - Initialize and update the UI (table, cards, turn).
 * - Manage background threads for CPU turns and table monitoring.
 * - Provide methods to play cards and advance the game.
 */
public class GameController implements IGameController {

    /**
     * Label that shows the current table sum.
     */
    @FXML private Label lblTableSum;

    /**
     * Label that shows information about the last played card or messages.
     */
    @FXML private Label lblLastCard;

    /**
     * Container for the human player's hand.
     */
    @FXML private HBox handContainer;

    /**
     * ImageView that displays the last card played.
     */
    @FXML private ImageView imgLastCard;

    /**
     * Label that displays the current turn as text.
     */
    @FXML private Label lblTurn;


    /**
     * Number of CPU players in the game (configurable).
     */
    private int cpuCount;

    /**
     * Reference to the game model.
     */
    private Game game;


    /**
     * Example additional control in the view; can be used for status messages.
     */
    @FXML
    private Label lblStatus; // Ejemplo de un control en tu vista (puedes cambiarlo según tu FXML)

    /**
     * Volatile flag indicating whether the human player can play.
     * Modified from different threads, hence volatile.
     */
    private volatile boolean playerCanPlay = true;

    /**
     * Volatile flag used to prevent concurrent processing of the same turn.
     */
    private volatile boolean turnProcessing = false;

    /**
     * Thread that monitors the table and forces periodic UI updates.
     */
    private TableMonitorThread tableMonitor;

    /**
     * Thread that could monitor turn changes (not fully used here).
     */
    private TurnMonitorThread turnMonitor;

    /**
     * Refresh interval in milliseconds for table monitoring.
     */
    private static final int REFRESH_INTERVAL = 100; // Update every 100 ms

    /**
     * Horizontal container that shows the players (each player in a VBox).
     */
    @FXML private HBox playersContainer;


    /**
     * Initializes the controller with the given {@link Game} instance.
     *
     * @param game the already-created game model
     */
    @Override
    public void init(Game game) {
        this.game = game;

        // Start the table monitor thread
        startTableMonitoring();

        // Start the turn monitor thread
        startTurnMonitoring();

        // Show players in the UI
        showPlayers();

        // Initial updates
        updateTable();
        showPlayerHand();
        updateTurnDisplay();
    }

    /**
     * Initializes a new game with the given number of CPUs.
     * Creates the {@link Game} instance, starts it and updates the UI.
     *
     * @param cpuCount number of CPU players to create
     */
    public void initializeGame(int cpuCount) {
        this.cpuCount = cpuCount;
        this.game = new Game(cpuCount);
        this.game.start(); // Init cards
        init(game); // Update UI with the new game
    }

    /**
     * Builds and displays all players in the UI.
     * Creates a VBox per player and adds it to {@code playersContainer}.
     */
    private void showPlayers() {
        playersContainer.getChildren().clear();

        for (IPlayer player : game.getPlayers()) {
            VBox playerBox = createPlayerBox(player);
            playersContainer.getChildren().add(playerBox);
        }

        updateTurnDisplay(); // Update the active player highlight
    }

    /**
     * Creates a visual node (VBox) that represents a player.
     * Human players show real cards; machine players show card backs.
     *
     * @param player the player to represent
     * @return VBox containing the player's name and card container
     */
    private VBox createPlayerBox(IPlayer player) {
        VBox playerBox = new VBox(5);
        playerBox.setAlignment(Pos.CENTER);
        playerBox.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-padding: 10; -fx-background-radius: 10;");

        // Player Name
        Label nameLabel = new Label(player.getName());
        nameLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: white;");

        // Container for cards
        HBox cardsBox = new HBox(5);
        cardsBox.setAlignment(Pos.CENTER);

        // Show cards based on player type
        if (player.isHuman()) {
            // Show real cards
            for (Card card : player.getHand()) {
                ImageView cardView = new ImageView(new Image(getClass().getResourceAsStream(card.getImagePath())));
                cardView.setFitWidth(60);
                cardView.setFitHeight(90);
                cardsBox.getChildren().add(cardView);
            }
        } else {
            // Show back cards for machine players
            for (int i = 0; i < player.getHand().size(); i++) {
                ImageView backView = new ImageView(new Image(getClass().getResourceAsStream("/org/example/mini/view/images/_/cardback.png")));
                backView.setFitWidth(60);
                backView.setFitHeight(90);
                cardsBox.getChildren().add(backView);
            }
        }

        playerBox.getChildren().addAll(nameLabel, cardsBox);
        return playerBox;
    }

    /**
     * Updates the UI to visually highlight the player who currently has the turn.
     * Iterates the children of {@code playersContainer} and compares names.
     *
     * @param currentPlayer the active player
     */
    private void updateTurnDisplay(IPlayer currentPlayer) {
        for (Node node : playersContainer.getChildren()) {
            VBox box = (VBox) node;
            Label nameLabel = (Label) box.getChildren().get(0);

            if (nameLabel.getText().equals(currentPlayer.getName())) {
                box.setStyle("-fx-background-color: rgba(255,215,0,0.4); -fx-padding: 10; -fx-background-radius: 10;");
            } else {
                box.setStyle("-fx-background-color: rgba(255,255,255,0.15); -fx-padding: 10; -fx-background-radius: 10;");
            }
        }
    }

    /**
     * UI-safe version that obtains the current player from the model and calls {@link #updateTurnDisplay(IPlayer)}.
     * Does nothing if {@code game} or the current player is null.
     */
    private void updateTurnDisplay() {
        if (game == null || game.getCurrentPlayer() == null) return;
        updateTurnDisplay(game.getCurrentPlayer());
    }

    /**
     * Starts the thread that monitors the table and forces periodic updates (every {@link #REFRESH_INTERVAL} ms).
     */
    private void startTableMonitoring() {
        tableMonitor = new TableMonitorThread(this::forceTableUpdate, REFRESH_INTERVAL);
        tableMonitor.start();
        System.out.println("Table monitor started");
    }

    /**
     * Starts the thread that monitors turn changes and updates UI accordingly.
     */
    private void startTurnMonitoring() {
        turnMonitor = new TurnMonitorThread(
                game,
                () -> { // If is human turn
                    Platform.runLater(() -> {
                        lblLastCard.setText("Your turn!");
                        handContainer.setDisable(false);
                    });
                },
                () -> { // If is CPU turn
                    Platform.runLater(() -> {
                        lblLastCard.setText("CPU is playing...");
                        handContainer.setDisable(true);
                    });
                }
        );
        turnMonitor.start();
        System.out.println("Turn monitor started");
    }


    /**
     * Forces a complete table update on the JavaFX Application Thread.
     * Updates the table sum, last card text and image.
     *
     * Safe to call from background threads.
     */
    private void forceTableUpdate() {
        if (game != null && game.getTable() != null) {
            // Update table sum
            int currentSum = game.getTable().getTableSum();
            Platform.runLater(() -> {
                lblTableSum.setText("Table sum: " + currentSum);

                // Also updates the last card
                Card lastCard = game.getTable().getLastCard();
                if (lastCard != null) {
                    lblLastCard.setText("Last card: " + lastCard.toString());
                    try {
                        Image image = new Image(getClass().getResourceAsStream(lastCard.getImagePath()));
                        imgLastCard.setImage(image);
                    } catch (Exception e) {
                        System.out.println("Error updating card image: " + e.getMessage());
                    }
                }
            });
        }
    }

    /**
     * Handles the logic for the human player playing a card.
     * Validates the move, updates model and UI, draws a replacement card and triggers CPU turns.
     *
     * @param card the card chosen by the human player
     */
    private void playCard(Card card) {
        if (!playerCanPlay || turnProcessing) return; // Block multiple clicks or invalid state
        turnProcessing = true;
        playerCanPlay = false;

        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);

        // Verify if the card can be played
        if (!card.canBePlayed(game.getTable().getTableSum(), true)) {
            Platform.runLater(() -> lblLastCard.setText("Cannot play - would exceed 50!"));
            turnProcessing = false;
            playerCanPlay = true;
            return;
        }

        // Play the card
        human.getHand().remove(card);
        boolean validMove = game.getTable().placeCard(card, true);

        if (!validMove) {
            Platform.runLater(() -> lblLastCard.setText("Invalid move!"));
            human.addCard(card);
            turnProcessing = false;
            playerCanPlay = true;
            return;
        }

        // Immediate update after play
        forceTableUpdate();

        // Take new card from deck
        Card newCard = game.drawCardWithRecycle();
        if (newCard != null) human.addCard(newCard);

        showPlayerHand();

        Platform.runLater(() -> {
            updateTurnDisplay();
            lblLastCard.setText("You played: " + card.getRank() + " of " + card.getSuit());
        });

        // Check for eliminated players
        game.checkAndEliminatePlayers();
        if (game.isGameOver()) {
            turnProcessing = false;
            playerCanPlay = false;
            stopTableMonitoring();
        } else {
            stopTableMonitoring();
            Platform.runLater(() -> {
                updateTurnDisplay();
                lblLastCard.setText("Game Over - You Win!");
            });
        }

        runMachineTurns();
    }

    /**
     * Shows the human player's hand in the {@code handContainer}.
     * Each card is clickable and calls {@link #playCard(Card)}.
     *
     * This method updates the UI and must run on the JavaFX Application Thread.
     */
    @FXML
    @Override
    public void showPlayerHand() {
        handContainer.getChildren().clear();

        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);

        for (Card card : human.getHand()) {
            // Load the image
            Image image = new Image(getClass().getResourceAsStream(card.getImagePath()));
            ImageView imageView = new ImageView(image);

            // Scale the card image
            imageView.setFitWidth(90);
            imageView.setFitHeight(130);
            imageView.setPreserveRatio(true);

            // Make it clickable
            imageView.setOnMouseClicked(e -> playCard(card));

            // Add it to the HBox
            handContainer.getChildren().add(imageView);
        }
    }

    /**
     * Runs the CPU turns in a separate thread.
     * Advances turns in the model and requests each CPU to play.
     * UI updates are performed via {@link Platform#runLater(Runnable)}.
     */
    private void runMachineTurns() {
        Thread cpuThread = new Thread(() -> {
            while (!game.isGameOver()) {
                game.nextTurn();
                IPlayer current = game.getCurrentPlayer();

                if (current instanceof MachinePlayer) {
                    MachinePlayer cpu = (MachinePlayer) current;

                    // Update display to show CPU turn
                    Platform.runLater(this::updateTurnDisplay);

                    // Delay before playing: 3-4 seconds
                    int delayBeforePlay = 3000 + (int)(Math.random() * 1000);
                    try {
                        Thread.sleep(delayBeforePlay);
                    } catch (InterruptedException ignored) {}

                    Card move = cpu.playCard(game.getTable().getTableSum());

                    if (move != null) {
                        // Play card
                        boolean validMove = game.getTable().placeCard(move, false);

                        // Immediatelly update after play
                        forceTableUpdate();

                        Platform.runLater(() -> {
                            if (validMove) {
                                lblLastCard.setText(cpu.getName() + " played: " + move.getRank() + " of " + move.getSuit());
                            }
                            updateTurnDisplay(); // Update after play if the CPU played is valid
                        });

                        // Delay after playing: 1-2 seconds
                        int delayAfterPlay = 1000 + (int)(Math.random() * 1000);
                        try {
                            Thread.sleep(delayAfterPlay);
                        } catch (InterruptedException ignored) {}

                        // CPU takes new card
                        Card newCard = game.drawCardWithRecycle();
                        if (newCard != null) {
                            cpu.addCard(newCard);
                        }

                        game.checkAndEliminatePlayers();

                        // Update if some players were eliminated
                        Platform.runLater(this::updateTurnDisplay);

                        // Delay between turns: 1-2 seconds
                        int delayBetweenTurns = 1000 + (int)(Math.random() * 1000);
                        try {
                            Thread.sleep(delayBetweenTurns);
                        } catch (InterruptedException ignored) {}

                    } else {
                        game.checkAndEliminatePlayers();
                        Platform.runLater(this::updateTurnDisplay); // Update if it can´t play
                    }
                } else {
                    // Take back to the human player
                    Platform.runLater(() -> {
                        updateTurnDisplay();
                        lblLastCard.setText("Your turn!");
                        playerCanPlay = true;      // Now the Human Player can play
                        turnProcessing = false;    // Unlock the logic
                    });
                    break;
                }
            }

            if (game.isGameOver()) {
                stopTableMonitoring();
                stopTurnMonitoring();
                if (turnMonitor != null) {
                    turnMonitor.stopMonitoring();
                }
                Platform.runLater(() -> {
                    updateTurnDisplay(); // Update Turns if the game ends
                    IPlayer winner = game.getWinner();
                    String winnerText = (winner != null) ? winner.getName() : "No winner";
                    lblLastCard.setText("Game Over - Winner: " + winnerText);
                    handContainer.getChildren().clear();
                });
            }
        });

        cpuThread.setDaemon(true);
        cpuThread.start();
    }

    /**
     * Stops the table monitor if running.
     */
    private void stopTableMonitoring() {
        if (tableMonitor != null) {
            tableMonitor.stopMonitoring();
            System.out.println("Table monitor stopped");
        }
    }

    /**
     * Stops the turn monitor if present.
     */
    private void stopTurnMonitoring() {
        if (turnMonitor != null) {
            turnMonitor.stopMonitoring();
            System.out.println("Table monitor stopped");
        }
    }

    /**
     * Updates the table view (sum and last card).
     * Assumes it is executed on the JavaFX Application Thread.
     */
    @FXML
    @Override
    public void updateTable() {
        Table table = game.getTable();
        lblTableSum.setText("Table sum: " + table.getTableSum());

        Card last = table.getLastCard();
        if (last != null) {
            lblLastCard.setText("Last card: " + last.toString());
            Image image = new Image(getClass().getResourceAsStream(last.getImagePath()));
            imgLastCard.setImage(image);
        } else {
            lblLastCard.setText("Last card: None");
            imgLastCard.setImage(null);
        }
    }
}
