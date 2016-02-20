package nl.tue.s2id90.group11;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 *
 * @author Martin
 */
public class AlphaBetaPlayer1 extends DraughtsPlayer {

    private final int STARTING_DEPTH = 10;

    private boolean isWhite;
    private boolean stopped = false;
    private int maxDepth;

    // Evaluation of last computed best move
    private int value = 0;

    // Possible nodes for next turn
    private List<GameNode> nextNodes = new ArrayList();

    @Override
    public Move getMove(DraughtsState s) {
        // Only in start of game:
        if (isStartingState(s)) {
            this.maxDepth = this.STARTING_DEPTH;
            this.isWhite = s.isWhiteToMove();
        }

        // First pick a random move so always a move is returned.
        Move bestMove = s.getMoves().get(0);

        // If next nodes already have been computed, you can already determine
        // best move.
        for (GameNode node : this.nextNodes) {
            if (Arrays.equals(node.getGameState().getPieces(), s.getPieces())) {
                bestMove = node.getBestMove();
                System.out.println("Took precomputed move");
                this.maxDepth++;
                break;
            }
        }
        this.nextNodes.clear();

        boolean foundMove = false;
        try {
            // Iterative Deepening     
            while (true) {
                System.out.println("depth: " + this.maxDepth);
                GameNode startingNode = new GameNode(s, 1);

                // Execute alpha beta algorithm
                this.value = alphaBeta(startingNode, Integer.MIN_VALUE,
                        Integer.MAX_VALUE);

                bestMove = startingNode.getBestMove();
                foundMove = true;
                this.maxDepth++;
            }
        } catch (AIStoppedException e) { // Player stopped by competition     
        }

        this.maxDepth--;        
        if (!foundMove) {
            System.out.println("Took random move");
            // If no move was found decrement depth again
            this.maxDepth--;
        }
        
        return bestMove;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public void stop() {
        stopped = true;
    }

    int alphaBeta(GameNode node, int alpha, int beta) throws AIStoppedException {
        if (this.stopped) {
            this.stopped = false;
            throw new AIStoppedException();
        }

        DraughtsState state = node.getGameState();

        // If there is only one move, next turn is a better representation
        if (isLeaf(node) && state.getMoves().size() != 1) {
            return evaluate(node);
        }

        for (Move move : state.getMoves()) {
            state.doMove(move);

            GameNode childNode = new GameNode(state, node.getDepth() + 1);

            int result = alphaBeta(childNode, alpha, beta);

            state.undoMove(move);

            if (this.isWhite == state.isWhiteToMove()) { // max level                
                // Keep nodes on depth of 3.
                if (node.getDepth() == 3) {
                    this.nextNodes.add(childNode);
                }

                if (result > alpha) {
                    alpha = result;
                    node.setBestMove(move);
                }
                if (alpha >= beta) {
                    return beta;
                }
            } else { // min level
                if (result < beta) {
                    beta = result;
                }
                if (beta <= alpha) {
                    return alpha;
                }
            }
        }

        if (this.isWhite == state.isWhiteToMove()) {
            return alpha;
        } else {
            return beta;
        }
    }

    int evaluate(GameNode node) {
        DraughtsState state = node.getGameState();
        int count = 0;
        for (int piece : state.getPieces()) {
            if (piece == 1) {
                if (this.isWhite) {
                    count += 1;
                } else {
                    count -= 1;
                }
            } else if (piece == 2) {
                if (this.isWhite) {
                    count -= 1;
                } else {
                    count += 1;
                }
            } else if (piece == 3) {
                if (this.isWhite) {
                    count += 2;
                } else {
                    count -= 2;
                }
            } else if (piece == 4) {
                if (this.isWhite) {
                    count -= 2;
                } else {
                    count += 2;
                }
            }
        }
        return count;
    }

    private boolean isLeaf(GameNode node) {
        return node.getDepth() >= this.maxDepth || node.getGameState().isEndState();
    }

    private boolean isStartingState(DraughtsState s) {
        int[] pieces = s.getPieces();
        // Checks that all black dams are in their starting state.
        for (int i = 1; i < 20; i++) {
            if (pieces[i] != 2) {
                return false;
            }
        }
        return true;
    }
}
