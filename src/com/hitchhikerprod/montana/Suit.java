package com.hitchhikerprod.montana;

public enum Suit {
    CLUBS, DIAMONDS, HEARTS, SPADES;

    public String firstChar() {
        return name().substring(0,1);
    }
}
