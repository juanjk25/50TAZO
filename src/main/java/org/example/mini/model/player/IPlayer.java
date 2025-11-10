package org.example.mini.model.player;

import org.example.mini.model.card.Card;
import java.util.List;

/**
 * Defines the contract for any player in the game.
 */
public interface IPlayer {
    String getName();
    List<Card> getHand();
    boolean isHuman();
    boolean isActive();
    void setActive(boolean active);
    void addCard(Card card);
    Card playCard(int tableSum);
    boolean hasPlayableCards(int tableSum);
}