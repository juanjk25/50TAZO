package org.example.mini.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import org.example.mini.model.Table;
import org.example.mini.model.card.Card;
import org.example.mini.model.game.Game;
import org.example.mini.model.player.*;

public class GameController implements IGameController {

    @FXML private Label lblTableSum;
    @FXML private Label lblLastCard;
    @FXML private HBox handContainer;

    private Game game;

    @FXML
    private Label lblStatus; // Ejemplo de un control en tu vista (puedes cambiarlo según tu FXML)

    private int cpuCount;

    public void initializeGame(int cpuCount) {
        this.cpuCount = cpuCount;
        System.out.println("Game initialized with " + cpuCount + " CPU players");
        // Aquí puedes agregar tu lógica inicial, como crear jugadores, etc.
    }

    @FXML
    public void initialize() {
        // Este método se llama automáticamente al cargar el FXML
        System.out.println("GameController loaded.");
    }

    @Override
    public void init(Game game) {
        this.game = game;
        updateTable();
        showPlayerHand();
    }

    @FXML
    @Override
    public void updateTable() {
        Table table = game.getTable();
        lblTableSum.setText("Table sum: " + table.getTableSum());
        Card last = table.getLastCard();
        lblLastCard.setText("Last card: " + (last != null ? last.toString() : "None"));
    }

    @FXML
    @Override
    public void showPlayerHand() {
        handContainer.getChildren().clear();
        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);

        for (Card card : human.getHand()) {
            Button btn = new Button(card.toString());
            btn.setOnAction(e -> playCard(card));
            handContainer.getChildren().add(btn);
        }
    }

    private void playCard(Card card) {
        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);
        human.getHand().remove(card);
        game.getTable().placeCard(card);
        updateTable();
        showPlayerHand();
        runMachineTurns();
    }

    private void runMachineTurns() {
        new Thread(() -> {
            while (!game.isGameOver()) {
                game.nextTurn();
                IPlayer current = game.getPlayers().get(game.getPlayers().indexOf(game.getPlayers().get(0)) + 1);
                if (current instanceof MachinePlayer) {
                    MachinePlayer cpu = (MachinePlayer) current;
                    Card move = cpu.playCard(game.getTable().getTableSum());
                    if (move != null) {
                        Platform.runLater(() -> {
                            game.getTable().placeCard(move);
                            updateTable();
                        });
                    }
                }
                try { Thread.sleep(1500); } catch (InterruptedException ignored) {}
                if (game.isGameOver()) break;
            }

            Platform.runLater(() -> {
                lblLastCard.setText("Winner: " + game.getWinner().getName());
                handContainer.getChildren().clear();
            });
        }).start();
    }
}