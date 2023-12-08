package com.hitchhikerprod.montana;

public record Card(int rank, Suit suit) {
    public Card follower() {
        final int nextRank = rank + 1;
        if (nextRank > 13) return null;
        return new Card(nextRank, suit);
    }

    public String toString() {
        return (switch (rank) {
            case 1 -> "A";
            case 10 -> "T";
            case 11 -> "J";
            case 12 -> "Q";
            case 13 -> "K";
            default -> String.valueOf(rank);
        }) + suit.firstChar();
    }
}
