package org.example.mini.model.player;

import org.example.mini.model.card.Card;

import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class implementing common player behavior.
 */
public abstract class Player implements IPlayer {
    protected String name;
    protected List<Card> hand;

    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public List<Card> getHand() {
        return hand;
    }

    @Override
    public void addCard(Card card) {
        hand.add(card);
    }

    /**
     * Método abstracto: cada tipo de jugador define cómo juega su carta
     */
    @Override
    public abstract Card playCard(int tableSum);
}