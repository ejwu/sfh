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

    UTGStrategy(Table<Long, ActionSequence, Double> actions) {
        super(actions);
    }

    public static UTGStrategy create(Map<Long, Double> hands) {
        Table<Long, ActionSequence, Double> actions = HashBasedTable.create();
	for (Long hand : hands.keySet()) {
	    for (OOPCheckActions action : OOPCheckActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    for (OOPBetActions action : OOPBetActions.values()) {
		actions.put(hand, action, 0.0);
	    }
	    // Default strategy for every hand is to shovel money in
	    actions.put(hand, OOPBetActions.B3C, 0.5);
	    actions.put(hand, OOPCheckActions.KR4, 0.5);
	}
        return new UTGStrategy(actions);
    }

    @Override
    public UTGStrategy getBestResponse(UTG3GameState gs, EPStrategy ep) {
	Table<Long, ActionSequence, Double> bestFreqs = HashBasedTable.create();

	for (Long hand : actions.rowKeySet()) {
            if (DEBUG) {
                System.out.println("\noptimizing " + Deck.cardMaskString(hand, ""));
            }
	    
            updateBestActionForHand(gs, hand,
                ObjectArrays.concat(OOPBetActions.values(), OOPCheckActions.values(),
                    ActionSequence.class),
                ep, bestFreqs, true);
	}
	return new UTGStrategy(normalize(bestFreqs));
    }

    @Override
    public void checkSanity() {
        checkSanity(ObjectArrays.concat(OOPBetActions.values(), OOPCheckActions.values(),
                ActionSequence.class));
    }

}
