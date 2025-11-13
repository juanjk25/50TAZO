package org.example.mini.model.player;

import org.example.mini.model.card.Card;
import java.util.ArrayList;
import java.util.List;

/**
 * Abstract base class providing common behavior for all players.
 * <p>
 * This class implements most of the {@link IPlayer} contract, including:
 * </p>
 * <ul>
 *     <li>Storing the player's name and hand of cards.</li>
 *     <li>Tracking whether the player is currently active in the game.</li>
 *     <li>Basic operations to add cards and check for playable cards.</li>
 * </ul>
 * Concrete subclasses must implement {@link #isHuman()} and
 * {@link #playCard(int)} to define how they select and play cards.
 */
public abstract class Player implements IPlayer {

    /**
     * Display name of the player.
     */
    protected String name;

    /**
     * List of cards currently held in the player's hand.
     */
    protected List<Card> hand;

    /**
     * Flag indicating whether the player is still active in the game.
     * <p>
     * When {@code false}, the player is considered eliminated and will be
     * skipped in turn rotation.
     * </p>
     */
    protected boolean active; // Cambié de isActive a active

    /**
     * Creates a new player with the given name.
     * <p>
     * The player starts with an empty hand and is marked as active.
     * </p>
     *
     * @param name the display name of the player
     */
    public Player(String name) {
        this.name = name;
        this.hand = new ArrayList<>();
        this.active = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        return name;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Card> getHand() {
        return hand;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addCard(Card card) {
        hand.add(card);
    }

    /**
     * Defines how this player chooses and plays a card.
     * <p>
     * Each concrete subclass must implement its own strategy:
     * </p>
     * <ul>
     *     <li>Human players typically rely on UI interaction.</li>
     *     <li>Machine players usually implement some automated decision logic.</li>
     * </ul>
     *
     * @param tableSum the current sum of the card values on the table
     * @return the {@link Card} selected to be played, or {@code null} if
     *         no card is played
     */
    @Override
    public abstract Card playCard(int tableSum);

    /**
     * Checks whether the player has at least one card that can be legally played.
     * <p>
     * This method iterates over the player's hand and uses
     * {@link Card#canBePlayed(int, boolean)} with {@link #isHuman()} to
     * determine if any card is playable under the current table sum.
     * </p>
     *
     * @param tableSum the current sum of the card values on the table
     * @return {@code true} if the player has at least one playable card,
     *         {@code false} otherwise
     */
    public boolean hasPlayableCards(int tableSum) {
        for (Card card : hand) {
            if (card.canBePlayed(tableSum, isHuman())) {
                return true;
            }
        }
        return false;
    }
}