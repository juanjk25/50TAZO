package org.example.mini.model.player;

import org.example.mini.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class implementing common player behavior.
 */
public abstract class Player implements IPlayer {
    protected final String name;
    protected final List<Card> hand = new ArrayList<>();
    protected boolean eliminated = false;

    public Player(String name) {
        this.name = name;
    }

    @Override
    public String getName() { return name; }

    @Override
    public boolean isEliminated() { return eliminated; }

    @Override
    public void eliminate() { eliminated = true; }

    @Override
    public void receiveCard(Card card) {
        hand.add(card);
    }

    public List<Card> getHand() { return hand; }

    @Override
    public abstract Card playCard(int tableSum);
}