package org.example.mini.model.game;

import org.example.mini.model.Table;
import org.example.mini.model.card.Card;
import org.example.mini.model.deck.Deck;
import org.example.mini.model.player.*;



import java.util.ArrayList;
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

        // reparte 5 cartas a cada jugador
        for (IPlayer p : players) {
            for (int i = 0; i < 5; i++) {
                p.addCard(deck.draw());
            }
        }

        // coloca una carta inicial en la mesa
        table.placeCard(deck.draw());
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
