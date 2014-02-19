package sfh.games.hulhe;

import static sfh.games.hulhe.HulheGameState.DEBUG;

import java.util.Map;
import java.util.Set;

import org.pokersource.game.Deck;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class OopStrategy extends AbstractHulheStrategy<HulheGameState, OopStrategy, IpStrategy> {

	// All possible actions when out of position on any street
	public enum OOPCheckActions implements ActionSequence {
		KF, KC, KRF, KRC, KR4
	}

	public enum OOPBetActions implements ActionSequence {
		BF, BC, B3F, B3C // bet first
	}

	OopStrategy(Table<Long, ActionSequence, Double> actions) {
		super(actions);
	}

	public static OopStrategy create(Map<Long, Double> hands) {
		Table<Long, ActionSequence, Double> actions = HashBasedTable.create();
		for (Long hand : hands.keySet()) {
			for (OOPCheckActions action : OOPCheckActions.values()) {
				actions.put(hand, action, 0.0);
			}
			for (OOPBetActions action : OOPBetActions.values()) {
				actions.put(hand, action, 0.0);
			}
			// Default strategy for every hand is to check
			actions.put(hand, OOPCheckActions.KC, 1.0);
		}
		return new OopStrategy(actions);
	}

	@Override
	public OopStrategy getBestResponse(HulheGameState gs, IpStrategy ep) {
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
		return new OopStrategy(normalized(bestFreqs, ObjectArrays.concat(
				OOPBetActions.values(), OOPCheckActions.values(), ActionSequence.class)));
	}

	@Override
	void normalize() {
		this.actions = normalized(actions, ObjectArrays.concat(
				OOPBetActions.values(), OOPCheckActions.values(), ActionSequence.class));
	}

	@Override
	public void checkSanity() {
		checkSanity(ObjectArrays.concat(OOPBetActions.values(), OOPCheckActions.values(),
				ActionSequence.class));
	}
	
    @Override
    Set<ActionSequence> getValidActions() {
    	return Sets.newHashSet(ObjectArrays.concat(OOPBetActions.values(), OOPCheckActions.values(), ActionSequence.class));
    }
}
