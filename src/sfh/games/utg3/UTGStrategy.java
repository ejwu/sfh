package sfh.games.utg3;

import sfh.Strategy;
import sfh.games.utg3.AbstractUTG3Strategy.ActionSequence;
import static sfh.games.utg3.UTG3GameState.DEBUG;

import com.google.common.collect.*;

import org.pokersource.game.*;

import java.util.Map;

public class UTGStrategy extends AbstractUTG3Strategy<UTG3GameState, UTGStrategy, EPStrategy> {

    // All possible actions when out of position on any street
    public enum OOPCheckActions implements ActionSequence {
	KF, KC, KRF, KRC, KR4
    }

    public enum OOPBetActions implements ActionSequence {
	BF, BC, B3F, B3C // bet first
    }

    public UTGStrategy(Map<Long, Double> hands) {
	for (Long hand : hands.keySet()) {
	    for (OOPCheckActions action : OOPCheckActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    for (OOPBetActions action : OOPBetActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    // Default strategy for every hand is to shovel money in
	    actions.put(hand, OOPBetActions.B3C, 0.5);
	    actions.put(hand, OOPCheckActions.KF, 0.5);
	}
    }

    private UTGStrategy(Table<Long, ActionSequence, Double> actions) {
	this.actions.putAll(actions);
    }

    @Override
    public UTGStrategy getBestResponse(UTG3GameState gs, EPStrategy ep) {
	Table<Long, ActionSequence, Double> bestFreqs = HashBasedTable.create();

	for (Long hand : actions.rowKeySet()) {
            if (DEBUG) {
                System.out.println("\noptimizing " + Deck.cardMaskString(hand, ""));
            }
	    double bestValue = -1.0000000000d;
	    ActionSequence bestAction = null;
	    Table<Long, ActionSequence, Double> tempFreqs = HashBasedTable.create();

	    Map<ActionSequence, Double> valueList = Maps.newLinkedHashMap();
	    
	    for (ActionSequence action : OOPCheckActions.values()) {
		tempFreqs.clear();
		tempFreqs.put(hand, action, 1.0);
		UTGStrategy pure = new UTGStrategy(tempFreqs);
		
		double value = gs.getValue(pure, ep);
                if (DEBUG) {
                    System.out.println("Pure " + action.name() + ": " + value);
                }
		valueList.put(action, value);
		if (value > bestValue) {
		    bestValue = value;
		    bestAction = action;
		}
	    }


            if (DEBUG) {
                System.out.println("\nBest for " + Deck.cardMaskString(hand, "") + ": " +
                    bestAction.name() + " " + bestValue);
                System.out.println("All: " + valueList);
            }
	    if (bestAction == null) {
		throw new IllegalStateException("No action");
	    }
            // TODO: Might be nice to mix between equivalent strategies.
	    bestFreqs.put(hand, bestAction, 1.0);
	}
	return new UTGStrategy(bestFreqs);
    }

    @Override
    public void mergeFrom(UTGStrategy other, double epsilon) {
        this.actions.clear();
        this.actions.putAll(other.actions);
    }

}
