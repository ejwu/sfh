package sfh.games.hulhe;

import sfh.Strategy;
import sfh.games.hulhe.IpStrategy.IPBetIntoActions;
import sfh.games.hulhe.IpStrategy.IPCheckedToActions;
import sfh.games.hulhe.OopStrategy.OOPBetActions;
import sfh.games.hulhe.OopStrategy.OOPCheckActions;
import static sfh.games.hulhe.HulheGameState.DEBUG;

import com.google.common.collect.*;

import org.pokersource.game.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

// Game state, hero, villain
public abstract class AbstractHulheStrategy<
    GS, H extends AbstractHulheStrategy, V extends AbstractHulheStrategy>
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

    protected AbstractHulheStrategy(Table<Long, ActionSequence, Double> actions) {
        this.actions.putAll(actions);
        checkSanity();
    }


    /**
     * Return a map of each possible ActionSequence and its frequency for the given hand.
     */
    protected Map<ActionSequence, Double> getActions(long hand) {
    	return actions.row(hand);
    }

    public static Map<Long, Double> createEqualFrequencies(String... hands) {
        Map<Long, Double> frequencies = Maps.newLinkedHashMap();
        for (String hand : hands) {
            if (Long.bitCount(Deck.parseCardMask(hand)) != 2) {
                throw new IllegalArgumentException(hand + " is not a valid hand");
            }
            if (frequencies.containsKey(Deck.parseCardMask(hand))) {
                throw new IllegalArgumentException(hand + " is repeated");
            }

            frequencies.put(Deck.parseCardMask(hand), 1.0 / hands.length);
        }

        return frequencies;
    }

    // update bestFreqs
    protected void updateBestActionForHand(HulheGameState gs, long hand, 
        ActionSequence[] possibleActions, AbstractHulheStrategy villain,
        Table<Long, ActionSequence, Double> bestFreqs, boolean isOop) {

        if (DEBUG) {
            System.out.println("\n\n\nbest action for " + Deck.cardMaskString(hand));
        }

        double bestValue = -100000000000.0d;
        ActionSequence bestAction = null;

        Table<Long, ActionSequence, Double> tempFreqs = HashBasedTable.create();
        // just for debugging
        Map<ActionSequence, Double> valueList = Maps.newLinkedHashMap();

        for (ActionSequence action : possibleActions) {
            tempFreqs.clear();
            tempFreqs.put(hand, action, 1.0);

            Double value = null;
            if (isOop) {
                OopStrategy pure = new OopStrategy(tempFreqs);
                IpStrategy ep = (IpStrategy) villain;
                value = gs.getValue(pure, ep, true);
            } else {
                // TODO: is this even sane?  do the UTG and EP strategies match properly?
                // TODO: this is a hack to make checkSanity work, do something smarter
                if (Arrays.asList(possibleActions).contains(IPBetIntoActions.C)) {
                    tempFreqs.put(hand, IPCheckedToActions.K, 1.0);
                } else {
                    tempFreqs.put(hand, IPBetIntoActions.C, 1.0);
                }
                IpStrategy pure = new IpStrategy(tempFreqs);
                OopStrategy utg = (OopStrategy) villain;
                // getValue returns UTG's EV, so we reverse it
                value = -1 * gs.getValue(utg, pure, true);
            }
            if (DEBUG) {
                System.out.println("\nPure " + action.name() + " for " + Deck.cardMaskString(hand) +
                    ": " + value);
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
        Table<Long, ActionSequence, Double> newFreqs = HashBasedTable.create();

        for (long hand : actions.rowKeySet()) {
            Map<ActionSequence, Double> actionFreqs = other.getActions(hand);
            // First update everything that has a new frequency
            for (ActionSequence otherAction : actionFreqs.keySet()) {
                double oldFreq = 0.0;
                if (actions.row(hand).containsKey(otherAction)) {
                    oldFreq = actions.row(hand).get(otherAction);
                }
                double newFreq = actionFreqs.get(otherAction);
                newFreqs.put(hand, otherAction, oldFreq + ((newFreq - oldFreq) * epsilon));
            }
            // Anything not already updated must have an update frequency of 0
            for (ActionSequence action : actions.row(hand).keySet()) {
                if (!newFreqs.row(hand).containsKey(action)) {
                    newFreqs.put(hand, action, actions.row(hand).get(action) * (1 - epsilon));
                }
            }
        }
        if (DEBUG) {
            System.out.println(actions);
            System.out.println(other.actions);
            System.out.println(newFreqs);
        }
        this.actions.clear();
        this.actions.putAll(newFreqs);
        normalize();
        checkSanity();
    }

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();

        // TODO: ugly hack to pretty print sort of in order.  Ordering AKQJT correctly would be nice
        SortedSet<Long> sortedHands = Sets.<Long>newTreeSet(
            new Comparator<Long>() {
                public int compare(Long first, Long second) {
                    return Deck.cardMaskString(first).compareTo(Deck.cardMaskString(second)) * -1;
                }
            });
        sortedHands.addAll(actions.rowKeySet());

        // We assume here that insertion is in sorted order, so the existence of a key in this map
        // means that it's the first hand to have that strategy.  For the other hands with the
        // same strategy, refer to equalHands
        Map<Long, String> handToStrategyMap = Maps.newHashMap();
        // Every strategy, with every hand that has the same strategy, in order
        Multimap<String, Long> equalHands = ArrayListMultimap.create(); 

	for (Long hand : sortedHands) {
            StringBuilder strategy = new StringBuilder();
	    Map<ActionSequence, Double> actionFreqs = actions.row(hand);
	    boolean printed = false;
	    for (ActionSequence action : OOPCheckActions.values()) {
		if (actionFreqs.containsKey(action)) {
		    strategy.append(String.format("%4s %4.2f  ",
                            action.name(),
                            actions.row(hand).get(action)));
		    printed = true;
		}
	    }
	    if (printed) {
		strategy.append("\n");
                printed = false;
	    }

	    for (ActionSequence action : OOPBetActions.values()) {
		if (actionFreqs.containsKey(action)) {
		    strategy.append(String.format("%4s %4.2f  ",
                            action.name(),
                            actions.row(hand).get(action)));
		}
	    }

	    for (ActionSequence action : IPCheckedToActions.values()) {
		if (actionFreqs.containsKey(action)) {
		    strategy.append(String.format("%4s %4.2f  ",
                            action.name(),
                            actions.row(hand).get(action)));
		    printed = true;
		}
	    }
	    if (printed) {
		strategy.append("\n");
		printed = false;
	    }

	    for (ActionSequence action : IPBetIntoActions.values()) {
		if (actionFreqs.containsKey(action)) {
		    strategy.append(String.format("%4s %4.2f  ",
                            action.name(),
                            actions.row(hand).get(action)));
		}
	    }
	    if (printed) {
		strategy.append("\n");
		printed = false;
	    }
	    strategy.append("\n\n");
            if (!equalHands.containsKey(strategy.toString())) {
                handToStrategyMap.put(hand, strategy.toString());
            }
            equalHands.put(strategy.toString(), hand);
	}

        for (Long hand : handToStrategyMap.keySet()) {
            String handStrategy = handToStrategyMap.get(hand);
            for (Long similarHand : equalHands.get(handStrategy)) {
                sb.append(Deck.cardMaskString(similarHand, "") + " ");
            }
            sb.append("\n");
            sb.append(handStrategy);
            sb.append("\n");
        }

	return sb.toString();
    }

    abstract void normalize();

    /**
     * Return a table where all the frequencies for each set of ActionSequences sum to 1.
     */
    protected Table<Long, ActionSequence, Double> normalized(
        Table<Long, ActionSequence, Double> freqs, ActionSequence[]... actionSets) {

        Table<Long, ActionSequence, Double> newFreqs = HashBasedTable.create();
        for (long hand : freqs.rowKeySet()) {
            for (ActionSequence[] actionSet : actionSets) {
                double sum = 0.0d;
                for (ActionSequence action : actionSet) {
                    if (freqs.row(hand).containsKey(action)) {
                        sum += freqs.row(hand).get(action);
                    }
                }
                for (ActionSequence action : actionSet) {
                    if (freqs.row(hand).containsKey(action)) {
                        newFreqs.put(hand, action, freqs.row(hand).get(action) / sum);
                    }
                }
            }
        }
        return newFreqs;
    }

    /**
     * Verify that this strategy is well formed.  Action frequencies should add up to 1 where
     * appropriate.
     */
    abstract void checkSanity();

    /**
     * Check that all actions within each action set have frequencies adding up to 1.  For the 
     * UTG player, the action set consists of all actions.  For the EP player, there are separate
     * action sets depending on whether he's facing a bet or a check.
     */
    protected void checkSanity(ActionSequence[]... actionSets) {
        for (long hand : actions.rowKeySet()) {
            for (ActionSequence[] actionSet : actionSets) {
                double sum = 0.0d;
                for (ActionSequence action : actionSet) {
                    if (actions.row(hand).containsKey(action)) {
                        sum += actions.row(hand).get(action);
                    }
                }
                // close enough
                if (Math.abs(1.0 - sum) > 0.0000001d) {
                    throw new IllegalStateException("Frequencies must add to 1, are: " + sum
                        + "\n" + toString());
                }
            }
        }
    }
}
