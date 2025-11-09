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
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;



public class GameController implements IGameController {

    @FXML private Label lblTableSum;
    @FXML private Label lblLastCard;
    @FXML private HBox handContainer;
    @FXML private ImageView imgLastCard;
    @FXML private Label lblTurn;


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
        // Este metodo se llama automáticamente al cargar el FXML
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
        if (last != null) {
            lblLastCard.setText("Last card: " + last.toString());
            Image image = new Image(getClass().getResourceAsStream(last.getImagePath()));
            imgLastCard.setImage(image);
        } else {
            lblLastCard.setText("Last card: None");
            imgLastCard.setImage(null);
        }
    }


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

    private void playCard(Card card) {
        // 🔹 Juega la carta del jugador humano
        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);
        human.getHand().remove(card);
        game.getTable().placeCard(card);

        // 🔹 Actualiza la interfaz inmediatamente
        updateTable();
        showPlayerHand();
        lblTurn.setText("Turn: CPU Players...");

        // 🔹 Lanza los turnos automáticos de las CPUs en un hilo separado
        runMachineTurns();
    }

    private void runMachineTurns() {
        Thread cpuThread = new Thread(() -> {
            while (!game.isGameOver()) {
                game.nextTurn();
                IPlayer current = game.getCurrentPlayer();

                if (current instanceof MachinePlayer) {
                    MachinePlayer cpu = (MachinePlayer) current;
                    Card move = cpu.playCard(game.getTable().getTableSum());

                    if (move != null) {
                        Platform.runLater(() -> {
                            lblTurn.setText("Turn: " + cpu.getName());
                            game.getTable().placeCard(move);
                            updateTable();
                        });
                    }

                    try { Thread.sleep(1000); } catch (InterruptedException ignored) {}
                } else {
                    // Si vuelve al jugador humano, se pausa el hilo de CPU
                    break;
                }
            }

            if (game.isGameOver()) {
                Platform.runLater(() -> {
                    lblLastCard.setText("Winner: " + game.getWinner().getName());
                    lblTurn.setText("Game Over");
                    handContainer.getChildren().clear();
                });
            }
        });

        cpuThread.setDaemon(true); // se cierra junto con la app
        cpuThread.start();
    }


}