package sfh.games.utg3;

import sfh.Strategy;
import sfh.games.utg3.EPStrategy.IPBetIntoActions;
import sfh.games.utg3.EPStrategy.IPCheckedToActions;
import sfh.games.utg3.UTGStrategy.OOPBetActions;
import sfh.games.utg3.UTGStrategy.OOPCheckActions;
import static sfh.games.utg3.UTG3GameState.DEBUG;

import com.google.common.collect.*;

import org.pokersource.game.*;

import java.util.Map;

// Game state, hero, villain
public abstract class AbstractUTG3Strategy<
    GS, H extends AbstractUTG3Strategy, V extends AbstractUTG3Strategy>
    implements Strategy<GS, H, V> {
    
    // A representation for all possible actions for a given hand
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

    // update bestFreqs
    protected void updateBestActionForHand(UTG3GameState gs, long hand, 
        ActionSequence[] possibleActions, AbstractUTG3Strategy villain,
        Table<Long, ActionSequence, Double> bestFreqs, boolean isUTG) {

        double bestValue = -100000000000.0d;
        ActionSequence bestAction = null;

        Table<Long, ActionSequence, Double> tempFreqs = HashBasedTable.create();
        // just for debugging
        Map<ActionSequence, Double> valueList = Maps.newLinkedHashMap();

        for (ActionSequence action : possibleActions) {
            tempFreqs.clear();
            tempFreqs.put(hand, action, 1.0);

            Double value = null;
            if (isUTG) {
                UTGStrategy pure = new UTGStrategy(tempFreqs);
                EPStrategy ep = (EPStrategy) villain;
                value = gs.getValue(pure, ep);
            } else {
                EPStrategy pure = new EPStrategy(tempFreqs);
                UTGStrategy utg = (UTGStrategy) villain;
                // getValue returns UTG's EV, so we reverse it
                value = -1 * gs.getValue(utg, pure);
            }
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

    @Override
    public void mergeFrom(H other, double epsilon) {
        this.actions.clear();
        this.actions.putAll(other.actions);
        // TODO: use epsilon to actually merge, instead of just copy
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
