/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group11;

import java.util.ArrayList;
import java.util.Collections;
import nl.tue.s2id90.draughts.DraughtsState;
import nl.tue.s2id90.game.GameState;
import org10x10.dam.game.Move;
import java.util.Arrays;

/**
 *
 * @author Martin
 */
public class GameNode {

    private DraughtsState state;
    private Move bestMove;
    private int depth;

    private final int MAX_DEPTH = 10;

    public GameNode(DraughtsState s, int d) {
        this.state = s;
        this.depth = d;
    }

    DraughtsState getGameState() {
        return this.state;
    }

    void setBestMove(Move m) {
        this.bestMove = m;
    }

    Move getBestMove() {
        return this.bestMove;
    }

    boolean isLeaf() {
        return this.state.isEndState() || this.depth >= this.MAX_DEPTH;
    }

    int evaluate(boolean isWhite) {
        int count = 0;
        for (int piece : this.state.getPieces()) {
            if (piece == 1) {
                if (isWhite) {
                    count += 1;
                } else {
                    count -= 1;
                }
            } else if (piece == 2) {
                if (isWhite) {
                    count -= 1;
                } else {
                    count += 1;
                }
            } else if (piece == 3) {
                if (isWhite) {
                    count += 2;
                } else {
                    count -= 2;
                }
            } else if (piece == 4) {
                if (isWhite) {
                    count -= 2;
                } else {
                    count += 2;
                }
            }
        }
        return count;
    }

    int getDepth() {
        return this.depth;
    }

}
