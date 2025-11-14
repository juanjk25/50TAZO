package org.example.mini.model.card;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Represents a single playing card used in the game.
 * <p>
 * A {@code Card} has a suit and a rank, can be face up or face down,
 * and knows how much it contributes to the table sum depending on the
 * current game state and the type of player (human or CPU) who plays it.
 * </p>
 */
public class Card {
    private final String suit;
    private final String rank;
    private boolean faceUp;

    /**
     * Creates a new card with the given suit and rank.
     * <p>
     * The {@code rank} is trimmed and the card is initially face down.
     * </p>
     *
     * @param suit the suit of the card (e.g. {@code "hearts"}, {@code "spades"})
     * @param rank the rank of the card (e.g. {@code "A"}, {@code "10"}, {@code "K"})
     */
    public Card(String suit, String rank) {
        this.suit = suit;
        this.rank = rank.trim();
        this.faceUp = false;
    }

    /**
     * Returns the suit of this card.
     *
     * @return the suit as a {@link String}
     */
    public String getSuit() {
        return suit;
    }

    /**
     * Returns the rank of this card.
     *
     * @return the rank as a {@link String}
     */
    public String getRank() {
        return rank;
    }

    /**
     * Returns the basic numeric value of the card, ignoring game context.
     * <p>
     * This is equivalent to {@link #getGameValue(int, boolean)} with a
     * current sum of {@code 0} and assuming a human player.
     * </p>
     *
     * @return the base value of the card
     */
    public int getValue() {
        return getGameValue(0, true);
    }

    /**
     * Checks if the card is currently face up.
     * @return {@code true} if the card is face up, {@code false} otherwise
     */
    public boolean isFaceUp() {
        return faceUp;
    }

    /**
     * Sets whether the card is face up or face down.
     *
     * @param faceUp {@code true} to set the card face up, {@code false} for face down
     */
    public void setFaceUp(boolean faceUp) {
        this.faceUp = faceUp;
    }

    /**
     * Computes the card's value within the context of the current game state.
     * <p>
     * The value can depend on:
     * </p>
     * <ul>
     *     <li>The rank (e.g. numeric card, face card, Ace).</li>
     *     <li>The current sum of the table.</li>
     *     <li>Whether the value is being evaluated for a human or CPU player.</li>
     * </ul>
     *
     * @param currentSum the current sum of all cards on the table
     * @param isForHuman {@code true} if the value is computed for a human player,
     *                   {@code false} for a CPU player
     * @return the numeric value of this card in the given context
     */
    public int getGameValue(int currentSum, boolean isForHuman) {
        return calculateCardValue(rank, currentSum, isForHuman);
    }

    /**
     * Checks whether this card can be legally played given the current table sum.
     * <p>
     * A card is playable if adding its contextual value to the current sum
     * does not exceed 50.
     * </p>
     *
     * @param currentSum the current sum of all cards on the table
     * @param isForHuman {@code true} if the check is for a human player,
     *                   {@code false} for a CPU player
     * @return {@code true} if playing this card would keep the total ≤ 50,
     *         {@code false} otherwise
     */
    public boolean canBePlayed(int currentSum, boolean isForHuman) {
        int cardValue = getGameValue(currentSum, isForHuman);
        return (currentSum + cardValue) <= 50;
    }

    /**
     * Calculates the numeric value of a card rank in the given context.
     * <p>
     * Supported ranks and their values:
     * </p>
     * <ul>
     *     <li>{@code "2", "3", "4", "5", "6", "7", "8", "10"} → numeric value.</li>
     *     <li>{@code "9"}, {@code "NINE"} → {@code 0}.</li>
     *     <li>{@code "J", "JACK", "Q", "QUEEN", "K", "KING"} → {@code -10}.</li>
     *     <li>{@code "A"}, {@code "ACE"} → value determined by
     *         {@link #calculateAceValue(int, boolean)}.</li>
     *     <li>Any other rank → {@code 0}, with a warning logged.</li>
     * </ul>
     *
     * @param rank       the rank to evaluate
     * @param currentSum the current sum of the table
     * @param isForHuman {@code true} to evaluate for a human player,
     *                   {@code false} for a CPU player
     * @return the card value according to game rules
     */
    private int calculateCardValue(String rank, int currentSum, boolean isForHuman) {
        // Lowercase and trim for uniformity
        rank = rank.toUpperCase().trim();

        switch (rank) {
            case "2": case "3": case "4": case "5":
            case "6": case "7": case "8": case "10":
                return Integer.parseInt(rank);

            case "9": case "NINE":
                return 0;

            case "J": case "JACK":
            case "Q": case "QUEEN":
            case "K": case "KING":
                return -10;

            case "A": case "ACE":
                return calculateAceValue(currentSum, isForHuman);

            default:
                System.out.println("⚠ Carta desconocida: '" + rank + "' — se toma como 0");
                return 0;
        }
    }

    /**
     * Calculates the value of an Ace based on the current sum and player type.
     * <p>
     * Rules:
     * </p>
     * <ul>
     *     <li>For a human player:
     *         <ul>
     *             <li>Use {@code 10} if {@code currentSum + 10 ≤ 50}.</li>
     *             <li>Otherwise use {@code 1}.</li>
     *         </ul>
     *     </li>
     *     <li>For a CPU player:
     *         <ul>
     *             <li>Use {@code 1} if {@code currentSum > 40}.</li>
     *             <li>Otherwise use {@code 10}.</li>
     *         </ul>
     *     </li>
     * </ul>
     *
     * @param currentSum the current sum of cards on the table
     * @param isForHuman {@code true} when evaluating for a human,
     *                   {@code false} when evaluating for a CPU
     * @return the value of the Ace, either {@code 1} or {@code 10}
     */
    private int calculateAceValue(int currentSum, boolean isForHuman) {
        if (isForHuman) {
            return (currentSum + 10 <= 50) ? 10 : 1;
        } else {
            return (currentSum > 40) ? 1 : 10;
        }
    }

    /**
     * Returns the relative path to the image resource representing this card.
     * <p>
     * The path is constructed from the rank and suit in lowercase, following
     * the pattern:
     * {@code /org/example/mini/view/images/_/&lt;rank&gt;_&lt;suit&gt;.png}
     * </p>
     *
     * @return the image path for this card
     */
    public String getImagePath() {
        return "/org/example/mini/view/images/_/" + rank.toLowerCase() + "_" + suit.toLowerCase() + ".png";
    }

    /**
     * Returns a human-readable string representation of this card.
     * <p>
     * The format is:
     * {@code "&lt;rank&gt; of &lt;suit&gt; (UP|DOWN)"}.
     * </p>
     *
     * @return a string describing this card
     */
    @Override
    public String toString() {
        return rank + " of " + suit + (faceUp ? " (UP)" : " (DOWN)");
    }
}
