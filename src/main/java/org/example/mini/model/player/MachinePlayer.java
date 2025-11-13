package org.example.mini.model.player;

import org.example.mini.model.card.Card;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Represents a non-human (CPU-controlled) player that plays automatically.
 * <p>
 * The {@code MachinePlayer} selects one of its playable cards at random
 * when it is its turn to play. A card is considered playable if it satisfies
 * the rules defined in {@link Card#canBePlayed(int, boolean)} for a
 * non-human player.
 * </p>
 */
public class MachinePlayer extends Player {

    /**
     * Source of randomness used to choose a card from the list
     * of currently playable cards.
     */
    private final Random random = new Random();

    /**
     * Creates a new machine player with the given name.
     *
     * @param name the name of the machine player
     */
    public MachinePlayer(String name) {
        super(name);
    }

    /**
     * Indicates that this player is not human.
     *
     * @return {@code false} always, since this is a machine player
     */
    @Override
    public boolean isHuman() {
        return false;
    }

    /**
     * Selects and plays a card based on the current table sum.
     * <p>
     * This implementation collects all cards in the hand that can be legally
     * played (according to {@link Card#canBePlayed(int, boolean)} with
     * {@code isHuman} set to {@code false}) and randomly chooses one of them.
     * The chosen card is removed from the hand and returned.
     * </p>
     *
     * @param tableSum the current sum of card values on the table
     * @return the card chosen to be played, or {@code null} if no card is playable
     */
    @Override
    public Card playCard(int tableSum) {
        // Get playable cards
        List<Card> playableCards = new ArrayList<>();
        for (Card card : hand) {
            if (card.canBePlayed(tableSum, false)) {
                playableCards.add(card);
            }
        }

        if (playableCards.isEmpty()) {
            return null; // No playable cards
        }

        // Choose a random card from the playable ones
        Card chosenCard = playableCards.get(random.nextInt(playableCards.size()));
        hand.remove(chosenCard);
        return chosenCard;
    }

    /**
     * Removes the specified card from this player's hand.
     * <p>
     * If the card is not present in the hand, this method has no effect.
     * </p>
     *
     * @param card the card to remove from the hand
     */
    @Override
    public void removeCard(Card card) {
        hand.remove(card);
    }
}
