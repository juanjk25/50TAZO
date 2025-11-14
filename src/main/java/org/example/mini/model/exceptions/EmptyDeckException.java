package org.example.mini.model.exceptions;

/**
 * Exception thrown when an operation is performed on a deck
 * that currently contains no cards.
 * <p>
 * This is an unchecked exception, indicating a programming or
 * logical error where the caller should ensure the deck is not
 * empty before drawing or accessing cards.
 * </p>
 */
public class EmptyDeckException extends RuntimeException {

    /**
     * Constructs a new {@code EmptyDeckException} with the specified detail message.
     *
     * @param message the detail message explaining the cause of the exception
     */
    public EmptyDeckException(String message) {
        super(message);
    }
}