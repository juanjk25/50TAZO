package org.example.mini.model.card;

public class Card {
    private final String value;  // 2–10, J, Q, K, A
    private final String suit;   // Hearts, Spades, Clubs, Diamonds

    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    public int getNumericValue(int tableSum) {
        switch (value) {
            case "A":
                return (tableSum + 10 <= 50) ? 10 : 1;
            case "J":
            case "Q":
            case "K":
                return -10;
            case "9":
                return 0;
            default:
                return Integer.parseInt(value);
        }
    }

    public String getValue() { return value; }
    public String getSuit() { return suit; }

    @Override
    public String toString() {
        return value + " of " + suit;
    }
}


