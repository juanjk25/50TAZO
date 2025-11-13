package org.example.mini.model.deck;

import org.example.mini.model.card.Card;

import java.util.List;

/**
 * Defines the basic operations supported by a deck of cards.
 * <p>
 * Implementations are responsible for how cards are stored, shuffled and
 * drawn, but must provide at least the operations declared in this interface.
 * </p>
 */
public interface IDeck {

    /**
     * Draws (removes and returns) the next card from the deck.
     *
     * @return the next {@link Card} in the deck
     * @throws Exception if a card cannot be drawn (for example, if the deck is empty
     *                   or in an invalid state)
     */
    Card drawCard() throws Exception;

    /**
     * Adds the given list of cards to the deck.
     * <p>
     * The way in which the cards are inserted (e.g. on top, at the bottom,
     * or shuffled in) is left to the concrete implementation.
     * </p>
     *
     * @param newCards the cards to add to the deck; must not be {@code null}
     */
    void addCards(List<Card> newCards);

    /**
     * Indicates whether the deck currently contains no cards.
     *
     * @return {@code true} if the deck has no cards, {@code false} otherwise
     */
    boolean isEmpty();
}
