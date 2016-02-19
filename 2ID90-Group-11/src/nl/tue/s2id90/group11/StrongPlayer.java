package nl.tue.s2id90.group11;

import static java.lang.Integer.max;
import static java.lang.Integer.min;
import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 *
 * @author Martin
 */
public class StrongPlayer extends DraughtsPlayer {

    private boolean isWhite;
    private boolean stopped = false;

    @Override
    public Move getMove(DraughtsState s) {
        GameNode startingNode = new GameNode(s, 1);

        // Check color
        this.isWhite = s.isWhiteToMove();

        try {
            // Execute alpha beta algorithm
            alphaBeta(startingNode, Integer.MIN_VALUE, Integer.MAX_VALUE);
        } catch (AIStoppedException e) {
            // TODO something with exception
        }

        return startingNode.getBestMove();
    }

    @Override
    public Integer getValue() {
        return 0;
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

        if (node.isLeaf()) {
            return node.evaluate(this.isWhite);
        }

        DraughtsState state = node.getGameState();

        List<Move> moves = state.getMoves();
        for (Move move : moves) {
            state.doMove(move);

            GameNode childNode = new GameNode(state, node.getDepth() + 1);

            int result = alphaBeta(childNode, alpha, beta);

            if (this.isWhite != state.isWhiteToMove()) {
                if (result > alpha) {
                    alpha = result;
                    node.setBestMove(move);
                }
                if (alpha >= beta) {
                    return beta;
                }
            } else {
                if (result < beta) {
                    beta = result;
                    node.setBestMove(move);
                }
                if (beta <= alpha) {
                    return alpha;
                }
            }

            state.undoMove(move);
        }

        if (this.isWhite == state.isWhiteToMove()) {
            return alpha;
        } else {
            return beta;
        }
    }

}
