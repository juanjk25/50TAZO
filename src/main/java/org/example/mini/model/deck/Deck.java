package org.example.mini.model.deck;

import org.example.mini.model.card.Card;

import java.util.Collections;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a shuffled deck of 52 cards.
 */
public class Deck {
    private final List<Card> cards;

    public Deck() {
        cards = new ArrayList<>();
        String[] suits = {"hearts", "spades", "clubs", "diamonds"};
        String[] values = {"A", "2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K"};

        for (String s : suits) {
            for (String v : values) {
                cards.add(new Card(v, s));
            }
        }

        shuffle();
    }

    public void shuffle() {
        Collections.shuffle(cards);
    }

    /**
     * Draws a card from the top of the deck.
     * @return the drawn card, or null if deck is empty
     */
    public Card draw() {
        return isEmpty() ? null : cards.remove(0);
    }

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

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public List<Card> getCards() {
        return cards;
    }
}