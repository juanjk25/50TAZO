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
        for (Card c : hand) {
            int value = c.getNumericValue(tableSum);
            if (tableSum + value <= 50) {
                hand.remove(c);
                return c;
            }
        }
        eliminate();
        return null;
    }
}