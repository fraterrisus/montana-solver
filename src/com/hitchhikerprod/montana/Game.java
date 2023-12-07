package com.hitchhikerprod.montana;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Game {
    public void solve() {
        final Board board = initializeBoard();
        System.out.println(board);
        final Board solved = solve(board, 0);
        System.out.println(solved);
        final int finalScore = solved.score();
        if (finalScore == 12 * 4) {
            System.out.println("WIN!");
        } else {
            System.out.println("Lost ;(");
        }
    }

    private Board solve(Board board, int level) {
        Set<Action> possibleMoves = board.moves();
        if (possibleMoves.isEmpty()) {
            return board;
        }

        // TODO: don't duplicate Boards when there's only one possible move

        int bestScore = 0;
        Board bestBoard = null;
        for (Action move : possibleMoves) {
            //System.out.printf("[%d] Trying %s\n", level, move);
            final Board newBoard = board.copy();
            newBoard.applyAction(move);

            final String hash = newBoard.toString();
            if (visitedBoards.contains(hash)) {
                continue;
            }

            visitedBoards.add(hash);
            final Board resultBoard = solve(newBoard, level + 1);
            if (resultBoard == null) continue;
            final int resultScore = resultBoard.score();
            if (resultScore == 48) {
                return resultBoard;
            }
            if (resultScore > bestScore) {
                //System.out.printf("[%d] Best score improved to %d\n", level, resultScore);
                bestScore = resultScore;
                bestBoard = resultBoard;
            }
        }
        //System.out.printf("[%d] Returning best score %d\n", level, bestScore);
        return bestBoard;
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
                    board.putCard(slot, card);
                    board.putSlot(card, slot);
                }
            }
        }
        return board;
    }

    public static void main(String[] args) {
        new Game().solve();
    }
}
