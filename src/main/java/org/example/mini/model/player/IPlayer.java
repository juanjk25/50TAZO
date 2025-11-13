package org.example.mini.model.player;

import org.example.mini.model.card.Card;
import java.util.List;

/**
 * Defines the contract for any player participating in the game.
 * <p>
 * Both human and machine (CPU) players must implement this interface.
 * It abstracts common operations such as managing the hand of cards,
 * determining whether a player is still active, and selecting a card to play.
 * </p>
 */
public interface IPlayer {

    /**
     * Returns the display name of the player.
     *
     * @return the player's name
     */
    String getName();

    /**
     * Returns the list of cards currently held by the player.
     *
     * @return the player's hand as a modifiable or unmodifiable {@link List} of {@link Card}s
     */
    List<Card> getHand();

    /**
     * Indicates whether this player is controlled by a human.
     *
     * @return {@code true} if the player is human-controlled, {@code false} if it is a CPU player
     */
    boolean isHuman();

    /**
     * Indicates whether this player is still active in the game.
     * <p>
     * A player typically becomes inactive when eliminated (for example, if
     * they have no playable cards left).
     * </p>
     *
     * @return {@code true} if the player is active, {@code false} if eliminated or inactive
     */
    boolean isActive();

    /**
     * Updates the active state of this player.
     *
     * @param active {@code true} to mark the player as active, {@code false} to mark as eliminated/inactive
     */
    void setActive(boolean active);

    /**
     * Adds the given card to the player's hand.
     *
     * @param card the {@link Card} to add; may be {@code null} depending on implementation
     */
    void addCard(Card card);

    /**
     * Removes the specified card from the player's hand.
     * <p>
     * If the card is not present in the hand, implementations may choose to
     * ignore the request or handle it specially.
     * </p>
     *
     * @param card the {@link Card} to remove from the hand
     */
    void removeCard(Card card);

    /**
     * Selects and returns a card to be played, based on the current state of the game.
     * <p>
     * Implementations can differ:
     * </p>
     * <ul>
     *     <li>Human players may rely on UI interaction, possibly returning {@code null}
     *         and letting the controller manage the actual selection.</li>
     *     <li>CPU players typically choose a card using some AI or heuristic.</li>
     * </ul>
     *
     * @param tableSum the current sum of the card values on the table
     * @return the chosen {@link Card} to play, or {@code null} if no move is made/possible
     */
    Card playCard(int tableSum);

    /**
     * Checks whether the player has at least one card that can be legally played.
     * <p>
     * This is typically evaluated with respect to the current table sum and
     * the game's rules (e.g. not exceeding a maximum total).
     * </p>
     *
     * @param tableSum the current sum of the card values on the table
     * @return {@code true} if the player has at least one playable card,
     *         {@code false} otherwise
     */
    boolean hasPlayableCards(int tableSum);
}
