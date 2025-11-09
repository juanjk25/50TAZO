package org.example.mini.model.deck;

import org.example.mini.model.card.Card;

import java.util.Collections;
import java.util.List;

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

    public boolean isEmpty() {
        return cards.isEmpty();
    }

    public Card draw() {
        return isEmpty() ? null : cards.remove(0);
    }

    public int size() {
        return cards.size();
    }
}