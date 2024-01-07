package com.hitchhikerprod.montana;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class Game {
    private static final boolean DEBUG_SHUFFLE = false;

    final Board board;
    final Set<String> visitedBoards = new HashSet<>();

    public Game() {
        this.board = Board.random();
    }

    public Game(List<String> boardData) {
        this.board = Board.from(boardData);
    }

    public void solve() {
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

    private static void debugLog(String debugString) {
        if (DEBUG_SHUFFLE) System.out.println(debugString);
    }

    private void shuffleUnsetCards(Board board) {
        debugLog("Before:\n" + board);

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

        debugLog("Clean:\n" + board);
        debugLog("Deck: " + String.join(" ", deck.stream().map(Card::toString).toList()));

        Collections.shuffle(deck);

        for (int row = 0; row < 4; row++) {
            final int start = startOfDeal.get(row);
            for (int col = 0; col < 13; col++) {
                if (col <= start) continue;
                final Card card = deck.removeFirst();
                final Slot slot = new Slot(row, col);
                board.addCardToBoard(slot, card);
            }
        }

        debugLog("Done:\n" + board);
        assert(deck.isEmpty());
    }

    public static void main(String[] args) {
        final Game game = (args.length > 0) ? new Game(readGameFromFile(args[0])) : new Game();
        game.solve();
    }

    private static List<String> readGameFromFile(String filename) {
        try (final FileReader fileReader = new FileReader(filename)) {
            final BufferedReader bufferedReader = new BufferedReader(fileReader);
            return bufferedReader.lines().toList();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
