package com.hitchhikerprod.montana;

import java.util.Arrays;
import java.util.Optional;

public enum Suit {
    CLUBS, DIAMONDS, HEARTS, SPADES;

    public String firstChar() {
        return name().substring(0,1);
    }

    public static Optional<Suit> from(String ch) {
        return Arrays.stream(Suit.values())
            .filter(suit -> suit.firstChar().equalsIgnoreCase(ch))
            .findFirst();
    }
}
