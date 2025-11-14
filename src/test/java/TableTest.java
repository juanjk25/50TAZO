package org.example.mini.model;

import org.example.mini.model.card.Card;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TableTest {

    private Table table;

    @BeforeEach
    void setUp() {
        // Initialize a new table before each test to start with a clean state
        table = new Table();
    }

    @Test
    void testPlaceCardValid() {
        // Mocking a card that returns a value of 10 when played on sum = 0 by a human
        Card card = Mockito.mock(Card.class);
        Mockito.when(card.getGameValue(0, true)).thenReturn(10);

        // Attempt to place the card on the table
        boolean result = table.placeCard(card);

        // Ensure the card was successfully placed
        assertTrue(result);
        assertEquals(10, table.getTableSum(), "Table sum should be updated after placing a valid card");
        assertEquals(card, table.getLastCard(), "Last card played should be stored");
    }

    @Test
    void testPlaceCardExceeds50() {
        // Manually set the table sum near the limit
        table.setTableSum(45);

        // Mock card returns 10, which would exceed the limit (45 + 10 > 50)
        Card card = Mockito.mock(Card.class);
        Mockito.when(card.getGameValue(45, true)).thenReturn(10);

        boolean result = table.placeCard(card);

        // Card placement must fail
        assertFalse(result, "Placing a card that exceeds 50 should fail");

        // Table sum and lastCard must not change
        assertEquals(45, table.getTableSum(), "Table sum must remain unchanged after invalid card");
        assertNull(table.getLastCard(), "No last card should be stored when placement fails");
    }

    @Test
    void testReset() {
        // Place a card to change the state of the table
        Card card = Mockito.mock(Card.class);
        Mockito.when(card.getGameValue(0, true)).thenReturn(10);

        table.placeCard(card);

        // Now reset the table
        table.reset();

        // Reset must clear everything
        assertEquals(0, table.getTableSum(), "Table sum must reset to 0");
        assertEquals(0, table.getCardCount(), "Card count must reset to 0");
    }

    @Test
    void testRemoveAllButLastCard() {
        // Mock three cards with controlled values
        Card c1 = Mockito.mock(Card.class);
        Card c2 = Mockito.mock(Card.class);
        Card c3 = Mockito.mock(Card.class);

        // Simulate sequential placement: sum -> 0 + 5 -> 5 + 10 -> 15 + 20
        Mockito.when(c1.getGameValue(0, true)).thenReturn(5);
        Mockito.when(c2.getGameValue(5, true)).thenReturn(10);
        Mockito.when(c3.getGameValue(15, true)).thenReturn(20);

        // Place the cards on the table
        table.placeCard(c1);
        table.placeCard(c2);
        table.placeCard(c3);

        // Remove all but the last card
        List<Card> removed = table.removeAllButLastCard();

        // Verify only the first two cards are removed
        assertEquals(2, removed.size(), "Two cards should be removed, leaving only the last one");
        assertTrue(removed.contains(c1), "First card must be removed");
        assertTrue(removed.contains(c2), "Second card must be removed");

        // Table should only keep the third card
        assertEquals(1, table.getCardCount(), "Only one card should remain on the table");
        assertEquals(c3, table.getLastCard(), "The last remaining card must be the most recently played");
    }
}
