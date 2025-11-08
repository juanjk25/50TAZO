package org.example.mini.controller;

import org.example.mini.model.game.Game;

/**
 * Defines the contract for game control operations.
 */
public interface IGameController {
    void init(Game game);
    void updateTable();
    void showPlayerHand();
}