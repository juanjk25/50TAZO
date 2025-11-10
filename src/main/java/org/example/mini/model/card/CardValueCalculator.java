package org.example.mini.model.card;

import java.util.List;
import java.util.stream.Collectors;

public class CardValueCalculator {

    /**
     * Calcula el valor de una carta según las reglas del Cincuentazo
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
     * Calcula el valor del As (1 o 10) según convenga
     */
    private static int calculateAceValue(int currentSum, boolean isForHuman) {
        if (isForHuman) {
            // Para el jugador humano, elegir el mejor valor
            return (currentSum + 10 <= 50) ? 10 : 1;
        } else {
            // Para la máquina, lógica simple
            return (currentSum > 40) ? 1 : 10;
        }
    }

    /**
     * Verifica si una carta puede ser jugada sin exceder 50
     */
    public static boolean canPlayCard(Card card, int currentSum, boolean isForHuman) {
        int cardValue = calculateCardValue(card, currentSum, isForHuman);
        int newSum = currentSum + cardValue;
        return newSum <= 50;
    }

    /**
     * Obtiene todas las cartas jugables de una mano
     */
    public static List<Card> getPlayableCards(List<Card> hand, int currentSum, boolean isForHuman) {
        return hand.stream()
                .filter(card -> canPlayCard(card, currentSum, isForHuman))
                .collect(Collectors.toList());
    }
}