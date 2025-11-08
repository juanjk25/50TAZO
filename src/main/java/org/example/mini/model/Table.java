package org.example.mini.model;

import org.example.mini.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents the current state of the table (sum and cards played).
 */
public class Table {
    private final List<Card> playedCards = new ArrayList<>();
    private int tableSum = 0;

    public void placeCard(Card card) {
        tableSum += card.getNumericValue(tableSum);
        playedCards.add(card);
    }

    public int getTableSum() { return tableSum; }

    public Card getLastCard() {
        return playedCards.isEmpty() ? null : playedCards.get(playedCards.size() - 1);
    }

    public List<Card> collectCards() {
        List<Card> temp = new ArrayList<>(playedCards);
        Card last = getLastCard();
        temp.remove(last);
        playedCards.clear();
        if (last != null) playedCards.add(last);
        return temp;
    }
}