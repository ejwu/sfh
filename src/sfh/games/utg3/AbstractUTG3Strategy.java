package sfh.games.utg3;

import sfh.Strategy;
import sfh.games.utg3.EPStrategy.IPBetIntoActions;
import sfh.games.utg3.EPStrategy.IPCheckedToActions;
import sfh.games.utg3.UTGStrategy.OOPBetActions;
import sfh.games.utg3.UTGStrategy.OOPCheckActions;

import com.google.common.collect.*;

import org.pokersource.game.*;

import java.util.Map;

// Game state, hero, villain
public abstract class AbstractUTG3Strategy<GS, H, V>
    implements Strategy<GS, H, V> {
    
    // A repreentation for all possible actions for a given hand
    public interface ActionSequence {
	// just a hack to get the enum name
	String name();
    };

    static final int FLOP = 0;
    static final int TURN = 1;
    static final int RIVER = 2;

    // Hands, actions, frequencies.  For any given hand, the frequencies of all its actions
    // should sum to 1.
    protected Table<Long, ActionSequence, Double> actions = HashBasedTable.create();

    /**
     * Return a map of each possible ActionSequence and its frequency for the given hand.
     */
    protected Map<ActionSequence, Double> getActions(long hand) {
	return actions.row(hand);
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	for (Long hand : actions.rowKeySet()) {
	    sb.append(Deck.cardMaskString(hand, "") + ": ");
	    Map<ActionSequence, Double> actionFreqs = actions.row(hand);
	    boolean printed = false;
	    for (ActionSequence action : OOPCheckActions.values()) {
		if (actionFreqs.containsKey(action)) {
		    sb.append(String.format("%3s %4.2f  ",
					    action.name(),
					    actions.row(hand).get(action)));
		    printed = true;
		}
	    }
	    if (printed) {
		sb.append("\n      ");
		printed = false;
	    }

	    for (ActionSequence action : OOPBetActions.values()) {
		if (actionFreqs.containsKey(action)) {
		    sb.append(String.format("%3s %4.2f  ",
					    action.name(),
					    actions.row(hand).get(action)));
		}
	    }

	    for (ActionSequence action : IPCheckedToActions.values()) {
		if (actionFreqs.containsKey(action)) {
		    sb.append(String.format("%3s %4.2f  ",
					    action.name(),
					    actions.row(hand).get(action)));
		    printed = true;
		}
	    }
	    if (printed) {
		sb.append("\n      ");
		printed = false;
	    }

	    for (ActionSequence action : IPBetIntoActions.values()) {
		if (actionFreqs.containsKey(action)) {
		    sb.append(String.format("%3s %4.2f  ",
					    action.name(),
					    actions.row(hand).get(action)));
		}
	    }
	    if (printed) {
		sb.append("\n");
		printed = false;
	    }
	    sb.append("\n\n");
	}
	return sb.toString();
    }
}
