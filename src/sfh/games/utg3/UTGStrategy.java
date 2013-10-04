package sfh.games.utg3;

import sfh.Strategy;
import sfh.games.utg3.AbstractUTG3Strategy.ActionSequence;

import java.util.Map;

public class UTGStrategy extends AbstractUTG3Strategy<UTGStrategy, EPStrategy> {

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

    @Override
    public UTGStrategy getBestResponse(EPStrategy ep) {
	return null;
    }

    @Override
    public void mergeFrom(UTGStrategy other, double epsilon) {

    }
}
