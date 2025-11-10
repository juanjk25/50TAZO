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
        this.game = new Game(cpuCount);
        this.game.start(); // Inicializa cartas
        init(game); // Actualiza la UI
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

    // En GameController, método playCard
    private void playCard(Card card) {
        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);

        // Verificar si la carta se puede jugar
        if (!card.canBePlayed(game.getTable().getTableSum(), true)) {
            lblStatus.setText("Cannot play this card - would exceed 50!");
            return;
        }

        // Jugar carta
        human.getHand().remove(card);
        boolean validMove = game.getTable().placeCard(card, true); // true = es humano

        if (!validMove) {
            lblStatus.setText("Invalid move!");
            human.addCard(card); // Devolver la carta a la mano
            return;
        }

        // Tomar nueva carta del mazo
        Card newCard = game.drawCardWithRecycle();
        if (newCard != null) {
            human.addCard(newCard);
        }

        // Actualizar UI
        updateTable();
        showPlayerHand();
        lblStatus.setText("You played: " + card);

        // Verificar eliminación y continuar
        game.checkAndEliminatePlayers();
        if (!game.isGameOver()) {
            lblTurn.setText("CPU's turn...");
            runMachineTurns();
        }
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
                        // Jugar la carta y actualizar TODO en la UI
                        Platform.runLater(() -> {
                            lblTurn.setText("Turn: " + cpu.getName());

                            // Jugar la carta en la mesa
                            boolean validMove = game.getTable().placeCard(move, false); // false = es CPU

                            if (validMove) {
                                updateTable(); // Actualizar suma y última carta
                                System.out.println("CPU played: " + move + ", New sum: " + game.getTable().getTableSum());
                            } else {
                                System.out.println("CPU move invalid: " + move);
                            }
                        });

                        // Pequeña pausa para ver la carta jugada
                        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

                        // CPU toma nueva carta del mazo
                        Card newCard = game.drawCardWithRecycle();
                        if (newCard != null) {
                            cpu.addCard(newCard);
                        }

                        // Verificar eliminación
                        game.checkAndEliminatePlayers();

                        // Pausa entre turnos de CPU
                        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
                    } else {
                        // Si la CPU no puede jugar, se elimina
                        game.checkAndEliminatePlayers();
                    }
                } else {
                    // Si vuelve al jugador humano, se pausa el hilo de CPU
                    break;
                }
            }

            // Mostrar fin del juego
            if (game.isGameOver()) {
                Platform.runLater(() -> {
                    IPlayer winner = game.getWinner();
                    lblLastCard.setText("Winner: " + (winner != null ? winner.getName() : "None"));
                    lblTurn.setText("Game Over");
                    handContainer.getChildren().clear();
                    lblTableSum.setText("Final Sum: " + game.getTable().getTableSum());
                });
            }
        });

        cpuThread.setDaemon(true);
        cpuThread.start();
    }

}