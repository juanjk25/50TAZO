package org.example.mini.model;

import org.example.mini.model.card.Card;
import org.example.mini.model.deck.Deck;

import java.util.ArrayList;
import java.util.List;



/**
 * Represents the current state of the table (sum and cards played).
 */
public class Table {
    Deck deck;
    private final List<Card> cards = new ArrayList<>();
    private int tableSum = 0;

    /**
     * Places a card on the table if the move is valid (default for human players)
     * @param card The card to place
     * @return true if the card was placed successfully, false if it would exceed 50
     */
    public boolean placeCard(Card card) {
        return placeCard(card, true);
    }


    /**
     * Places a card on the table if the move is valid - DEBUG
     * @param card The card to place
     * @param isHumanPlayer Whether the player is human (affects Ace value)
     * @return true if the card was placed successfully, false if it would exceed 50
     */
    public boolean placeCard(Card card, boolean isHumanPlayer) {
        int currentSum = tableSum;
        int cardValue = card.getGameValue(currentSum, isHumanPlayer);
        int newValue = currentSum + cardValue;

        System.out.println("DEBUG - Placing card: " + card +
                ", Current sum: " + currentSum +
                ", Card value: " + cardValue +
                ", New sum: " + newValue);

        // 🔹 Validación de regla principal
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
     * Gets the last card placed on the table
     */
    public Card getLastCard() {
        return cards.isEmpty() ? null : cards.get(cards.size() - 1);
    }

    /**
     * Gets the current sum of all cards on the table
     */
    public int getTableSum() {
        return tableSum;
    }

    /**
     * Sets the total sum on the table manually (used by Game)
     */
    public void setTableSum(int value) {
        this.tableSum = value;
    }

    /**
     * Gets all cards currently on the table
     */
    public List<Card> getCards() {
        return new ArrayList<>(cards); // Return copy to avoid external modification
    }

    /**
     * Gets the number of cards on the table
     */
    public int getCardCount() {
        return cards.size();
    }

    /**
     * Resets the table (clears all cards and sets sum to 0)
     */
    public void reset() {
        cards.clear();
        tableSum = 0;
    }


    public void reshuffleFromTable(List<Card> tableCards) {
        cards.addAll(tableCards);
        deck.shuffle();
    }


    /**
     * Removes all cards except the last one (for reshuffling)
     * @return The cards that were removed (all except the last one)
     */
    public List<Card> removeAllButLastCard() {
        if (cards.size() <= 1) {
            return new ArrayList<>(); // No cards to remove or only one card
        }

        List<Card> removedCards = new ArrayList<>(cards.subList(0, cards.size() - 1));
        Card lastCard = cards.get(cards.size() - 1);

        // Limpiar la mesa y dejar solo la última carta
        cards.clear();
        cards.add(lastCard);

        // Recalcular la suma basada solo en la última carta
        tableSum = lastCard.getGameValue(0, true); // Usar valor básico

        System.out.println("Table reset: kept " + lastCard + ", sum is now " + tableSum);

        return removedCards;
    }
    @Override
    public String toString() {
        return "Table{sum=" + tableSum + ", cards=" + cards.size() + "}";
    }
}