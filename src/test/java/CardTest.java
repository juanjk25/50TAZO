package org.example.mini.model.card;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the Card class.
 * These tests validate card creation, value rules, playability,
 * face orientation, and other utility behaviors.
 */
public class CardTest {

    /**
     * Ensures the constructor correctly initializes suit, rank,
     * and that cards start face down.
     */
    @Test
    void testConstructorAndBasicGetters() {
        Card card = new Card("hearts", "A");

        assertEquals("hearts", card.getSuit());
        assertEquals("A", card.getRank());
        assertFalse(card.isFaceUp());
    }

    /**
     * Verifies that the faceUp property toggles correctly.
     */
    @Test
    void testFaceUpSetter() {
        Card card = new Card("spades", "K");

        card.setFaceUp(true);
        assertTrue(card.isFaceUp());

        card.setFaceUp(false);
        assertFalse(card.isFaceUp());
    }

    /**
     * Ensures numeric cards retain their correct values.
     */
    @Test
    void testNumericCardsReturnCorrectValue() {
        assertEquals(5, new Card("clubs", "5").getValue());
        assertEquals(10, new Card("diamonds", "10").getValue());
        assertEquals(7, new Card("hearts", "7").getValue());
    }

    /**
     * The value of a 9 should be zero according to game rules.
     */
    @Test
    void testNineIsZero() {
        assertEquals(0, new Card("hearts", "9").getValue());
    }

    /**
     * J, Q, and K should all return -10.
     */
    @Test
    void testFaceCardsAreMinusTen() {
        assertEquals(-10, new Card("spades", "K").getValue());
        assertEquals(-10, new Card("clubs", "Q").getValue());
        assertEquals(-10, new Card("diamonds", "J").getValue());
    }

    /**
     * Human players use Ace as 10 if it does not exceed the limit.
     */
    @Test
    void testAceValueForHumanLowSum() {
        Card ace = new Card("hearts", "A");
        assertEquals(10, ace.getGameValue(20, true));
    }

    /**
     * Human players use Ace as 1 if using 10 would exceed 50.
     */
    @Test
    void testAceValueForHumanHighSum() {
        Card ace = new Card("hearts", "A");
        assertEquals(1, ace.getGameValue(45, true));
    }

    /**
     * CPU uses Ace = 1 when the table sum is high ( > 40 ).
     */
    @Test
    void testAceValueForCPUHighSum() {
        Card ace = new Card("spades", "A");
        assertEquals(1, ace.getGameValue(45, false));
    }

    /**
     * CPU uses Ace = 10 when the table sum is low ( <= 40 ).
     */
    @Test
    void testAceValueForCPULowSum() {
        Card ace = new Card("spades", "A");
        assertEquals(10, ace.getGameValue(20, false));
    }

    /**
     * Valid play: card value + current sum must be <= 50.
     */
    @Test
    void testCanBePlayedWhenValid() {
        Card card = new Card("hearts", "5");
        assertTrue(card.canBePlayed(40, true)); // 40 + 5 = 45 <= 50
    }

    /**
     * Invalid play: card value + current sum exceeds the limit.
     */
    @Test
    void testCannotBePlayedIfExceedsLimit() {
        Card card = new Card("spades", "10");
        assertFalse(card.canBePlayed(45, true)); // 45 + 10 = 55 > 50
    }

    /**
     * Ensures the string representation matches the required format.
     */
    @Test
    void testToStringFormat() {
        Card card = new Card("clubs", "A");
        assertEquals("A of clubs (DOWN)", card.toString());

        card.setFaceUp(true);
        assertEquals("A of clubs (UP)", card.toString());
    }

    /**
     * Verifies that the card generates the correct image file path.
     */
    @Test
    void testImagePath() {
        Card card = new Card("hearts", "Q");
        assertEquals("/org/example/mini/view/images/_/q_hearts.png", card.getImagePath());
    }
}
