package org.example.mini.model.game;

import org.example.mini.model.Table;
import org.example.mini.model.player.IPlayer;

import java.util.List;

/**
 * Defines the contract for any game logic implementation.
 */
public interface IGame {
    void start();
    void nextTurn();
    boolean isGameOver();
    IPlayer getWinner();
    Table getTable();
    List<IPlayer> getPlayers();
}