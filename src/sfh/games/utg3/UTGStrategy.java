package sfh.games.utg3;

import sfh.Strategy;

import java.util.Map;

public class UTGStrategy extends AbstractUTG3Strategy<UTGStrategy, EPStrategy> {

    // All possible actions when out of position on any street
    public enum OOPActions implements AbstractUTG3Strategy.ActionSequence {
	KF, KC, KRF, KRC, KR4, // check first
	BF, BC, B3F, B3C // bet first
    }

    public UTGStrategy(Map<Long, Double> hands) {
	for (Long hand : hands.keySet()) {
	    for (OOPActions action : OOPActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    // Default strategy for every hand is to shovel money in
	    actions.put(hand, OOPActions.B3C, 1.0);
	}
    }

    @Override
    public UTGStrategy getBestResponse(EPStrategy ep) {
	return null;
    }

    @Override
    public void mergeFrom(UTGStrategy other, double epsilon) {

    }
}
