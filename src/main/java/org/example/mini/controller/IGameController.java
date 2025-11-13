package org.example.mini.controller;

import org.example.mini.model.game.Game;

/**
 * Defines the contract for controllers that manage a Cincuentazo game view.
 * <p>
 * Implementations are responsible for binding a {@link Game} instance
 * to a concrete user interface and keeping the visual state in sync with
 * the underlying model (table, cards and player hand).
 * </p>
 */
public interface IGameController {

    /**
     * Initializes the controller with the given game instance.
     * <p>
     * Typical implementations will:
     * </p>
     * <ul>
     *     <li>Store a reference to the provided {@link Game}.</li>
     *     <li>Prepare the initial UI state (table, players, etc.).</li>
     *     <li>Start any required background or monitoring tasks.</li>
     * </ul>
     *
     * @param game the game model to control; must not be {@code null}
     */
    void init(Game game);

    /**
     * Refreshes the visual representation of the table.
     * <p>
     * Implementations should update any UI elements that show:
     * </p>
     * <ul>
     *     <li>The current sum of cards on the table.</li>
     *     <li>The last card played and/or its image.</li>
     * </ul>
     */
    void updateTable();

    /**
     * Displays the current human player's hand in the UI.
     * <p>
     * Implementations typically render each card and attach the necessary
     * event handlers so that the user can select or play a card.
     * </p>
     */
    void showPlayerHand();
}