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
        super(actions);
    }

    public static EPStrategy create(Map<Long, Double> hands) {
        Table<Long, ActionSequence, Double> actions = HashBasedTable.create();

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
        return new EPStrategy(actions);
    }

    @Override
    public EPStrategy getBestResponse(UTG3GameState gs, UTGStrategy utg) {
	Table<Long, ActionSequence, Double> bestBetFreqs = HashBasedTable.create();
	Table<Long, ActionSequence, Double> bestCheckFreqs = HashBasedTable.create();
        
	for (Long hand : actions.rowKeySet()) {
            if (DEBUG) {
                System.out.println("\noptimizing " + Deck.cardMaskString(hand, ""));
            }
	    
            updateBestActionForHand(gs, hand, IPBetIntoActions.values(), utg, bestBetFreqs, false);
            updateBestActionForHand(gs, hand, IPCheckedToActions.values(), utg, bestCheckFreqs,
                false);
	}
        bestBetFreqs = normalized(bestBetFreqs, IPBetIntoActions.values());
        bestCheckFreqs = normalized(bestCheckFreqs, IPCheckedToActions.values());
        bestBetFreqs.putAll(bestCheckFreqs);
	return new EPStrategy(bestBetFreqs);
    }

    @Override
    void normalize() {
        this.actions = normalized(actions, IPBetIntoActions.values(), IPCheckedToActions.values());
    }

    @Override
    void checkSanity() {
        checkSanity(IPBetIntoActions.values(), IPCheckedToActions.values());
    }

}
