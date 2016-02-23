/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.tue.s2id90.group11;

import net.xeoh.plugins.base.annotations.PluginImplementation;
import nl.tue.s2id90.draughts.DraughtsPlayerProvider;
import nl.tue.s2id90.draughts.DraughtsPlugin;



/**
 *
 * @author huub
 */
@PluginImplementation
public class MyDraughtsPlugin extends DraughtsPlayerProvider implements DraughtsPlugin {
    public MyDraughtsPlugin() {
        // make two players available to the AICompetition tool
        // During the final competition you should make only your 
        // best player available. For testing it might be handy
        // to make more than one player available.
        super(new OptimisticPlayer(), 
                new AlphaBetaPlayer1(4, AlphaBetaPlayer1.EvaluationFunction.NR_OF_PIECES), 
                new AlphaBetaPlayer1(10, AlphaBetaPlayer1.EvaluationFunction.PLACE_OF_PIECES),
                new AlphaBetaPlayer1(10, AlphaBetaPlayer1.EvaluationFunction.NR_OF_DANGEROUS_PIECES),
                new AlphaBetaPlayer1(10, AlphaBetaPlayer1.EvaluationFunction.ALL));
    }
}
