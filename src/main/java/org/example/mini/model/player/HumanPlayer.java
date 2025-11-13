package org.example.mini.model.player;

import org.example.mini.model.card.Card;

/**
 * Represents a human-controlled player in the game.
 * <p>
 * Unlike machine players, a {@code HumanPlayer} does not decide its moves
 * autonomously. The actual card selection and play action are driven by
 * the user interface (e.g. mouse clicks), and the game controller invokes
 * the appropriate methods on this instance.
 * </p>
 */
public class HumanPlayer extends Player {

    /**
     * Creates a new human player with the given name.
     *
     * @param name the display name of the player
     */
    public HumanPlayer(String name) {
        super(name);
    }

    /**
     * Indicates that this player is human-controlled.
     *
     * @return {@code true}, since this implementation represents a human player
     */
    @Override
    public boolean isHuman() {
        return true;
    }

    /**
     * Called when the game logic expects this player to play a card.
     * <p>
     * For a human player, the selection of the card is handled externally
     * by the user interface (for example, via mouse interaction). Therefore,
     * this method does not perform any selection and always returns {@code null}.
     * The controller is responsible for removing the chosen card from the hand
     * and placing it on the table.
     * </p>
     *
     * @param tableSum the current sum of card values on the table
     * @return always {@code null}, because the UI is responsible for the move
     */
    @Override
    public Card playCard(int tableSum) {
        // The player chooses the card manually through the UI.
        // The controller manages the actual card selection and play.
        return null;
    }

    /**
     * Removes the given card from this player's hand.
     * <p>
     * If the specified card is not present in the hand, this method has no effect.
     * </p>
     *
     * @param card the card to remove from the player's hand
     */
    @Override
    public void removeCard(Card card) {
        hand.remove(card);
    }
}
