package org.example.mini.controller;

import javafx.event.ActionEvent;


/**
 * Defines the contract for the start (menu) screen controller.
 * <p>
 * Implementations of this interface are responsible for reacting to the
 * user's selection of the number of CPU opponents and starting the game
 * accordingly.
 * Each method typically corresponds to a different button in the start UI.
 * </p>
 */
public interface IStartController {

    /**
     * Starts a new game with one CPU opponent.
     *
     * @param event the action event triggered by the UI control (e.g. button click)
     */
   void startGame1(ActionEvent event);

    /**
     * Starts a new game with two CPU opponents.
     *
     * @param event the action event triggered by the UI control (e.g. button click)
     */
   void startGame2(ActionEvent event);

    /**
     * Starts a new game with three CPU opponents.
     *
     * @param event the action event triggered by the UI control (e.g. button click)
     */
   void startGame3(ActionEvent event);
}
