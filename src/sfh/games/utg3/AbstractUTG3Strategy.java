package sfh.games.utg3;

import sfh.Strategy;

import com.google.common.collect.*;

import java.util.Map;

public abstract class AbstractUTG3Strategy<Hero, Villain> implements Strategy<Hero, Villain> {
    // A repreentation for all possible actions for a given hand
    public interface ActionSequence {};

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

}
