package org.example.mini.model.deck;

import org.example.mini.model.card.Card;
import org.example.mini.model.exceptions.EmptyDeckException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DeckTest {

    @Test
    void testDeckStartsWith52Cards() {
        Deck deck = new Deck();
        // A newly created deck must contain all 52 standard cards
        assertEquals(52, deck.size(), "Deck must initialize with 52 cards");
    }

    @Test
    void testShuffleChangesOrder() {
        Deck deck = new Deck();
        // Copy the current order of the cards before shuffling
        List<Card> beforeShuffle = new ArrayList<>(deck.getCards());

        deck.shuffle();
        List<Card> afterShuffle = deck.getCards();

        // The order should change after shuffling
        assertNotEquals(beforeShuffle, afterShuffle, "Shuffle should change the order of the cards");
    }

    @Test
    void testDrawReducesDeckSize() {
        Deck deck = new Deck();
        int initialSize = deck.size();

        // Draw a card from the deck
        Card drawnCard = deck.draw();

        // Ensure the drawn card is not null and that deck size decreases by 1
        assertNotNull(drawnCard, "Drawn card should not be null");
        assertEquals(initialSize - 1, deck.size(), "Drawing a card must reduce deck size by 1");
    }

    @Test
    void testDrawUntilEmptyThenThrow() {
        Deck deck = new Deck();

        // Draw all 52 cards to empty the deck
        for (int i = 0; i < 52; i++) {
            deck.draw();
        }

        // Verify deck is now empty
        assertTrue(deck.isEmpty(), "Deck should be empty after drawing all cards");

        // Drawing again must throw an exception
        assertThrows(EmptyDeckException.class, deck::draw,
                "Drawing from an empty deck must throw EmptyDeckException");
    }

    @Test
    void testReturnCardIncreasesSize() {
        Deck deck = new Deck();

        // Draw a card so deck size decreases
        Card card = deck.draw();
        int sizeAfterDraw = deck.size();

        // Returning a card should increase deck size by 1
        deck.returnCard(card);

        assertEquals(sizeAfterDraw + 1, deck.size(), "Returning a card should increase deck size by 1");
    }

    @Test
    void testReshuffleFromTableAddsCards() {
        Deck deck = new Deck();

        // Draw two cards to simulate cards on the table
        Card card1 = deck.draw();
        Card card2 = deck.draw();

        List<Card> tableCards = List.of(card1, card2);

        int sizeBefore = deck.size();

        // Reshuffling from table should add these cards back into the deck
        deck.reshuffleFromTable(tableCards);

        assertEquals(sizeBefore + 2, deck.size(), "Reshuffle must add table cards to the deck");
    }

    @Test
    void testIsEmptyWorksCorrectly() {
        Deck deck = new Deck();
        // A new deck should not be empty
        assertFalse(deck.isEmpty(), "A new deck should not be empty");

        // Draw all cards to empty the deck
        for (int i = 0; i < 52; i++) deck.draw();

        // Now the deck must be empty
        assertTrue(deck.isEmpty(), "Deck should be empty now");
    }
}

