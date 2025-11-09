package org.example.mini.model.player;

import org.example.mini.model.card.Card;

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
    public Card playCard(int tableSum) {
        if (hand.isEmpty()) return null;

        // Ejemplo: CPU elige una carta al azar
        int index = random.nextInt(hand.size());
        return hand.remove(index);
    }
}