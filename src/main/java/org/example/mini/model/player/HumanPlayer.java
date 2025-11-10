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
    public boolean isHuman() {
        return true;
    }

    @Override
    public Card playCard(int tableSum) {
        // El jugador humano elige manualmente con el mouse
        // La UI manejará la selección real de la carta
        return null;
    }

    /** 🔹 Nuevo método requerido por la interfaz IPlayer */
    @Override
    public void removeCard(Card card) {
        hand.remove(card);
    }
}
