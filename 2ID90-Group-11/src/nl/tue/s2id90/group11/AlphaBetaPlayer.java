package nl.tue.s2id90.group11;

import java.util.List;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 * Alpha-beta player using iterative deepening and a smart evaluation function.
 *
 * @author Martin,Pieter
 */
public class AlphaBetaPlayer extends DraughtsPlayer {

    private final int STARTING_DEPTH = 6;
    private final int[] COST_MATRIX = {0,
        9, 9, 9, 9, 9,
        9, 8, 8, 8, 8,
        7, 7, 7, 7, 8,
        7, 6, 6, 6, 6,
        5, 5, 5, 5, 6,
        5, 4, 4, 4, 4,
        3, 3, 3, 3, 4,
        3, 2, 2, 2, 2,
        1, 1, 1, 1, 1,
        3, 3, 3, 3, 3};

    private final String name;
    private final int valueKing;
    private final EvaluationFunction evaluationFunction;

    private boolean isWhite;
    private boolean stopped = false;
    private int maxDepth;
    private int value; // Evaluation of last computed best move

    public enum EvaluationFunction {

        NR_PLACES,
        DANGEROUS,
        NR_PLACES_DANGEROUS,
    }

    public AlphaBetaPlayer(String name, int valueKing, EvaluationFunction evaluationFunction) {
        super(UninformedPlayer.class.getResource("resources/alphabeta.png"));
        this.name = name;
        this.valueKing = valueKing;
        this.evaluationFunction = evaluationFunction;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public Move getMove(DraughtsState s) {
        // Determine color of player's draughts
        if (isFirstTurn(s)) {
            this.isWhite = s.isWhiteToMove();
        }

        // Reset max depth
        this.maxDepth = this.STARTING_DEPTH;

        // First pick a random move so always a move is returned.
        Move bestMove = s.getMoves().get(0);

        boolean foundMove = false;
        try {
            // Iterative deepening     
            while (true) {
                // Create new root node
                GameNode startingNode = new GameNode(s, 1);

                // Execute alpha beta algorithm
                this.value = alphaBeta(startingNode, Integer.MIN_VALUE,
                        Integer.MAX_VALUE);

                bestMove = startingNode.getBestMove();
                System.out.println("depth: " + this.maxDepth);
                foundMove = true;
                maxDepth += 1;
            }
        } catch (AIStoppedException e) { // Player stopped by contest     
        }

        if (!foundMove) {
            System.out.println("Took random move");
        }

        return bestMove;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    /**
     * Alpha-beta algorithm with iterative deepening.
     */
    int alphaBeta(GameNode node, int alpha, int beta) throws AIStoppedException {
        if (this.stopped) {
            this.stopped = false;
            throw new AIStoppedException();
        }

        DraughtsState state = node.getGameState();

        // If there is only one move, next turn is a better representation
        if (isLeaf(node) && !containsCapture(state.getMoves())) {
            return evaluate(node);
        } else {
            for (Move move : state.getMoves()) {
                state.doMove(move);

                GameNode childNode = new GameNode(state, node.getDepth() + 1);
                int result = alphaBeta(childNode, alpha, beta);

                state.undoMove(move);

                if (this.isWhite == state.isWhiteToMove()) { // max level                
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
        }

        if (this.isWhite
                == state.isWhiteToMove()) { // max level
            return alpha;
        } else { // min level
            return beta;
        }
    }

    /**
     * Evaluate value of node.
     */
    int evaluate(GameNode node) {
        DraughtsState state = node.getGameState();

        int count = 0;
        if (this.evaluationFunction == EvaluationFunction.NR_PLACES
                || this.evaluationFunction == EvaluationFunction.NR_PLACES_DANGEROUS) {
            count += numberAndPlacesOfPieces(state);
        }

        if (this.evaluationFunction == EvaluationFunction.NR_PLACES
                || this.evaluationFunction == EvaluationFunction.NR_PLACES_DANGEROUS) {
            count += amountOfDangerousDraughts(state);
        }

        return count;
    }

    private int numberAndPlacesOfPieces(DraughtsState s) {
        int[] pieces = s.getPieces();
        int playerCorrection = this.isWhite ? 1 : -1;

        int count = 0;
        for (int i = 1; i <= 50; i++) {
            switch (pieces[i]) {
                case DraughtsState.WHITEPIECE:
                    count += COST_MATRIX[i] * playerCorrection;
                    break;
                case DraughtsState.BLACKPIECE:
                    count -= COST_MATRIX[51 - i] * playerCorrection;
                    break;
                case DraughtsState.WHITEKING:
                    count += valueKing * playerCorrection;
                    break;
                case DraughtsState.BLACKKING:
                    count -= valueKing * playerCorrection;
                    break;
            }
        }
        return count;
    }

    private int amountOfDangerousDraughts(DraughtsState s) {
        int[] pieces = s.getPieces();

        int amount = 0;
        for (int i = 1; i <= 50; i++) {
            if (isPieceOrKingOfColor(isWhite, pieces[i])) {
                //subtract
                if (i % 10 == 6) {
                    // safe, leftmost column
                } else if (1 <= i && i <= 5) {
                    // safe, topmost column
                } else if (i % 10 == 5) {
                    // save rightmost column
                } else if (46 <= i && i <= 50) {
                    // save bottommost column
                } else {
                    // not save
                    if (isPieceOrKingOfColor(!isWhite, pieces[i - 5])
                            && pieceIsEmpty(pieces[i + 6])) {
                        amount--;
                    } else if (pieceIsEmpty(pieces[i - 5])
                            && isPieceOrKingOfColor(!isWhite, pieces[i + 6])) {
                        amount--;
                    } else if (isPieceOrKingOfColor(!isWhite, pieces[i - 4])
                            && pieceIsEmpty(pieces[i + 5])) {
                        amount--;
                    } else if (pieceIsEmpty(pieces[i - 4])
                            && isPieceOrKingOfColor(!isWhite, pieces[i + 5])) {
                        amount--;
                    }
                }

            } else if (isPieceOrKingOfColor(!isWhite, pieces[i])) {
                //add
                if (i % 10 == 6) {
                    // save, leftmost column
                } else if (1 <= i && i <= 5) {
                    // save, topmost column
                } else if (i % 10 == 5) {
                    // save rightmost column
                } else if (46 <= i && i <= 50) {
                    // save bottommost column
                } else {
                    // not save
                    if (isPieceOrKingOfColor(isWhite, pieces[i - 5])
                            && pieceIsEmpty(pieces[i + 6])) {
                        amount++;
                    } else if (pieceIsEmpty(pieces[i - 5])
                            && isPieceOrKingOfColor(isWhite, pieces[i + 6])) {
                        amount++;
                    } else if (isPieceOrKingOfColor(isWhite, pieces[i - 4])
                            && pieceIsEmpty(pieces[i + 5])) {
                        amount++;
                    } else if (pieceIsEmpty(pieces[i - 4])
                            && isPieceOrKingOfColor(isWhite, pieces[i + 5])) {
                        amount++;
                    }
                }
            }
        }
        return amount;
    }

    private boolean isPieceOrKingOfColor(boolean white, int piece) {
        if (white) {
            if (piece == DraughtsState.WHITEPIECE || piece == DraughtsState.WHITEKING) {
                return true;
            }
            return false;
        } else {
            //black
            if (piece == DraughtsState.BLACKPIECE || piece == DraughtsState.BLACKKING) {
                return true;
            }
            return false;
        }
    }

    private boolean pieceIsEmpty(int piece) {
        if (piece == DraughtsState.EMPTY) {
            return true;
        }
        return false;
    }

    /*
     If it the player has white draughts, then every white piece/king should be
     counted positive.
     On the other hand, if the player does not have white draughts, i.e. it has 
     black draughts, every white piece/king should be counted negative. 
     */
    private int playerHasWhiteDraughtsInt() {
        if (this.isWhite) {
            return 1;
        }
        return -1;
    }

    /**
     * Returns whether the node is leaf of search tree.
     */
    private boolean isLeaf(GameNode node) {
        return node.getDepth() >= this.maxDepth || node.getGameState().isEndState();
    }

    /**
     * Returns whether at least one of the moves captures an opponents piece.
     */
    private boolean containsCapture(List<Move> moves) {
        for (Move move : moves) {
            if (move.isCapture()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns whether is first turn for player.
     */
    private boolean isFirstTurn(DraughtsState s) {
        int[] pieces = s.getPieces();

        // If all black pieces are still in their original place, 
        // we know at least one player still has to move.
        for (int i = 1; i <= 20; i++) {
            if (pieces[i] != DraughtsState.BLACKPIECE) {
                return false;
            }
        }

        return true;
    }
}
