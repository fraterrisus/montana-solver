package com.hitchhikerprod.montana;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    final Set<Slot> blanks = new HashSet<>();
    final Card[][] slotToCard = new Card[4][13];
    final Map<Card, Slot> cardToSlot = new HashMap<>();

    public void putBlank(Slot slot) {
        blanks.add(slot);
        putCardInSlot(slot, null);
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
        blanks.remove(move.newSlot());
        putCardInSlot(move.newSlot(), move.card());
        putBlank(move.oldSlot());
        assignSlotToCard(move.card(), move.newSlot());
    }

    public void reverseAction(Action move) {
        blanks.remove(move.oldSlot());
        putCardInSlot(move.oldSlot(), move.card());
        putBlank(move.newSlot());
        assignSlotToCard(move.card(), move.oldSlot());
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
