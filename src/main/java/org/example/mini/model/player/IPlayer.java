package org.example.mini.model.player;

import org.example.mini.model.card.Card;
import java.util.List;


/**
 * Defines the contract for any player in the game.
 */
public interface IPlayer {
    String getName();
    List<Card> getHand();

    void addCard(Card card);   // recibe una carta
    Card playCard(int tableSum); // juega una carta (puede depender del estado de la mesa)
}