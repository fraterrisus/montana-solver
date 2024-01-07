package com.hitchhikerprod.montana;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class Board {
    final Set<Slot> blanks = new HashSet<>();
    final Card[][] slotToCard = new Card[4][13];
    final Map<Card, Slot> cardToSlot = new HashMap<>();

    private Board() {}

    public static Board from(List<String> boardString) {
        final Board board = new Board();
        if (boardString.size() < 4)
            throw new RuntimeException("Can't parse board: not enough lines");

        final Map<Card, Boolean> fullDeck = new HashMap<>();
        for (Suit suit : Suit.values()) {
            for (int rank = 2; rank <= 13; rank++) {
                fullDeck.put(new Card(rank, suit), false);
            }
        }

        for (int row = 0; row < 4; row++) {
            final String lineString = boardString.get(row);
            final String[] slots = lineString.split("\\s+");
            if (slots.length < 13)
                throw new RuntimeException("Can't parse board: not enough cards in row " + (row + 1));
            for (int col = 0; col < 13; col++) {
                final Slot thisSlot = new Slot(row, col);
                final Optional<Card> thisCard = Card.from(slots[col]);
                if (thisCard.isPresent()) {
                    fullDeck.put(thisCard.get(), true);
                    board.putCardInSlot(thisSlot, thisCard.get());
                    board.assignSlotToCard(thisCard.get(), thisSlot);
                } else {
                    board.putBlank(thisSlot);
                }
            }
        }

        var missing = fullDeck.entrySet().stream()
            .filter(e -> !e.getValue())
            .findAny();
        if (missing.isPresent()) {
            throw new RuntimeException("Can't parse board: missing card " + missing.get().getKey());
        }

        return board;
    }

    public static Board random() {
        final Board board = new Board();

        final List<Card> deck = new ArrayList<>();
        for (Suit suit : Suit.values()) {
            for (int rank = 1; rank <= 13; rank++) {
                deck.add(new Card(rank, suit));
            }
        }
        Collections.shuffle(deck);

        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 13; col++) {
                final Card card = deck.removeFirst();
                final Slot slot = new Slot(row, col);
                if (card.rank() == 1) {
                    board.putBlank(slot);
                } else {
                    board.putCardInSlot(slot, card);
                    board.assignSlotToCard(card, slot);
                }
            }
        }
        return board;
    }

    public void putBlank(Slot slot) {
        blanks.add(slot);
        putCardInSlot(slot, null);
    }

    public void removeBlank(Slot slot) {
        blanks.remove(slot);
    }

    public void addCardToBoard(Slot slot, Card card) {
        removeBlank(slot);
        putCardInSlot(slot, card);
        assignSlotToCard(card, slot);
    }

    public void removeCardFromBoard(Card card, Slot slot) {
        putBlank(slot);
        clearCard(card);
        clearSlot(slot);
    }

    public Slot getSlotForCard(Card card) {
        return cardToSlot.get(card);
    }

    public void assignSlotToCard(Card card, Slot slot) {
        cardToSlot.put(card, slot);
    }

    public void clearCard(Card card) { cardToSlot.remove(card); }

    public Card getCardForSlot(Slot slot) {
        return slotToCard[slot.row()][slot.column()];
    }

    public void putCardInSlot(Slot slot, Card card) {
        slotToCard[slot.row()][slot.column()] = card;
    }

    public void clearSlot(Slot slot) { slotToCard[slot.row()][slot.column()] = null; }

    public void applyAction(Action move) {
        addCardToBoard(move.newSlot(), move.card());
        putBlank(move.oldSlot());
    }

    public void reverseAction(Action move) {
        addCardToBoard(move.oldSlot(), move.card());
        putBlank(move.newSlot());
    }

    public Set<Action> moves() {
        final Set<Action> moves = new HashSet<>();
        for (Slot destination : blanks) {
            final Slot left = destination.left();
            if (left == null) {
                for (Suit suit : Suit.values()) {
                    final Card two = new Card(2, suit);
                    final Slot source = getSlotForCard(two);
                    if (source.column() == 0) continue;
                    moves.add(new Action(source, two, destination));
                }
            } else {
                final Card predecessor = getCardForSlot(left);
                if (predecessor == null) continue;
                final Card candidate = predecessor.follower();
                if (candidate == null) continue;
                final Slot source = getSlotForCard(candidate);
                moves.add(new Action(source, candidate, destination));
            }
        }
        return moves;
    }

    public int score() {
        int score = 0;
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 13; col++) {
                final Slot slot = new Slot(row, col);
                final Slot leftSlot = slot.left();
                final Card card = getCardForSlot(slot);
                if (card == null) {
                    break;
                } else if (leftSlot == null) {
                    if (card.rank() == 2) score++;
                    else break;
                } else {
                    final Card leftCard = getCardForSlot(leftSlot);
                    if (leftCard == null) break;
                    if (card.suit() == leftCard.suit() && card.rank() == leftCard.rank() + 1) score++;
                    else break;
                }
            }
        }
        return score;
    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int row = 0; row < 4; row++) {
            for (int col = 0; col < 13; col++) {
                final Slot slot = new Slot(row, col);
                final Card card = getCardForSlot(slot);
                if (card == null) sb.append(".. ");
                else sb.append(card).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
