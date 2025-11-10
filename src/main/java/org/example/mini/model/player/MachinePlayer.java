package org.example.mini.model.player;

import org.example.mini.model.card.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents an automated machine player.
 */
public class MachinePlayer extends Player {
    private final Random random = new Random();

    public MachinePlayer(String name) {
        super(name);
    }

    @Override
    public boolean isHuman() {
        return false;
    }

    @Override
    public Card playCard(int tableSum) {
        // Obtener cartas jugables
        List<Card> playableCards = new ArrayList<>();
        for (Card card : hand) {
            if (card.canBePlayed(tableSum, false)) {
                playableCards.add(card);
            }
        }

        if (playableCards.isEmpty()) {
            return null; // No tiene cartas jugables
        }

        // Elegir una carta aleatoria de las jugables
        Card chosenCard = playableCards.get(random.nextInt(playableCards.size()));
        hand.remove(chosenCard);
        return chosenCard;
    }

    /**
     * Método alternativo para elegir carta (compatible con Game.java)
     */
    public Card chooseCardToPlay(int tableSum) {
        return playCard(tableSum);
    }

    /** 🔹 Nuevo método requerido por la interfaz IPlayer */
    @Override
    public void removeCard(Card card) {
        hand.remove(card);
    }
}
