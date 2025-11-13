package org.example.mini.model.game;

import org.example.mini.model.Table;
import org.example.mini.model.card.Card;
import org.example.mini.model.deck.Deck;
import org.example.mini.model.exceptions.EliminatedPlayerException;
import org.example.mini.model.player.*;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Main game controller that manages turns, players, the deck, and the table state.
 * Handles all core logic for the Cincuentazo game.
 */
public class Game {

    private final Table table;           // Represents the table where cards are placed
    private final Deck deck;             // Represents the deck of cards
    private final List<IPlayer> players; // List of all players (human + CPUs)
    private int currentPlayerIndex;      // Index of the current player's turn
    private boolean gameOver;            // Flag to indicate if the game is over

    /**
     * Constructor — initializes the table, deck, and players.
     *
     * @param cpuCount number of CPU (machine) players to include
     */
    public Game(int cpuCount) {
        this.table = new Table();
        this.deck = new Deck();
        this.players = new ArrayList<>();

        // Create one human player
        players.add(new HumanPlayer("You"));

        // Create the requested number of CPU players
        for (int i = 1; i <= cpuCount; i++) {
            players.add(new MachinePlayer("CPU " + i));
        }

        this.currentPlayerIndex = 0;
        this.gameOver = false;
    }

    /**
     * 🔹 Starts the game — deals cards and places the first card on the table.
     */
    public void start() {
        System.out.println("Game started with " + players.size() + " players.");

        // Deal 4 cards to each player
        for (IPlayer p : players) {
            for (int i = 0; i < 4; i++) {
                Card card = deck.draw();
                if (card != null) {
                    p.addCard(card);
                }
            }
        }

        // Place the initial card on the table
        Card initialCard = deck.draw();
        if (initialCard != null) {
            table.placeCard(initialCard, true);
            System.out.println("Initial card: " + initialCard +
                    " → initial sum = " + table.getTableSum());
        } else {
            System.out.println("Error: No cards available for initial table card!");
        }
    }

    /**
     * 🔹 Returns the player whose turn it currently is.
     *
     * @return the current player
     */
    public IPlayer getCurrentPlayer() {
        return players.get(currentPlayerIndex);
    }

    /**
     * 🔹 Advances the turn to the next player.
     * Wraps around to the first player when reaching the end.
     */
    public void nextTurn() {
        currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
    }

    /**
     * 🔹 Checks if any players should be eliminated and whether the game should end.
     * Players are eliminated if they have no playable cards left.
     */
    public void checkAndEliminatePlayers() {
        Iterator<IPlayer> iterator = players.iterator();
        while (iterator.hasNext()) {
            IPlayer player = iterator.next();

            // Eliminate inactive players with no playable cards
            if (player.isActive() && !player.hasPlayableCards(table.getTableSum())) {
                player.setActive(false);
                System.out.println(player.getName() + " has been eliminated!");

                // Return their remaining cards to the deck
                for (Card card : player.getHand()) {
                    deck.returnCard(card);
                }
                player.getHand().clear();
            }
        }

        // Check how many players are still active
        long activePlayers = players.stream().filter(IPlayer::isActive).count();
        if (activePlayers <= 1) {
            gameOver = true;
            System.out.println("🏁 Game Over! Only " + activePlayers + " active player(s) remain.");
        }
    }

    /**
     * 🔹 Ensures the deck has cards available.
     * If empty, it recycles cards from the table (except the last one).
     */
    private void ensureDeckHasCards() {
        if (deck.isEmpty()) {
            System.out.println("Deck is empty — recycling cards from the table...");
            List<Card> recycledCards = table.removeAllButLastCard();
            if (!recycledCards.isEmpty()) {
                deck.reshuffleFromTable(recycledCards);
            }
        }
    }

    /**
     * 🔹 Draws a card from the deck, recycling if necessary.
     *
     * @return the drawn card
     */
    public Card drawCardWithRecycle() {
        ensureDeckHasCards();
        return deck.draw();
    }

    /**
     * 🔹 Returns the current table instance.
     *
     * @return the game table
     */
    public Table getTable() {
        return table;
    }

    /**
     * 🔹 Returns the current deck instance.
     *
     * @return the deck
     */
    public Deck getDeck() {
        return deck;
    }

    /**
     * 🔹 Returns the list of all players (human and CPUs).
     *
     * @return list of players
     */
    public List<IPlayer> getPlayers() {
        return players;
    }

    /**
     * 🔹 Checks if the game has ended.
     *
     * @return true if the game is over, false otherwise
     */
    public boolean isGameOver() {
        return gameOver;
    }

    /**
     * 🔹 Ends the game manually (for example, from the controller or UI).
     */
    public void endGame() {
        this.gameOver = true;
    }

    /**
     * 🔹 Returns the current winner (the last active player).
     *
     * @return the winning player, or null if none found
     */
    public IPlayer getWinner() {
        return players.stream().filter(IPlayer::isActive).findFirst().orElse(null);
    }
}
