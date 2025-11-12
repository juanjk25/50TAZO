package org.example.mini.model.card;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a playing card with suit, rank, and face orientation.
 */
public class Card {
    private final String suit;
    private final String rank;
    private boolean faceUp;

    /**
     * CONSTRUCTOR
     */
    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank.trim();
        this.faceUp = false;
    }

    /**
     * GETTERS & SETTERS
     */
    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public String getSymbol() {
        return rank;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    /**
     * Returns the basic numeric value of the card (without game context)
     */
    public int getValue() {
        return getGameValue(0, true);
    }

    /**
     * Returns the game value considering current game state.
     * Takes into account human/AI behavior and sum rules.
     */
    public int getGameValue(int currentSum, boolean isForHuman) {
        return calculateCardValue(rank, currentSum, isForHuman);
    }

    /**
     * Checks if this card can be played given the current table sum.
     */
    public boolean canBePlayed(int currentSum, boolean isForHuman) {
        int cardValue = getGameValue(currentSum, isForHuman);
        return (currentSum + cardValue) <= 50;
    }

    /**
     * Calculates the numeric value of a card considering rank variations.
     */
    private int calculateCardValue(String rank, int currentSum, boolean isForHuman) {
        // Lowercase and trim for uniformity
        rank = rank.toUpperCase().trim();

        switch (rank) {
            case "2": case "3": case "4": case "5":
            case "6": case "7": case "8": case "10":
                return Integer.parseInt(rank);

            case "9": case "NINE":
                return 0;

            case "J": case "JACK":
            case "Q": case "QUEEN":
            case "K": case "KING":
                return -10;

            case "A": case "ACE":
                return calculateAceValue(currentSum, isForHuman);

            default:
                System.out.println("⚠ Carta desconocida: '" + rank + "' — se toma como 0");
                return 0;
        }
    }

    /**
     * Calculates the value of an Ace depending on the current sum and player type.
     */
    private int calculateAceValue(int currentSum, boolean isForHuman) {
        if (isForHuman) {
            return (currentSum + 10 <= 50) ? 10 : 1;
        } else {
            return (currentSum > 40) ? 1 : 10;
        }
    }

    /**
     * Returns the relative path of the image for this card.
     */
    public String getImagePath() {
        return "/org/example/mini/view/images/_/" + rank.toLowerCase() + "_" + suit.toLowerCase() + ".png";
    }

    /**
     * String representation of the card.
     */
    @Override
    public String toString() {
        return rank + " of " + suit + (faceUp ? " (UP)" : " (DOWN)");
    }
}
