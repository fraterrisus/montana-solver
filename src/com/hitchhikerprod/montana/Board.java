package com.hitchhikerprod.montana;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Board {
    final Set<Slot> blanks = new HashSet<>();
    final Card[][] slotToCard = new Card[4][13];
    final Map<Card, Slot> cardToSlot = new HashMap<>();

    public Board copy() {
        final Board copy = new Board();
        copy.blanks.addAll(this.blanks);
        for (int row = 0; row < 4; row++) {
            System.arraycopy(this.slotToCard[row], 0, copy.slotToCard[row], 0, 13);
        }
        copy.cardToSlot.putAll(this.cardToSlot);
        return copy;
    }

    public void putBlank(Slot slot) {
        blanks.add(slot);
        putCard(slot, null);
    }

    public Slot getSlot(Card card) {
        return cardToSlot.get(card);
    }

    public void putSlot(Card card, Slot slot) {
        cardToSlot.put(card, slot);
    }

    public Card getCard(Slot slot) {
        return slotToCard[slot.row()][slot.column()];
    }

    public void putCard(Slot slot, Card card) {
        slotToCard[slot.row()][slot.column()] = card;
    }

    public void moveCardTo(Slot newSlot, Card card) {
        final Slot oldSlot = getSlot(card);
        blanks.remove(newSlot);
        putCard(newSlot, card);
        putBlank(oldSlot);
        putSlot(card, newSlot);
    }

    public Set<Action> moves() {
        final Set<Action> moves = new HashSet<>();
        for (Slot slot : blanks) {
            final Slot left = slot.left();
            if (left == null) {
                for (Suit suit : Suit.values()) {
                    final Card two = new Card(2, suit);
                    final Slot where = getSlot(two);
                    if (where.column() == 0) continue;
                    moves.add(new Action(two, slot));
                }
            } else {
                final Card predecessor = getCard(left);
                if (predecessor == null) continue;
                final Card candidate = predecessor.follower();
                if (candidate == null) continue;
                moves.add(new Action(candidate, slot));
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
                final Card card = getCard(slot);
                if (card == null) {
                    break;
                } else if (leftSlot == null) {
                    if (card.rank() == 2) score++;
                    else break;
                } else {
                    final Card leftCard = getCard(leftSlot);
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
                final Card card = getCard(slot);
                if (card == null) sb.append(".. ");
                else sb.append(card).append(" ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
