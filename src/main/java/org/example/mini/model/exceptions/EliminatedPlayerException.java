package org.example.mini.model.exceptions;

/**
 * Exception thrown when an operation is attempted on a player
 * who has already been eliminated from the game.
 */
public class EliminatedPlayerException extends Exception {

    /**
     * Constructs a new {@code EliminatedPlayerException} with the specified detail message.
     *
     * @param message the detail message explaining the elimination condition
     */
    public EliminatedPlayerException(String message) {
        super(message);
    }
}