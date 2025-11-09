package org.example.mini.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.application.Platform;

import org.example.mini.model.card.Card;
import org.example.mini.model.Table;
import org.example.mini.model.game.Game;
import org.example.mini.model.player.HumanPlayer;
import org.example.mini.model.player.MachinePlayer;



public class GameView {

    private final Game game;
    private final Stage stage;

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

        for (Card card : human.getHand()) {
            try {
                Image image = new Image(getClass().getResourceAsStream(card.getImagePath()));
                ImageView imageView = new ImageView(image);
                imageView.setFitWidth(90);
                imageView.setFitHeight(130);
                imageView.setPreserveRatio(true);

                // --- Start drag (when user clicks and drags the card) ---
                imageView.setOnDragDetected(e -> {
                    Dragboard db = imageView.startDragAndDrop(TransferMode.MOVE);
                    ClipboardContent content = new ClipboardContent();
                    content.putString(card.getValue() + "-" + card.getSuit());
                    db.setContent(content);
                    e.consume();
                });

                // --- Define drag-over behavior on the table image (center) ---
                imgLastCard.setOnDragOver(e -> {
                    if (e.getGestureSource() != imgLastCard && e.getDragboard().hasString()) {
                        e.acceptTransferModes(TransferMode.MOVE);
                    }
                    e.consume();
                });

                // --- Handle drop (when user releases card over the table) ---
                imgLastCard.setOnDragDropped(e -> {
                    Dragboard db = e.getDragboard();
                    if (db.hasString()) {
                        playHumanCard(card); // plays the card (removes from hand and places on table)
                        e.setDropCompleted(true);
                    }
                    e.consume();
                });


                handContainer.getChildren().add(imageView);
            } catch (Exception e) {
                System.out.println("Error showing card: " + card.getImagePath());
            }
        }


    }

    /**
     * Handles when the human player clicks on a card.
     */
    private void playHumanCard(Card card) {
        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);
        boolean valid = game.getTable().placeCard(card);

        if (!valid) {
            // Invalid move — show message and do nothing else
            lblTableSum.setText("Cannot exceed 50!");
            return;
        }

        // Valid move
        human.getHand().remove(card);
        updateTable();
        updateHand();

        game.nextTurn();
        updateTurn();

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

            Platform.runLater(this::updateTurn);
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