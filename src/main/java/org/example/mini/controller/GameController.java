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

// Para VBox y HBox
import javafx.scene.layout.*;
import javafx.scene.layout.VBox;
import javafx.geometry.Pos;





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

    @FXML private HBox playersContainer; // Agrega esta línea


    /**
     * FUNCIONES PRUEBA PARA MOSTRAR JUGADORES
     * @param game
     */
    @Override
    public void init(Game game) {
        this.game = game;

        // Iniciar el monitor de la mesa
        startTableMonitoring();

        // Mostrar jugadores
        showPlayers();

        updateTable();
        showPlayerHand();
        updateTurnDisplay(); // Nuevo método para mostrar turno actual
    }

    /**
     * Muestra todos los jugadores en la interfaz
     */
    private void showPlayers() {
        playersContainer.getChildren().clear();

        for (IPlayer player : game.getPlayers()) {
            VBox playerBox = createPlayerBox(player);
            playersContainer.getChildren().add(playerBox);
        }

        updateTurnDisplay(); // Actualizar quién está activo
    }


    private VBox createPlayerBox(IPlayer player) {
        VBox box = new VBox(5);
        box.setAlignment(Pos.CENTER);
        box.setStyle("-fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-radius: 5; -fx-background-radius: 5;");
        box.setId("player-" + player.getName().replace(" ", ""));

        Label nameLabel = new Label(player.getName());
        nameLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 14px;");

        Label cardsLabel = new Label("Cards: " + player.getHand().size());
        cardsLabel.setStyle("-fx-font-size: 12px;");

        Label statusLabel = new Label(player.isActive() ? "ACTIVE" : "Waiting");
        statusLabel.setStyle("-fx-font-size: 11px;");

        box.getChildren().addAll(nameLabel, cardsLabel, statusLabel);

        return box;
    }

    /**
     * Actualiza la visualización de qué jugador está activo
     */
    private void updateTurnDisplay() {
        if (playersContainer == null || game == null) {
            System.out.println("DEBUG: playersContainer o game es null");
            return;
        }

        IPlayer currentPlayer = game.getCurrentPlayer();
        System.out.println("DEBUG: Turno actual: " + (currentPlayer != null ? currentPlayer.getName() : "null"));

        // Resetear todos los jugadores a estilo normal
        for (javafx.scene.Node node : playersContainer.getChildren()) {
            if (node instanceof VBox) {
                VBox playerBox = (VBox) node;
                Label nameLabel = (Label) playerBox.getChildren().get(0);
                String playerName = nameLabel.getText();

                // Buscar el jugador real
                IPlayer player = findPlayerByName(playerName);
                if (player == null) continue;

                // Actualizar número de cartas
                Label cardsLabel = (Label) playerBox.getChildren().get(1);
                cardsLabel.setText("Cards: " + player.getHand().size());

                if (playerName.equals(currentPlayer.getName())) {
                    // Jugador activo - resaltar
                    playerBox.setStyle("-fx-padding: 10; -fx-border-color: #e74c3c; -fx-border-width: 2; " +
                            "-fx-border-radius: 5; -fx-background-color: #fff5f5; -fx-background-radius: 5;");

                    Label statusLabel = (Label) playerBox.getChildren().get(2);
                    statusLabel.setText("▶ PLAYING");
                    statusLabel.setStyle("-fx-font-size: 11px; -fx-font-weight: bold; -fx-text-fill: #e74c3c;");

                } else {
                    // Jugador inactivo
                    playerBox.setStyle("-fx-padding: 10; -fx-border-color: #bdc3c7; -fx-border-radius: 5; " +
                            "-fx-background-color: #f8f9fa; -fx-background-radius: 5;");

                    Label statusLabel = (Label) playerBox.getChildren().get(2);
                    if (!player.isActive()) {
                        statusLabel.setText("ELIMINATED");
                        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
                    } else {
                        statusLabel.setText("Waiting");
                        statusLabel.setStyle("-fx-font-size: 11px; -fx-text-fill: #7f8c8d;");
                    }
                }
            }
        }
    }


    /**
     * Encuentra un jugador por nombre
     */
    private IPlayer findPlayerByName(String name) {
        for (IPlayer player : game.getPlayers()) {
            if (player.getName().equals(name)) {
                return player;
            }
        }
        return null;
    }



    /**
     * FIN FUNCIONES PRUEBA PARA MOSTRAR JUGADORES
     */

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

        // ACTUALIZAR TURNO INMEDIATAMENTE
        Platform.runLater(() -> {
            updateTurnDisplay();
            lblLastCard.setText("You played: " + card.getRank() + " of " + card.getSuit());
        });

        // Verificar eliminación y continuar
        game.checkAndEliminatePlayers();
        if (!game.isGameOver()) {
            Platform.runLater(() -> {
                updateTurnDisplay(); // ← ACTUALIZAR ANTES DE CPUs
                lblLastCard.setText("CPU's turn...");
            });
            runMachineTurns();
        } else {
            stopTableMonitoring();
            Platform.runLater(() -> {
                updateTurnDisplay(); // ← ACTUALIZAR AL FINAL
                lblLastCard.setText("Game Over - You Win!");
            });
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

                    // ACTUALIZAR DISPLAY INMEDIATAMENTE AL CAMBIAR TURNO
                    Platform.runLater(this::updateTurnDisplay);

                    // DELAY ANTES DE JUGAR: 3-4 segundos
                    int delayBeforePlay = 3000 + (int)(Math.random() * 1000);
                    try {
                        Thread.sleep(delayBeforePlay);
                    } catch (InterruptedException ignored) {}

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
                            updateTurnDisplay(); // ← ACTUALIZAR DESPUÉS DE JUGAR
                        });

                        // DELAY DESPUÉS DE JUGAR: 1-2 segundos
                        int delayAfterPlay = 1000 + (int)(Math.random() * 1000);
                        try {
                            Thread.sleep(delayAfterPlay);
                        } catch (InterruptedException ignored) {}

                        // CPU toma nueva carta
                        Card newCard = game.drawCardWithRecycle();
                        if (newCard != null) {
                            cpu.addCard(newCard);
                        }

                        game.checkAndEliminatePlayers();

                        // ACTUALIZAR POR SI ALGUIEN FUE ELIMINADO
                        Platform.runLater(this::updateTurnDisplay);

                        // DELAY ENTRE TURNOS: 1-2 segundos adicionales
                        int delayBetweenTurns = 1000 + (int)(Math.random() * 1000);
                        try {
                            Thread.sleep(delayBetweenTurns);
                        } catch (InterruptedException ignored) {}

                    } else {
                        game.checkAndEliminatePlayers();
                        Platform.runLater(this::updateTurnDisplay); // ← ACTUALIZAR SI NO PUEDE JUGAR
                    }
                } else {
                    // Vuelve al jugador humano
                    Platform.runLater(() -> {
                        updateTurnDisplay(); // ← ACTUALIZAR AL VOLVER AL HUMANO
                        lblLastCard.setText("Your turn!");
                    });
                    break;
                }
            }

            if (game.isGameOver()) {
                stopTableMonitoring();
                Platform.runLater(() -> {
                    updateTurnDisplay(); // ← ACTUALIZAR AL TERMINAR
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