package org.example.mini.model.game;

import org.example.mini.model.Table;
import org.example.mini.model.card.Card;
import org.example.mini.model.deck.Deck;
import org.example.mini.model.player.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;

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
        this.deck = new Deck();              // 👈 inicializa el mazo
        this.players = new ArrayList<>();

        players.add(new HumanPlayer("You"));
        for (int i = 1; i <= cpuCount; i++) {
            players.add(new MachinePlayer("CPU " + i));
        }

        this.currentPlayerIndex = 0;
        this.gameOver = false;
    }

    public void start() {
        System.out.println("Game started with " + players.size() + " players.");

        // ✅ CORREGIDO: Verificar que deck.draw() no devuelva null
        for (IPlayer p : players) {
            for (int i = 0; i < 5; i++) {
                Card card = deck.draw();
                if (card != null) {
                    p.addCard(card);
                } else {
                    System.out.println("Warning: No more cards in deck during initial deal!");
                    break;
                }
            }
        }

        // ✅ CORREGIDO: Verificar que la carta inicial no sea null
        Card initialCard = deck.draw();
        if (initialCard != null) {
            table.placeCard(initialCard);
        } else {
            System.out.println("Error: No cards available for initial table card!");
        }
    }

    /** 🔹 Devuelve el jugador actual */
    public IPlayer getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /** 🔹 Avanza al siguiente jugador */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /** 🔹 Devuelve todos los jugadores */
    public List<IPlayer> getPlayers() {
        return players;
    }

    /** 🔹 Devuelve la mesa */
    public Table getTable() {
        return table;
    }

    /** 🔹 Devuelve el mazo */
    public Deck getDeck() {
        return deck;
    }

    public void checkAndEliminatePlayers() {
        Iterator<IPlayer> iterator = players.iterator();
        while (iterator.hasNext()) {
            IPlayer player = iterator.next();
            if (player.isActive() && !player.hasPlayableCards(table.getTableSum())) {
                player.setActive(false);
                System.out.println(player.getName() + " ha sido eliminado!");

                // Enviar cartas al mazo
                for (Card card : player.getHand()) {
                    deck.returnCard(card);
                }
                player.getHand().clear();
            }
        }

        // Verificar si el juego terminó
        long activePlayers = players.stream().filter(IPlayer::isActive).count();
        if (activePlayers <= 1) {
            gameOver = true;
            System.out.println("¡Juego terminado! Solo queda " + activePlayers + " jugador activo");
        }
    }

    /**
     * Ensures the deck has cards by recycling from table if necessary
     */
    private void ensureDeckHasCards() {
        if (deck.isEmpty()) {
            System.out.println("Deck is empty, recycling cards from table...");

            // Tomar todas las cartas de la mesa excepto la última jugada
            List<Card> recycledCards = table.removeAllButLastCard();

            if (!recycledCards.isEmpty()) {
                deck.reshuffleFromTable(recycledCards);
                System.out.println("Deck now has " + deck.size() + " cards after recycling");
            } else {
                System.out.println("No cards available to recycle!");
            }
        }
    }

    /**
     * Draws a card from deck, recycling from table if necessary
     */
    public Card drawCardWithRecycle() {
        ensureDeckHasCards();
        return deck.draw();
    }

    /** 🔹 Determina si el juego terminó */
    public boolean isGameOver() {
        return gameOver;
    }

    /** 🔹 Simula una jugada */
    public void playTurn(Object card) {
        // lógica para ejecutar un turno del jugador actual
        nextTurn();
    }

    /** 🔹 Marca el juego como terminado */
    public void endGame() {
        this.gameOver = true;
    }

    /** 🔹 Devuelve al ganador (por ahora simulado) */
    public IPlayer getWinner() {
        // puedes reemplazarlo por lógica real
        return players.get(0);
    }
}