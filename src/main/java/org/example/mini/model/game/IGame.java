package org.example.mini.model.game;

import org.example.mini.model.Table;
import org.example.mini.model.player.IPlayer;

import java.util.List;

/**
 * Defines the contract for a Cincuentazo game logic implementation.
 * <p>
 * Implementations of this interface are responsible for managing the core
 * game flow: initializing the game, progressing turns, tracking whether the
 * game has ended, and exposing the current state (table and players).
 * </p>
 */
public interface IGame {

    /**
     * Starts or initializes the game.
     * <p>
     * Typical implementations will:
     * </p>
     * <ul>
     *     <li>Create or reset the deck and table.</li>
     *     <li>Deal initial cards to the players.</li>
     *     <li>Place the first card on the table if required by the rules.</li>
     * </ul>
     */
    void start();

    /**
     * Advances the game to the next player's turn.
     * <p>
     * Implementations are expected to update the internal turn index and
     * potentially perform any automatic actions required at the start of a
     * turn (such as checking for eliminated players).
     * </p>
     */
    void nextTurn();

    /**
     * Indicates whether the game has finished.
     * <p>
     * A game is typically over when only one active player remains or when
     * some end condition defined by the rules has been reached.
     * </p>
     *
     * @return {@code true} if the game is over, {@code false} otherwise
     */
    boolean isGameOver();

    /**
     * Returns the current winner of the game, if there is one.
     * <p>
     * Usually this is the last active player once the game is over.
     * If the game is still in progress or no winner can be determined, an
     * implementation may return {@code null}.
     * </p>
     *
     * @return the winning player, or {@code null} if no winner is determined
     */
    IPlayer getWinner();

    /**
     * Returns the table associated with this game.
     * <p>
     * The table stores the cards that have been played and the current sum
     * of their values.
     * </p>
     *
     * @return the {@link Table} representing the current table state
     */
    Table getTable();

    /**
     * Returns the list of players participating in this game.
     * <p>
     * The list may include both human and machine players. The ordering
     * usually corresponds to turn order.
     * </p>
     *
     * @return a list of {@link IPlayer} instances currently in the game
     */
    List<IPlayer> getPlayers();
}