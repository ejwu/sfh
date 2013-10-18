package sfh.games.utg3;

import sfh.Strategy;

import java.util.Map;

public class EPStrategy extends AbstractUTG3Strategy<UTG3GameState, EPStrategy, UTGStrategy> {

    // All possible strategies when in position
    public enum IPCheckedToActions implements ActionSequence {
	K, BF, BC, B3F, B3C
    }

    public enum IPBetIntoActions implements ActionSequence {
	F, C, RF, RC, R4
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
    public EPStrategy getBestResponse(UTG3GameState gs, UTGStrategy ep) {
	return null;
    }

    @Override
    public void mergeFrom(EPStrategy other, double epsilon) {

    }
}
