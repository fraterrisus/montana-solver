package com.hitchhikerprod.montana;

public record Slot(int row, int column) {
    public Slot left() {
        int newColumn = column - 1;
        if (newColumn < 0) return null;
        else return new Slot(row, newColumn);
    }

    public String toString() { return String.format("(%d,%d)", row, column); }
}
