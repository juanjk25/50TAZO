package org.example.mini.model.player;

import org.example.mini.model.card.Card;

/**
 * Defines the contract for any player in the game.
 */
public interface IPlayer {
    String getName();
    boolean isEliminated();
    void eliminate();
    void receiveCard(Card card);
    Card playCard(int tableSum);
}