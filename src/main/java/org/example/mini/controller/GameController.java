package org.example.mini.controller;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

import org.example.mini.model.Table;
import org.example.mini.model.card.Card;
import org.example.mini.model.game.Game;
import org.example.mini.model.player.*;
import org.example.mini.util.TableMonitorThread;
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

    private TableMonitorThread tableMonitor;
    private static final int REFRESH_INTERVAL = 100; // Actualizar cada 100ms

    @Override
    public void init(Game game) {
        this.game = game;

        // Iniciar el monitor de la mesa
        startTableMonitoring();

        updateTable();
        showPlayerHand();
        lblLastCard.setText("Your turn!");
    }

    /**
     * Inicia el hilo que monitorea y actualiza el estado de la mesa
     */
    private void startTableMonitoring() {
        tableMonitor = new TableMonitorThread(this::forceTableUpdate, REFRESH_INTERVAL);
        tableMonitor.start();
        System.out.println("Table monitor started");
    }

    /**
     * Fuerza la actualización completa de la mesa
     */
    private void forceTableUpdate() {
        if (game != null && game.getTable() != null) {
            // Actualizar la suma de la mesa
            int currentSum = game.getTable().getTableSum();
            Platform.runLater(() -> {
                lblTableSum.setText("Table sum: " + currentSum);

                // También actualizar la última carta
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
     * Versión mejorada de updateTable que también usa el monitor
     */

    // Modifica el método playCard para forzar actualización inmediata
    private void playCard(Card card) {
        HumanPlayer human = (HumanPlayer) game.getPlayers().get(0);

        // Verificar si la carta se puede jugar
        if (!card.canBePlayed(game.getTable().getTableSum(), true)) {
            Platform.runLater(() -> lblLastCard.setText("Cannot play - would exceed 50!"));
            return;
        }

        // Jugar carta
        human.getHand().remove(card);
        boolean validMove = game.getTable().placeCard(card, true);

        if (!validMove) {
            Platform.runLater(() -> lblLastCard.setText("Invalid move!"));
            human.addCard(card);
            return;
        }

        // ACTUALIZACIÓN INMEDIATA después de jugar
        forceTableUpdate();

        // Tomar nueva carta del mazo
        Card newCard = game.drawCardWithRecycle();
        if (newCard != null) {
            human.addCard(newCard);
        }

        // Actualizar UI
        showPlayerHand();
        Platform.runLater(() -> lblLastCard.setText("You played: " + card.getRank() + " of " + card.getSuit()));

        // Verificar eliminación y continuar
        game.checkAndEliminatePlayers();
        if (!game.isGameOver()) {
            Platform.runLater(() -> lblLastCard.setText("CPU's turn..."));
            runMachineTurns();
        } else {
            stopTableMonitoring();
            Platform.runLater(() -> lblLastCard.setText("Game Over - You Win!"));
        }
    }

    // Modifica runMachineTurns para actualizaciones forzadas
    private void runMachineTurns() {
        Thread cpuThread = new Thread(() -> {
            while (!game.isGameOver()) {
                game.nextTurn();
                IPlayer current = game.getCurrentPlayer();

                if (current instanceof MachinePlayer) {
                    MachinePlayer cpu = (MachinePlayer) current;
                    Card move = cpu.playCard(game.getTable().getTableSum());

                    if (move != null) {
                        // Jugar la carta
                        boolean validMove = game.getTable().placeCard(move, false);

                        // ACTUALIZACIÓN INMEDIATA
                        forceTableUpdate();

                        Platform.runLater(() -> {
                            if (validMove) {
                                lblLastCard.setText(cpu.getName() + " played: " + move.getRank() + " of " + move.getSuit());
                            }
                        });

                        try { Thread.sleep(800); } catch (InterruptedException ignored) {}

                        // CPU toma nueva carta
                        Card newCard = game.drawCardWithRecycle();
                        if (newCard != null) {
                            cpu.addCard(newCard);
                        }

                        game.checkAndEliminatePlayers();
                        try { Thread.sleep(1200); } catch (InterruptedException ignored) {}
                    } else {
                        game.checkAndEliminatePlayers();
                    }
                } else {
                    Platform.runLater(() -> lblLastCard.setText("Your turn!"));
                    break;
                }
            }

            if (game.isGameOver()) {
                stopTableMonitoring();
                Platform.runLater(() -> {
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
     * Detiene el monitor cuando el juego termina
     */
    private void stopTableMonitoring() {
        if (tableMonitor != null) {
            tableMonitor.stopMonitoring();
            System.out.println("Table monitor stopped");
        }
    }

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



}