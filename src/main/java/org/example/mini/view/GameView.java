package org.example.mini.view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import org.example.mini.model.card.Card;
import org.example.mini.model.Table;
import org.example.mini.model.game.Game;
import org.example.mini.model.player.HumanPlayer;

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
        updateTable(); // load initial image
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
     * Updates the table information visually.
     */
    public void updateTable() {
        Table table = game.getTable();
        lblTableSum.setText("Table sum: " + table.getTableSum());

        Card lastCard = table.getLastCard();
        if (lastCard != null) {
            try {
                Image img = new Image(getClass().getResourceAsStream(lastCard.getImagePath()));
                imgLastCard.setImage(img);
            } catch (Exception e) {
                System.out.println("Error loading image: " + lastCard.getImagePath());
            }
        } else {
            imgLastCard.setImage(null);
        }
    }

    /**
     * Updates the player's hand displayed at the bottom.
     */
    public void updateHand() {
        handContainer.getChildren().clear();
        HumanPlayer player = (HumanPlayer) game.getPlayers().get(0);

        for (Card card : player.getHand()) {
            try {
                Image img = new Image(getClass().getResourceAsStream(card.getImagePath()));
                ImageView iv = new ImageView(img);
                iv.setFitWidth(90);
                iv.setFitHeight(130);
                iv.setPreserveRatio(true);
                handContainer.getChildren().add(iv);
            } catch (Exception e) {
                System.out.println("Error showing card: " + card.getImagePath());
            }
        }
    }

    /**
     * Updates the turn label.
     */
    public void updateTurn() {
        lblTurn.setText("Turn: " + game.getCurrentPlayer().getName());
    }
}
