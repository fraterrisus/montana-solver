package com.hitchhikerprod.montana;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Game {
    public void solve() {
        final Board board = initializeBoard();
        for (int round = 0; round < 3; round++) {
            System.out.println(MessageFormat.format("*** Round {0}", round + 1));
            System.out.println(board);
            final Score solution = solve(board, 0);
            for (Action move : solution.steps()) {
                System.out.println(move);
                board.applyAction(move);
            }
            System.out.println("\n" + board);
            if (solution.score() == 12 * 4) {
                System.out.println("Won!");
                return;
            }
            shuffleUnsetCards(board);
        }
        System.out.println("Lost :(");
/*
        for (Action move : solution.steps()) {
            System.out.println(move);
            solved.applyAction(move);

            for (int row = 0; row < 4; row++) {
                for (int col = 0; col < 13; col++) {
                    if (move.newSlot().row() == row && move.newSlot().column() == col) System.out.print(">");
                    else System.out.print(" ");

                    final Card thisCard = solved.getCard(new Slot(row, col));
                    System.out.print(thisCard == null ? ".." : thisCard);
                }
                System.out.println();
            }
        }
*/
    }

    private Score solve(Board board, int level) {
        final Set<Action> possibleMoves = board.moves();
        if (possibleMoves.isEmpty()) {
            return new Score(board.score(), new LinkedList<>());
        }
        Score best = new Score(-1, new LinkedList<>());
        for (Action move : possibleMoves) {
            board.applyAction(move);

            final String hash = board.toString();
            if (visitedBoards.contains(hash)) {
                board.reverseAction(move);
                continue;
            } else {
                visitedBoards.add(hash);
            }

            final Score newScore = solve(board, level + 1);
            board.reverseAction(move);

            if (newScore.score() == 48) {
                newScore.steps().addFirst(move);
                return newScore;
            } else if (newScore.score() > best.score()) {
                best = newScore;
                best.steps().addFirst(move);
            }
        }
        return best;
    }

    Set<String> visitedBoards = new HashSet<>();

    private Board initializeBoard() {
        final Board board = new Board();

        List<Card> deck = new ArrayList<>();
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

    private void shuffleUnsetCards(Board board) {
        // System.out.println("Before:\n" + board);

        List<Card> deck = new ArrayList<>();
        List<Integer> startOfDeal = new ArrayList<>();
        for (int row = 0; row < 4; row++) {
            int start = 0;
            for (int col = 0; col < 13; col++) {
                final Slot thisSlot = new Slot(row, col);
                final Card thisCard = board.getCardForSlot(thisSlot);
                if (start == col && thisCard != null && thisCard.rank() == col + 2) {
                    start++;
                } else {
                    if (thisCard != null) {
                        deck.add(thisCard);
                        board.removeCardFromBoard(thisCard, thisSlot);
                    }
                }
            }
            startOfDeal.add(start);
        }

        // System.out.println("Clean:\n" + board);

        Collections.shuffle(deck);
        // System.out.println("Deck: " +
        //    String.join(" ", deck.stream().map(Card::toString).toList()));

        for (int row = 0; row < 4; row++) {
            final int start = startOfDeal.get(row);
            for (int col = 0; col < 13; col++) {
                if (col <= start) continue;
                final Card card = deck.removeFirst();
                final Slot slot = new Slot(row, col);
                board.putCardInSlot(slot, card);
                board.assignSlotToCard(card, slot);
            }
        }

        // System.out.println("Done:\n" + board);
        // assert(deck.isEmpty());
    }

    public static void main(String[] args) {
        new Game().solve();
    }
}
