package nl.tue.s2id90.group11;

import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.draughts.player.DraughtsPlayer;
import org10x10.dam.game.Move;

/**
 *
 * @author Martin
 */
public class AlphaBetaPlayer1 extends DraughtsPlayer {

    private final int STARTING_DEPTH = 8;
    private final int VALUE_KING;
    
    private boolean playerHasWhiteDraughts;
    private boolean stopped = false;
    private int maxDepth;

    // Evaluation of last computed best move
    private int value = 0;
    private EvaluationFunction evaluationFunction;
    
    public enum EvaluationFunction {
        NUMBER_OF_PIECES,
        PLACE_OF_PIECES,
        NUMBER_OF_DANGEROUS_PIECES,
        ALL
    }
    
    public AlphaBetaPlayer1(int VALUE_KING, EvaluationFunction evaluationFunction) {
        super(UninformedPlayer.class.getResource("resources/alphabeta.png"));
        this.VALUE_KING = VALUE_KING;
        this.evaluationFunction = evaluationFunction;
    }
    
    @Override
    /** generate name for player based on class name. **/
    public String getName() {
        return "AB " + VALUE_KING + " " + 
                evaluationFunction.toString();
    }
    
    @Override
    public Move getMove(DraughtsState s) {
        // determine color of player's draughts
        if (isFirstMoveForPlayer(s)) {
            this.playerHasWhiteDraughts = s.isWhiteToMove();
        }
        
        resetMaxDepth();
        
        // First pick a random move so always a move is returned.
        Move bestMove = s.getMoves().get(0);

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
                maxDepth += 1;
            }
        } catch (AIStoppedException e) { // Player stopped by competition     
        }

        if (!foundMove) {
            System.out.println("Took random move");
        }
        
        return bestMove;
    }
    
    private void resetMaxDepth(){
        this.maxDepth = this.STARTING_DEPTH;
    }

    @Override
    public Integer getValue() {
        return this.value;
    }

    @Override
    public void stop() {
        this.stopped = true;
    }

    int alphaBeta(GameNode node, int alpha, int beta) throws AIStoppedException {
        if (this.stopped) {
            this.stopped = false;
            throw new AIStoppedException();
        }

        DraughtsState state = node.getGameState();

        if (isLeaf(node)) {
            return evaluate(node.getGameState().getPieces());
        } else {
            for (Move move : state.getMoves()) {
                state.doMove(move);

                GameNode childNode = new GameNode(state, node.getDepth() + 1);
                int result = alphaBeta(childNode, alpha, beta);

                state.undoMove(move);

                if (this.playerHasWhiteDraughts == state.isWhiteToMove()) { // max level                
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
                        node.setBestMove(move);
                    }
                    if (beta <= alpha) {
                        return alpha;
                    }
                }
            }
        }

        if (this.playerHasWhiteDraughts == state.isWhiteToMove()) { // max level
            return alpha;
        } else { // min level
            return beta;
        }
    }

    /*
    Evaluate value of draughts state
    */
    int evaluate(int[] pieces) {
        int count = 0;
        
        if (this.evaluationFunction == EvaluationFunction.NUMBER_OF_PIECES ||
                this.evaluationFunction == EvaluationFunction.ALL) {
            count += numberOfPieces(pieces);
        }
        
        if(this.evaluationFunction == EvaluationFunction.PLACE_OF_PIECES ||
                this.evaluationFunction == EvaluationFunction.ALL){
            count += placeOfPieces(pieces);
        }
            
        if (evaluationFunction == EvaluationFunction.NUMBER_OF_DANGEROUS_PIECES ||
                evaluationFunction == EvaluationFunction.ALL) {
            count += amountOfDangerousDraughts(pieces);
        }
        
        return count;
    }
    
    private int numberOfPieces(int[] pieces) {
        int count = 0;
        
        for (int piece : pieces) {
            switch(piece) {
                case DraughtsState.EMPTY:
                    ; // do nothing
                    break;
                case DraughtsState.WHITEPIECE:
                    count += playerHasWhiteDraughtsInt();
                    break;
                case DraughtsState.BLACKPIECE:
                    count += playerHasWhiteDraughtsInt();
                    break;
                case DraughtsState.WHITEKING:
                    count += playerHasWhiteDraughtsInt() * VALUE_KING;
                    break;
                case DraughtsState.BLACKKING:
                    count += playerHasWhiteDraughtsInt() * -VALUE_KING;
                    break;
                case DraughtsState.WHITEFIELD:
                    ; // do nothing
                    break;
                default:
                    System.out.println("Unknown piece " + piece);
                    break;
            }
        }
        return count;
    }
    
    private int placeOfPieces(int[] pieces) {
        int count = 0;

        for (int i = 1; i <= 50; i++){
            if (playerHasWhiteDraughts) {
                if (pieces[i] == DraughtsState.WHITEPIECE) {
                    if (1 <= i && i <= 5) {
                        count += 10;
                    } else if (6 <= i && i <= 10) {
                        count += 9;
                    } else if (11 <= i && i <= 15) {
                        count += 8;
                    } else if (16 <= i && i <= 20) {
                        count += 7;
                    } else if (21 <= i && i <= 25) {
                        count += 6;
                    } else if (26 <= i && i <= 30) {
                        count += 5;
                    } else if (31 <= i && i <= 35) {
                        count += 4;
                    } else if (36 <= i && i <= 40) {
                        count += 3;
                    } else if (41 <= i && i <= 45) {
                        count += 2;
                    } else {
                        count += 1;
                    }
                }

                if (pieces[i] == DraughtsState.BLACKPIECE) {
                    if (1 <= i && i <= 5) {
                        count -= 1;
                    } else if (6 <= i && i <= 10) {
                        count -= 2;
                    } else if (11 <= i && i <= 15) {
                        count -= 3;
                    } else if (16 <= i && i <= 20) {
                        count -= 4;
                    } else if (21 <= i && i <= 25) {
                        count -= 5;
                    } else if (26 <= i && i <= 30) {
                        count -= 6;
                    } else if (31 <= i && i <= 35) {
                        count -= 7;
                    } else if (36 <= i && i <= 40) {
                        count -= 8;
                    } else if (41 <= i && i <= 45) {
                        count -= 9;
                    } else {
                        count -= 10;
                    }
                }
            } else { // player has black draughts
                if (pieces[i] == DraughtsState.BLACKPIECE) {
                    if (1 <= i && i <= 5) {
                        count += 10;
                    } else if (6 <= i && i <= 10) {
                        count += 9;
                    } else if (11 <= i && i <= 15) {
                        count += 8;
                    } else if (16 <= i && i <= 20) {
                        count += 7;
                    } else if (21 <= i && i <= 25) {
                        count += 6;
                    } else if (26 <= i && i <= 30) {
                        count += 5;
                    } else if (31 <= i && i <= 35) {
                        count += 4;
                    } else if (36 <= i && i <= 40) {
                        count += 3;
                    } else if (41 <= i && i <= 45) {
                        count += 2;
                    } else {
                        count += 1;
                    }
                }

                if (pieces[i] == DraughtsState.WHITEPIECE) {
                    if (1 <= i && i <= 5) {
                        count -= 1;
                    } else if (6 <= i && i <= 10) {
                        count -= 2;
                    } else if (11 <= i && i <= 15) {
                        count -= 3;
                    } else if (16 <= i && i <= 20) {
                        count -= 4;
                    } else if (21 <= i && i <= 25) {
                        count -= 5;
                    } else if (26 <= i && i <= 30) {
                        count -= 6;
                    } else if (31 <= i && i <= 35) {
                        count -= 7;
                    } else if (36 <= i && i <= 40) {
                        count -= 8;
                    } else if (41 <= i && i <= 45) {
                        count -= 9;
                    } else {
                        count -= 10;
                    }
                }
            }
        }
        return count;
    }
    
    private int amountOfDangerousDraughts(int[] pieces){
        int amount = 0;
        
        for (int i = 1; i <= 50; i ++) {
            if (isPieceOrKingOfColor(playerHasWhiteDraughts,pieces[i])) {
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
                    if (isPieceOrKingOfColor(!playerHasWhiteDraughts,pieces[i-5]) && 
                            pieceIsEmpty(pieces[i+6])){
                        amount--;
                    } else if (pieceIsEmpty(pieces[i-5]) &&
                            isPieceOrKingOfColor(!playerHasWhiteDraughts,pieces[i+6])) {
                        amount--;
                    } else if (isPieceOrKingOfColor(!playerHasWhiteDraughts,pieces[i-4]) && 
                            pieceIsEmpty(pieces[i+5])){
                        amount--;
                    } else if (pieceIsEmpty(pieces[i-4]) &&
                            isPieceOrKingOfColor(!playerHasWhiteDraughts,pieces[i+5])) {
                        amount--;
                    }
                }
                
            } else if (isPieceOrKingOfColor(!playerHasWhiteDraughts,pieces[i])) {
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
                    if (isPieceOrKingOfColor(playerHasWhiteDraughts,pieces[i-5]) && 
                            pieceIsEmpty(pieces[i+6])){
                        amount++;
                    } else if (pieceIsEmpty(pieces[i-5]) && 
                            isPieceOrKingOfColor(playerHasWhiteDraughts,pieces[i+6])) {
                        amount++;
                    } else if (isPieceOrKingOfColor(playerHasWhiteDraughts,pieces[i-4]) && 
                            pieceIsEmpty(pieces[i+5])){
                        amount++;
                    } else if (pieceIsEmpty(pieces[i-4]) && 
                            isPieceOrKingOfColor(playerHasWhiteDraughts,pieces[i+5])) {
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
        if (piece == DraughtsState.EMPTY){
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
        if (this.playerHasWhiteDraughts) {
            return 1;
        }
        return -1;
    }

    /*
    Returns true iff the depth is greater than the max depth or it is the endstate
    False otherwise
    */
    private boolean isLeaf(GameNode node) {
        return node.getDepth() >= this.maxDepth || node.getGameState().isEndState();
    }

    /* Checks whether all black pieces are still in original place.
    Since white starts, only after both players played their first move, this will
    evaluate to false.
    */
    private boolean isFirstMoveForPlayer(DraughtsState s) {
        int[] pieces = s.getPieces();

        for (int i = 1; i <= 20 ; i ++) {
            if (pieces[i] != DraughtsState.BLACKPIECE) {
                return false;
            }
        }
        
        return true;
    }
}


