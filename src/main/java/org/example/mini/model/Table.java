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

    public boolean placeCard(Card card) {
        int newValue = tableSum + getCardValue(card);

        // 🔹 Validación de regla principal
        if (newValue > 50) {
            System.out.println(" Move not allowed: total would exceed 50 (" + newValue + ")");
            return false; // No se puede colocar la carta
        }

        // Jugada válida
        cards.add(card);
        tableSum = newValue;
        return true;
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

    public void reset() {
        cards.clear();
        tableSum = 0;
    }
}