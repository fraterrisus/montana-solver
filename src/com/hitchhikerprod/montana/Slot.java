package com.hitchhikerprod.montana;

public record Slot(int row, int column) {
    public Slot left() {
        int newColumn = column - 1;
        if (newColumn < 0) return null;
        else return new Slot(row, newColumn);
    }

    public Slot copy() {
        return new Slot(this.row, this.column);
    }
}
