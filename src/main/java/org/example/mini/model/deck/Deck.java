package org.example.mini.model.deck;

import org.example.mini.model.card.Card;

import java.util.Collections;
import java.util.List;
import java.util.Stack;

/**
 * Represents a shuffled deck of 52 cards.
 */
public class Deck implements IDeck {
    private final Stack<Card> cards = new Stack<>();

    public Deck() {
        String[] values = {"2","3","4","5","6","7","8","9","10","J","Q","K","A"};
        String[] suits = {"Hearts","Spades","Clubs","Diamonds"};
        for (String suit : suits)
            for (String value : values)
                cards.push(new Card(value, suit));
        Collections.shuffle(cards);
    }

    @Override
    public Card drawCard() throws Exception {
        if (cards.isEmpty())
            throw new Exception("Deck is empty");
        return cards.pop();
    }

    @Override
    public void addCards(List<Card> newCards) {
        Collections.shuffle(newCards);
        cards.addAll(newCards);
    }

    @Override
    public boolean isEmpty() {
        return cards.isEmpty();
    }
}