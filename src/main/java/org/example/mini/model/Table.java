package org.example.mini.model;

import org.example.mini.model.card.Card;
import org.example.mini.model.deck.Deck;

import java.util.ArrayList;
import java.util.List;



/**
 * Represents the state of the table during a game.
 * <p>
 * The {@code Table} keeps track of the cards that have been played and the
 * accumulated sum of their values according to the game rules. It also
 * provides utility operations used when recycling cards back into the deck.
 * </p>
 */
public class Table {

    /**
     * List of cards currently on the table, in the order they were played.
     */
    private final List<Card> cards = new ArrayList<>();

    /**
     * Current sum of the values of all cards on the table.
     */
    private int tableSum = 0;

    /**
     * Places a card on the table if the move is valid.
     * <p>
     * This is a convenience method that assumes the player is human and
     * delegates to {@link #placeCard(Card, boolean)} with
     * {@code isHumanPlayer = true}.
     * </p>
     *
     * @param card the card to place
     * @return {@code true} if the card was placed successfully,
     *         {@code false} if placing it would cause the sum to exceed 50
     */
    public boolean placeCard(Card card) {
        return placeCard(card, true);
    }


    /**
     * Places a card on the table if the move is valid.
     * <p>
     * The effective value of the card is computed via
     * {@link Card#getGameValue(int, boolean)}, taking into account whether
     * the player is human (for example, for Ace behaviour). If adding this
     * value to the current table sum would exceed 50, the card is not placed
     * and the method returns {@code false}.
     * </p>
     *
     * @param card          the card to place on the table
     * @param isHumanPlayer {@code true} if the card is played by a human player,
     *                      {@code false} if played by a CPU player
     * @return {@code true} if the card was placed and the sum updated,
     *         {@code false} if the move was invalid (sum would exceed 50)
     */
    public boolean placeCard(Card card, boolean isHumanPlayer) {
        int currentSum = tableSum;
        int cardValue = card.getGameValue(currentSum, isHumanPlayer);
        int newValue = currentSum + cardValue;

        System.out.println("DEBUG - Placing card: " + card +
                ", Current sum: " + currentSum +
                ", Card value: " + cardValue +
                ", New sum: " + newValue);

        // Main rule validation: total must not exceed 50
        if (newValue > 50) {
            System.out.println("Move not allowed: total would exceed 50 (" + newValue + ")");
            return false;
        }

        // Valid move - add card to table and update sum
        cards.add(card);
        tableSum = newValue;

        System.out.println("SUCCESS - Card placed. New table sum: " + tableSum);
        return true;
    }

    /**
     * Returns the last card placed on the table.
     *
     * @return the most recently played {@link Card}, or {@code null} if the table is empty
     */
    public Card getLastCard() {
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
    }

    /**
     * Returns the current sum of the values of all cards on the table.
     *
     * @return the accumulated table sum
     */
    public int getTableSum() {
        return tableSum;
    }

    /**
     * Returns all cards currently on the table.
     * <p>
     * A defensive copy is returned to prevent external modification of the
     * internal list.
     * </p>
     *
     * @return a new {@link List} containing all cards on the table
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards); // Return copy to avoid external modification
    }


    /**
     * Removes all cards from the table except the last one and recalculates the sum.
     * <p>
     * This is typically used when recycling cards back into the deck:
     * all but the most recently played card are removed and returned, while the
     * table keeps only that last card. The {@code tableSum} is recalculated
     * using the basic value of the remaining card.
     * </p>
     *
     * @return a list containing all removed cards (all except the last one);
     *         if there are zero or one cards on the table, an empty list is returned
     */
    public List<Card> removeAllButLastCard() {
        if (cards.size() <= 1) {
            return new ArrayList<>(); // No cards to remove or only one card
        }

        List<Card> removedCards = new ArrayList<>(cards.subList(0, cards.size() - 1));
        Card lastCard = cards.get(cards.size() - 1);

        // Clear the table and keep only the last card
        cards.clear();
        cards.add(lastCard);

        // Recalculate the sum based only on the remaining card (basic value)
        tableSum = lastCard.getGameValue(0, true);

        System.out.println("Table reset: kept " + lastCard + ", sum is now " + tableSum);

        return removedCards;
    }

    /**
     * Returns a short textual representation of the table.
     * <p>
     * The format is {@code "Table{sum=&lt;sum&gt;, cards=&lt;count&gt;}"}, where
     * {@code sum} is the current table sum and {@code count} is the number of cards.
     * </p>
     *
     * @return a string describing the table state
     */
    @Override
    public String toString() {
        return "Table{sum=" + tableSum + ", cards=" + cards.size() + "}";
    }
}