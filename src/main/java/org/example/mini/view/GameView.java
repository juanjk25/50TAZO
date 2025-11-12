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



public class GameView {

    private final Game game;
    private final Stage stage;

    private TurnMonitorThread turnMonitor;
    private boolean playerCanPlay = false;


    private Label lblTurn;
    private Label lblTableSum;
    private ImageView imgLastCard;
    private HBox handContainer;

    public GameView(Game game, Stage stage) {
        this.game = game;
        this.stage = stage;
    }

    /**
     * Builds and shows the game window.
     */
    public void show() {
        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        // --- Top: status bar ---
        VBox topBox = new VBox(5);
        lblTurn = new Label("Turn: " + game.getCurrentPlayer().getName());
        lblTableSum = new Label("Table sum: " + game.getTable().getTableSum());
        topBox.getChildren().addAll(lblTurn, lblTableSum);
        topBox.setAlignment(Pos.CENTER);

        // --- Center: last card played ---
        VBox centerBox = new VBox(10);
        centerBox.setAlignment(Pos.CENTER);
        imgLastCard = new ImageView();
        imgLastCard.setFitWidth(100);
        imgLastCard.setFitHeight(140);
        updateTable();
        centerBox.getChildren().add(imgLastCard);

        // --- Bottom: player's hand ---
        handContainer = new HBox(10);
        handContainer.setAlignment(Pos.CENTER);
        updateHand();

        root.setTop(topBox);
        root.setCenter(centerBox);
        root.setBottom(handContainer);

        Scene scene = new Scene(root, 600, 500);
        stage.setScene(scene);
        stage.setTitle("50tazo - Game");
        stage.show();

        turnMonitor = new TurnMonitorThread(
                game,
                () -> { // cuando es turno del humano
                    playerCanPlay = true;
                    handContainer.setDisable(false);
                },
                () -> { // cuando es turno de CPU
                    playerCanPlay = false;
                    handContainer.setDisable(true);
                }
        );
        turnMonitor.start();

    }

    /**
     * Updates the visual state of the table (sum, last card)
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
     * Displays the human player's hand in the interface, with click events.
     */
    public void updateHand() {
        handContainer.getChildren().clear();

        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);

        // ⚠️ Asegurarse de no duplicar los eventos del área central
        imgLastCard.setOnDragOver(null);
        imgLastCard.setOnDragDropped(null);

        for (Card card : human.getHand()) {
            try {
                Image image = new Image(getClass().getResourceAsStream(card.getImagePath()));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(90);
                imageView.setFitHeight(130);
                imageView.setPreserveRatio(true);

                // --- Click directo en la carta ---
                imageView.setOnMouseClicked(e -> {
                    if (playerCanPlay) {           // 🔒 solo si es turno del humano
                        playerCanPlay = false;      // 🚫 bloquear inmediatamente
                        handContainer.setDisable(true);
                        playHumanCard(card);
                    }
                });

                handContainer.getChildren().add(imageView);
            } catch (Exception e) {
                System.out.println("Error showing card: " + card.getImagePath());
            }
        }

        // --- Define drag behavior only once for the table image (center) ---
        imgLastCard.setOnDragOver(e -> {
            if (playerCanPlay && e.getGestureSource() != imgLastCard && e.getDragboard().hasString()) {
                e.acceptTransferModes(TransferMode.MOVE);
            }
            e.consume();
        });

        imgLastCard.setOnDragDropped(e -> {
            Dragboard db = e.getDragboard();
            if (playerCanPlay && db.hasString()) {  // 🔒 verificar turno aquí también
                playerCanPlay = false;
                handContainer.setDisable(true);
                playHumanCard(human.getHand().stream()
                        .filter(c -> (c.getValue() + "-" + c.getSuit())
                                .equals(db.getString()))
                        .findFirst()
                        .orElse(null));
                e.setDropCompleted(true);
            }
            e.consume();
        });
    }


    /**
     * Handles when the human player clicks on a card.
     */
    private void playHumanCard(Card card) {
        // ❌ Prevent click if it's not player's turn
        if (!playerCanPlay) return;

        // 🚫 Block immediately
        playerCanPlay = false;
        handContainer.setDisable(true);

        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);
        boolean valid = game.getTable().placeCard(card);

        if (!valid) {
            lblTableSum.setText("Cannot exceed 50!");
            playerCanPlay = true;
            handContainer.setDisable(false);
            return;
        }

        // ✅ Valid move
        human.getHand().remove(card);
        updateTable();
        updateHand();

        game.nextTurn();
        updateTurn();

        // 🚀 Let CPUs play
        runCpuTurns();
    }



    /**
     * Runs CPU turns in a background thread.
     */
    private void runCpuTurns() {
        Thread cpuThread = new Thread(() -> {
            while (game.getCurrentPlayer() instanceof MachinePlayer && !game.isGameOver()) {
                MachinePlayer cpu = (MachinePlayer) game.getCurrentPlayer();
                Card move = cpu.playCard(game.getTable().getTableSum());

                if (move != null) {
                    boolean valid = game.getTable().placeCard(move);
                    if (!valid) {
                        Platform.runLater(() -> lblTableSum.setText("CPU move skipped (would exceed 50)"));
                        game.nextTurn();  // pasar turno
                        continue;         // ahora dentro del while
                    }

                    Platform.runLater(() -> {
                        game.getTable().placeCard(move);
                        updateTable();
                        updateTurn();
                    });
                }

                game.nextTurn();

                try {
                    Thread.sleep(1200);
                } catch (InterruptedException ignored) {}
            }

            Platform.runLater(() -> {
                updateTurn();
                playerCanPlay = true;
                handContainer.setDisable(false);
                updateHand(); // reconstruye los eventos limpios
            });
        });

        cpuThread.setDaemon(true);
        cpuThread.start();
    }

    /**
     * Updates the label showing whose turn it is.
     */
    public void updateTurn() {
        lblTurn.setText("Turn: " + game.getCurrentPlayer().getName());
    }
}