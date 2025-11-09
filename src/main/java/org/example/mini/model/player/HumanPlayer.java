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
        // El jugador humano elige manualmente con el mouse, así que no hace nada aquí.
        return null;
    }
}