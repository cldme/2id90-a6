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
        super(
                // TOURNAMENT PLAYER
                // new AlphaBetaPlayer("AlphaBetaPlayer-Group11", 30, AlphaBetaPlayer.EvaluationFunction.NR_PLACES_DANGEROUS, true)
                // DEFAULT PLAYERS
                // new OptimisticPlayer(), 
                // new StupidPlayer(),
                // new UninformedPlayer(),
                // TEST PLAYERS
                new AlphaBetaPlayer("checkCaptures", 30, AlphaBetaPlayer.EvaluationFunction.NR_PLACES, true),
                new AlphaBetaPlayer("checkCapturesDangerous", 30, AlphaBetaPlayer.EvaluationFunction.NR_PLACES_DANGEROUS, true),
                new AlphaBetaPlayer("noCheckCaptures", 30, AlphaBetaPlayer.EvaluationFunction.NR_PLACES, false),
                new AlphaBetaPlayer("noCheckCapturesDangerous", 30, AlphaBetaPlayer.EvaluationFunction.NR_PLACES_DANGEROUS, false)
        );
    }
}
