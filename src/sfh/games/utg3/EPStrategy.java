package sfh.games.utg3;

import sfh.Strategy;
import static sfh.games.utg3.UTG3GameState.DEBUG;

import com.google.common.collect.*;

import java.util.Map;

import org.pokersource.game.*;

public class EPStrategy extends AbstractUTG3Strategy<UTG3GameState, EPStrategy, UTGStrategy> {

    // All possible strategies when in position
    public enum IPCheckedToActions implements ActionSequence {
	K, BF, BC, B3F, B3C
    }

    public enum IPBetIntoActions implements ActionSequence {
	F, C, RF, RC, R4
    }			     

    EPStrategy(Table<Long, ActionSequence, Double> actions) {
	this.actions.putAll(actions);
    }

    public EPStrategy(Map<Long, Double> hands) {
	for (Long hand : hands.keySet()) {
	    for (IPCheckedToActions action : IPCheckedToActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    for (IPBetIntoActions action : IPBetIntoActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    // Default strategy for every hand is to shovel money in
	    actions.put(hand, IPCheckedToActions.B3C, 1.0);
	    actions.put(hand, IPBetIntoActions.R4, 1.0);
	}
    }

    @Override
    public EPStrategy getBestResponse(UTG3GameState gs, UTGStrategy utg) {
	Table<Long, ActionSequence, Double> bestFreqs = HashBasedTable.create();

	for (Long hand : actions.rowKeySet()) {
            if (DEBUG) {
                System.out.println("\noptimizing " + Deck.cardMaskString(hand, ""));
            }
	    
            updateBestActionForHand(gs, hand, IPBetIntoActions.values(), utg, bestFreqs, false);
            updateBestActionForHand(gs, hand, IPCheckedToActions.values(), utg, bestFreqs, false);
	}
	return new EPStrategy(bestFreqs);
    }

}
