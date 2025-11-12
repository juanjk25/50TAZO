package org.example.mini.model.card;

import java.util.List;
import java.util.stream.Collectors;

public class CardValueCalculator {

    /**
     * Calculate the value of a card based on its rank and current game context
     */
    public static int calculateCardValue(Card card, int currentSum, boolean isForHuman) {
        String rank = card.getRank();

        switch (rank) {
            case "2": case "3": case "4": case "5":
            case "6": case "7": case "8": case "10":
                return Integer.parseInt(rank);

            case "9":
                return 0; // Ni suma ni resta

            case "J": case "Q": case "K":
                return -10;

            case "A":
                return calculateAceValue(currentSum, isForHuman);

            default:
                return 0;
        }
    }

    /**
     * Calculate the value of an Ace card based on current sum and player type
     */
    private static int calculateAceValue(int currentSum, boolean isForHuman) {
        if (isForHuman) {
            // For the human player, prefer 10 if it doesn't exceed 50
            return (currentSum + 10 <= 50) ? 10 : 1;
        } else {
            // For CPU players, use 1 if over 40, else 10
            return (currentSum > 40) ? 1 : 10;
        }
    }

    /**
     * Verify if a card can be played without exceeding the limit
     */
    public static boolean canPlayCard(Card card, int currentSum, boolean isForHuman) {
        int cardValue = calculateCardValue(card, currentSum, isForHuman);
        int newSum = currentSum + cardValue;
        return newSum <= 50;
    }
}