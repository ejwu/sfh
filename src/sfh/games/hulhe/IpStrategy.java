package sfh.games.hulhe;

import sfh.Strategy;
import static sfh.games.hulhe.HulheGameState.DEBUG;

import com.google.common.collect.*;

import java.util.Map;

import org.pokersource.game.*;

public class IpStrategy extends AbstractHulheStrategy<HulheGameState, IpStrategy, OopStrategy> {

    // All possible strategies when in position
    public enum IPCheckedToActions implements ActionSequence {
	K, BF, BC, B3F, B3C
    }

    public enum IPBetIntoActions implements ActionSequence {
	F, C, RF, RC, R4
    }			     

    IpStrategy(Table<Long, ActionSequence, Double> actions) {
        super(actions);
    }

    public static IpStrategy create(Map<Long, Double> hands) {
        Table<Long, ActionSequence, Double> actions = HashBasedTable.create();

	for (Long hand : hands.keySet()) {
	    for (IPCheckedToActions action : IPCheckedToActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    for (IPBetIntoActions action : IPBetIntoActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    // Default strategy for every hand is to check
	    actions.put(hand, IPCheckedToActions.K, 1.0);
	    actions.put(hand, IPBetIntoActions.C, 1.0);
	}
        return new IpStrategy(actions);
    }

    @Override
    public IpStrategy getBestResponse(HulheGameState gs, OopStrategy utg) {
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
	return new IpStrategy(bestBetFreqs);
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
