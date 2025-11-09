package org.example.mini.model;

import org.example.mini.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the current state of the table (sum and cards played).
 */
public class Table {
    private final List<Card> cards = new ArrayList<>();
    private int tableSum = 0;

    public void placeCard(Card card) {
        cards.add(card);
        tableSum += getCardValue(card);
    }

    public Card getLastCard() {
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
    }

    public int getTableSum() {
        return tableSum;
    }

    private int getCardValue(Card card) {
        switch (card.getValue()) {
            case "A": return 1;
            case "J": return 11;
            case "Q": return 12;
            case "K": return 13;
            default: return Integer.parseInt(card.getValue());
        }
    }
}
