package org.example.mini.model.deck;

import org.example.mini.model.card.Card;
import org.example.mini.model.exceptions.EmptyDeckException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a standard shuffled deck of 52 playing cards.
 * <p>
 * The deck is initialized with all combinations of 4 suits
 * ({@code hearts}, {@code spades}, {@code clubs}, {@code diamonds})
 * and 13 ranks ({@code A, 2–10, J, Q, K}), and is shuffled on creation.
 * Cards can be drawn from the top, returned to the bottom, and
 * additional cards can be recycled from the table and reshuffled into
 * the deck.
 * </p>
 */
public class Deck {

    /**
     * Internal list holding the cards in draw order.
     * The card at index {@code 0} is considered the top of the deck.
     */
    private final List<Card> cards;

    /**
     * Constructs a new {@code Deck} with 52 cards and shuffles it.
     * <p>
     * The deck is built from the four suits
     * ({@code "hearts", "spades", "clubs", "diamonds"}) and the ranks
     * ({@code "A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"}).
     * After creation, the cards are shuffled using {@link #shuffle()}.
     * </p>
     */
    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"hearts", "spades", "clubs", "diamonds"};
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (String v : values) {
            for (String s : suits) {
                cards.add(new Card(s, v));
            }
        }

        shuffle();
    }

    /**
     * Randomly shuffles the cards currently in the deck.
     * <p>
     * Uses {@link Collections#shuffle(List)} to reorder the internal card list.
     * </p>
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws a card from the top of the deck.
     * <p>
     * The drawn card is removed from the internal list and returned to the caller.
     * If the deck is empty, an {@link EmptyDeckException} is thrown.
     * </p>
     *
     * @return the top {@link Card} from the deck
     * @throws EmptyDeckException if the deck has no cards left to draw
     */
    public Card draw() {
        if (isEmpty()) {
            throw new EmptyDeckException("Cannot draw from empty deck");
        }
        return cards.remove(0);
    }

    /**
     * Returns the number of cards currently remaining in the deck.
     *
     * @return the size of the deck
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns a card to the bottom of the deck.
     * <p>
     * The card is appended to the end of the internal list and will be drawn
     * after all cards that are currently in the deck.
     * </p>
     *
     * @param card the {@link Card} to return to the deck; if {@code null},
     *             the method has no effect
     */
    public void returnCard(Card card) {
        cards.add(card);
    }

    /**
     * Adds multiple cards back into the deck and shuffles them together with
     * the existing cards.
     * <p>
     * This method is typically used when the deck becomes empty and cards
     * need to be recycled from the table. All provided cards are appended
     * to the internal list and the deck is then shuffled.
     * </p>
     *
     * @param tableCards the list of cards to add and reshuffle into the deck;
     *                   if {@code null} or empty, nothing is done
     */
    public void reshuffleFromTable(List<Card> tableCards) {
        if (tableCards != null && !tableCards.isEmpty()) {
            System.out.println("Reshuffling " + tableCards.size() + " cards from table into deck");
            cards.addAll(tableCards);
            shuffle();
        }
    }

    /**
     * Checks whether the deck currently has no cards.
     *
     * @return {@code true} if the deck is empty, {@code false} otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}