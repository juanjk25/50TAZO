package org.example.mini.model.player;

import org.example.mini.model.card.Card;

/**
 * Represents a human player; actual move is controlled by the UI.
 */
public class HumanPlayer extends Player {
    public HumanPlayer(String name) {
        super(name);
    }

    @Override
    public Card playCard(int tableSum) {
        // Human plays via GUI, not automatic
        return null;
    }
}