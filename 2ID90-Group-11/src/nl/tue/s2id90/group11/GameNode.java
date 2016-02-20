package nl.tue.s2id90.group11;

import nl.tue.s2id90.draughts.DraughtsState;
import org10x10.dam.game.Move;

/**
 * Node in decision tree.
 * @author Martin
 */
public class GameNode {
    private DraughtsState state;
    private Move bestMove;
    private int depth;    

    public GameNode(DraughtsState s, int d){
        this.state = s;
        this.depth = d;
    }

    public DraughtsState getGameState() {
        return this.state;
    }

    public Move getBestMove() {
        return this.bestMove;
    }

    public void setBestMove(Move m) {
        this.bestMove = m;
    }    
    
    public int getDepth() {
        return this.depth;
    }
}