package org.example.mini.model.deck;

import org.example.mini.model.card.Card;
import org.example.mini.model.exceptions.EmptyDeckException;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shuffled deck of 52 cards.
 */
public class Deck {
    private final List<Card> cards;

    /**
     * CONSTRUCTOR - Creates and shuffles a standard 52-card deck
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
     * Shuffles the deck of cards.
     */
    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws a card from the top of the deck.
     * @return The drawn card
     * @throws EmptyDeckException if the deck is empty
     */
    public Card draw() {
        if (isEmpty()) {
            throw new EmptyDeckException("Cannot draw from empty deck");
        }
        return cards.remove(0);
    }

    /**
     * Returns the number of cards remaining in the deck.
     */
    public int size() {
        return cards.size();
    }

    /**
     * Returns a card to the bottom of the deck.
     */
    public void returnCard(Card card) {
        cards.add(card);
    }

    /**
     * Adds multiple cards to the deck and shuffles them.
     * Used when the deck is empty and we need to recycle cards from the table.
     * @param tableCards List of cards from the table to reshuffle into the deck
     */
    public void reshuffleFromTable(List<Card> tableCards) {
        if (tableCards != null && !tableCards.isEmpty()) {
            System.out.println("Reshuffling " + tableCards.size() + " cards from table into deck");
            cards.addAll(tableCards);
            shuffle();
        }
    }

    /**
     * Checks if the deck is empty.
     * @return true if the deck has no cards left, false otherwise
     */
    public boolean isEmpty() {
        return cards.isEmpty();
    }

    /**
     * Gets all cards currently in the deck.
     * @return List of cards in the deck
     */
    public List<Card> getCards() {
        return cards;
    }
}