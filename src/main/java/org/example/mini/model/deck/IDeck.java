package org.example.mini.model.deck;

import org.example.mini.model.card.Card;

import java.util.List;

/**
 * Interface defining the behavior of a deck.
 */
public interface IDeck {
    Card drawCard() throws Exception;
    void addCards(List<Card> newCards);
    boolean isEmpty();
}
