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
public class Game implements IGame {
    private final Table table;
    private final Deck deck;
    private final List<IPlayer> players;
    private int turnIndex = 0;

    public Game(int machineCount) {
        this.table = new Table();
        this.deck = new Deck();
        this.players = new ArrayList<>();

        players.add(new HumanPlayer("Human"));
        for (int i = 1; i <= machineCount; i++)
            players.add(new MachinePlayer("CPU " + i));

        dealInitialCards();

        try {
            Card firstCard = deck.drawCard();
            table.placeCard(firstCard);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void dealInitialCards() {
        try {
            for (IPlayer p : players)
                for (int i = 0; i < 4; i++)
                    p.receiveCard(deck.drawCard());
        } catch (Exception e) {
            System.err.println("Error dealing cards: " + e.getMessage());
        }
    }

    @Override
    public Table getTable() { return table; }

    @Override
    public List<IPlayer> getPlayers() { return players; }

    @Override
    public void nextTurn() {
        do {
            turnIndex = (turnIndex + 1) % players.size();
        } while (players.get(turnIndex).isEliminated());
    }

    @Override
    public boolean isGameOver() {
        return players.stream().filter(p -> !p.isEliminated()).count() == 1;
    }

    @Override
    public IPlayer getWinner() {
        return players.stream().filter(p -> !p.isEliminated()).findFirst().orElse(null);
    }
}