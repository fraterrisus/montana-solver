package com.hitchhikerprod.montana;

import java.util.Optional;

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

    public static Optional<Card> from(String text) {
        final Integer rank = switch (text.charAt(0)) {
            case 'A' -> 1;
            case 'T' -> 10;
            case 'J' -> 11;
            case 'Q' -> 12;
            case 'K' -> 13;
            case '.', '-' -> null;
            default -> {
                try {
                    yield Integer.parseInt(text.substring(0,1));
                } catch (NumberFormatException ignored) {
                    System.out.println("Unrecognized card " + text + ", assuming blank space");
                    yield null;
                }
            }
        };
        if (rank == null) return Optional.empty();

        final Optional<Suit> suit = Suit.from(text.substring(1));
        if (suit.isEmpty()) throw new RuntimeException("Couldn't read board: unrecognized suit " + text);

        return Optional.of(new Card(rank, suit.get()));
    }
}
