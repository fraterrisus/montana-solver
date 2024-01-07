# montana-solver
A solver for the solitare game Montana, also known as Gaps.

## Rules

Shuffle the cards and deal out all fifty-two in four rows of thirteen, then remove the Aces (leaving four empty spaces).

Legal moves involve picking up a card and placing it in a currently-empty space immediately to the right of its
predecessor of the same suit. For example, if there is an empty space to the right of the Three of Hearts (3H), the 
Four of Hearts (4H) may be picked up and placed next to the 3H, leaving an empty space where the 4H once was.

Empty spaces in the first column may be filled by any Two that isn't already in the first column. No card can be placed
to the right of a King, or to the right of an empty space.

The goal is to sort all the cards by suit and rank, with the Two in the first column and the King in the twelfth column
(the thirteenth column will remain empty). It doesn't matter which suit is in which row.

If there are no more moves, you may reshuffle twice. Lock down every Two that is in the first column, plus every
**consecutive** card of the same suit to the right of it. For example, if a column starts with 2C 3C 7H 8D 6C, the 2C
and 3C are locked down. The 6C is ostensibly in the right place, but it cannot be locked because of the intervening
incorrect cards. Collect all cards that are not locked down, shuffle, and deal again.

In some variants, the Aces are shuffled back into the deck, dealt into the tableau, and removed in order to randomly
place new gaps during the deal. In other variants (including the one that this solver plays), the gaps are always placed
after the last locked-down card in each row.

## Design notes

The solver uses a fairly straightforward [depth-first search](https://en.wikipedia.org/wiki/Depth-first_search). It is
quite easy to arrive at the same game state through multiple paths, so the solver attempts to keep a "hash" of every
position it has examined and refuses to explore the same subtree twice. This means that the final state of each "round"
is optimal in terms of the scoring function, but there is no guarantee that the sequence of moves that arrives at that
state is the fastest.

In order to reduce object creation and garbage collection, a single copy of the board state is maintained as we walk
up and down the solution tree. It is easy enough to "undo" a move, so long as we keep track of both the source and the
destination as well as which card we moved. As we proceed down the tree we make additional moves, skipping over board
states that we've seen before, until we arrive at a leaf node where no more moves can be made. When we reach a leaf, we
calculate the board's score and return. Then we retreat back up the tree, building a list of moves that were made in
reverse order. After all child solutions have been evaluated, we return the best one to our parent. When we arrive at
the root of the tree the board is in a pristine state (because we've undone all of the moves) but we have a list of
moves, which can be replayed so they can be displayed to the user. The solver then picks up all the not-locked-down
cards, shuffles, and proceeds to the next round.

The scoring function is simply a count of the number of "locked down" cards; if it reaches 12 * 4 = 48, a win state
has been found and the algorithm short-circuits. It's possible that a more nuanced scoring function might be able to
discover better intermediate non-win states; anecdotally, when I am solving this game by hand I find it isn't always
better to finish one row 2-K and leave another row mostly unfinished (2-3 or 2-5 or some such). However, as it stands
this solver rarely makes it past a second deal, so the scoring function seems to be "good enough".

I'm not sure if there's a way to prune the search space; the ultimate score of a solution branch is, I think, 
unpredictable until we reach the leaf node at the bottom of the branch, so algorithms that rely on estimates of
the value of the solution (or, equivalently, the "distance" from here to there) aren't likely to be useful. In the years
I've been playing this game I have yet to come up with heuristics that consistently steer me towards a win, so I'd be 
hard-pressed to write code to do so. As a result, the runtime of the solver is highly variable, depending on whether a
win can be achieved from the current board state and in what order we happen to traverse the search tree.