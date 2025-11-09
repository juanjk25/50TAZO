package org.example.mini.model.card;

public class Card {
    private final String value;
    private final String suit;

    public Card(String value, String suit) {
        this.value = value;
        this.suit = suit;
    }

    public String getValue() { return value; }
    public String getSuit() { return suit; }

    public int getNumericValue(int tableSum) {
        switch (value) {
            case "A": return (tableSum + 10 <= 50) ? 10 : 1;
            case "J": case "Q": case "K": return -10;
            case "9": return 0;
            default: return Integer.parseInt(value);
        }
    }

    /**
     * Returns the relative path of the image for this card.
     * Example: "images/2_hearts.png"
     */
    public String getImagePath() {
        return "/org/example/mini/view/images/_/" + value.toLowerCase() + "_" + suit.toLowerCase() + ".png";
    }


    @Override
    public String toString() {
        return value + " of " + suit;
    }
}
