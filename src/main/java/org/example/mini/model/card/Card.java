package org.example.mini.model.card;

import java.util.List;
import java.util.stream.Collectors;


public class Card {
    private final String suit;
    private final String rank;
    private boolean faceUp;

    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank;
        this.faceUp = false;
    }

    // GETTERS BÁSICOS
    public String getSuit() {
        return suit;
    }

    public String getRank() {
        return rank;
    }

    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    public boolean isFaceUp() {
        return faceUp;
    }

    /**
     * Returns the basic numeric value of the card (without game context)
     */
    public int getValue() {
        switch (rank) {
            case "2": case "3": case "4": case "5":
            case "6": case "7": case "8": case "10":
                return Integer.parseInt(rank);
            case "9": return 0;
            case "J": case "Q": case "K": return -10;
            case "A": return 1;
            default: return 0;
        }
    }

    /**
     * Returns the game value considering current game state
     */
    public int getGameValue(int currentSum, boolean isForHuman) {
        return calculateCardValue(this.rank, currentSum, isForHuman);
    }

    /**
     * Verifica si esta carta puede ser jugada
     */
    public boolean canBePlayed(int currentSum, boolean isForHuman) {
        int cardValue = getGameValue(currentSum, isForHuman);
        return (currentSum + cardValue) <= 50;
    }

    /**
     * Calcula el valor considerando el contexto del juego
     */
    private int calculateCardValue(String rank, int currentSum, boolean isForHuman) {
        switch (rank) {
            case "2": case "3": case "4": case "5":
            case "6": case "7": case "8": case "10":
                return Integer.parseInt(rank);
            case "9":
                return 0;
            case "J": case "Q": case "K":
                return -10;
            case "A":
                return calculateAceValue(currentSum, isForHuman);
            default:
                return 0;
        }
    }

    private int calculateAceValue(int currentSum, boolean isForHuman) {
        if (isForHuman) {
            return (currentSum + 10 <= 50) ? 10 : 1;
        } else {
            return (currentSum > 40) ? 1 : 10;
        }
    }

    /**
     * Returns the image path
     */
    /**
     * Returns the relative path of the image for this card.
     * Example: "images/2_hearts.png"
     */
    public String getImagePath() {
        return "/org/example/mini/view/images/_/" + suit.toLowerCase() + "_" + rank.toLowerCase() + ".png";
    }


    @Override
    public String toString() {
        return rank + " of " + suit + (faceUp ? " (UP)" : " (DOWN)");
    }
}