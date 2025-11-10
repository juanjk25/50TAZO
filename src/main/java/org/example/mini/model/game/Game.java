package org.example.mini.model.game;

import org.example.mini.model.Table;
import org.example.mini.model.card.Card;
import org.example.mini.model.deck.Deck;
import org.example.mini.model.player.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Main game controller handling turns, table state, and logic.
 */
public class Game {

    private final Table table;
    private final Deck deck;
    private final List<IPlayer> players;
    private int currentPlayerIndex;
    private boolean gameOver;

    public Game(int cpuCount) {
        this.table = new Table();
        this.deck = new Deck();
        this.players = new ArrayList<>();

        // Crear jugadores
        players.add(new HumanPlayer("You"));
        for (int i = 1; i <= cpuCount; i++) {
            players.add(new MachinePlayer("CPU " + i));
        }

        this.currentPlayerIndex = 0;
        this.gameOver = false;
    }

    /** 🔹 Inicia el juego: reparte cartas y coloca la inicial */
    public void start() {
        System.out.println("Game started with " + players.size() + " players.");

        // Repartir 5 cartas por jugador
        for (IPlayer p : players) {
            for (int i = 0; i < 4; i++) {
                Card card = deck.draw();
                if (card != null) {
                    p.addCard(card);
                }
            }
        }

        // Colocar carta inicial sobre la mesa
        Card initialCard = deck.draw();
        if (initialCard != null) {
            table.placeCard(initialCard, true);
            System.out.println("Carta inicial: " + initialCard +
                    " → suma inicial = " + table.getTableSum());
        } else {
            System.out.println("Error: No cards available for initial table card!");
        }
    }


    /** 🔹 Jugada principal de un jugador */
    public void playCard(IPlayer player, Card card) {
        if (!player.getHand().contains(card)) {
            System.out.println("El jugador " + player.getName() + " no tiene esa carta.");
            return;
        }

        boolean isHuman = player.isHuman();

        // 🔹 Delega el conteo y validación a Table
        boolean played = table.placeCard(card, isHuman);

        if (!played) {
            System.out.println("❌ Jugada no válida (" + card + "). Suma actual: " + table.getTableSum());
            return;
        }

        // 🔹 Si fue válida, actualizar estado del jugador
        player.removeCard(card);
        System.out.println("✅ " + player.getName() + " jugó " + card +
                " → suma total: " + table.getTableSum());

        // 🔹 Robar una nueva carta si hay
        Card newCard = drawCardWithRecycle();
        if (newCard != null) {
            player.addCard(newCard);
        }

        // 🔹 Revisar si algún jugador queda sin jugadas válidas
        checkAndEliminatePlayers();

        // 🔹 Pasar turno
        nextTurn();
    }

    /** 🔹 Devuelve el jugador actual */
    public IPlayer getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /** 🔹 Avanza al siguiente jugador */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /** 🔹 Verifica eliminaciones y fin del juego */
    public void checkAndEliminatePlayers() {
        Iterator<IPlayer> iterator = players.iterator();
        while (iterator.hasNext()) {
            IPlayer player = iterator.next();
            if (player.isActive() && !player.hasPlayableCards(table.getTableSum())) {
                player.setActive(false);
                System.out.println(player.getName() + " ha sido eliminado!");

                for (Card card : player.getHand()) {
                    deck.returnCard(card);
                }
                player.getHand().clear();
            }
        }

        long activePlayers = players.stream().filter(IPlayer::isActive).count();
        if (activePlayers <= 1) {
            gameOver = true;
            System.out.println("🏁 ¡Juego terminado! Solo queda " + activePlayers + " jugador activo");
        }
    }

    /** 🔹 Asegura que el mazo tenga cartas */
    private void ensureDeckHasCards() {
        if (deck.isEmpty()) {
            System.out.println("Deck vacío — reciclando cartas de la mesa...");
            List<Card> recycledCards = table.removeAllButLastCard();
            if (!recycledCards.isEmpty()) {
                deck.reshuffleFromTable(recycledCards);
            }
        }
    }

    /** 🔹 Roba carta del mazo, reciclando si es necesario */
    public Card drawCardWithRecycle() {
        ensureDeckHasCards();
        return deck.draw();
    }

    /** 🔹 Devuelve mesa y mazo */
    public Table getTable() { return table; }
    public Deck getDeck() { return deck; }
    public List<IPlayer> getPlayers() { return players; }

    public boolean isGameOver() { return gameOver; }

    /** 🔹 Termina el juego manualmente */
    public void endGame() { this.gameOver = true; }

    /** 🔹 Obtiene el ganador provisional */
    public IPlayer getWinner() {
        return players.stream().filter(IPlayer::isActive).findFirst().orElse(null);
    }
}
