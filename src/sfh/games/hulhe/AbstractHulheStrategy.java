package sfh.games.hulhe;

import sfh.GameState;
import sfh.Strategy;
import sfh.games.hulhe.IpStrategy.IPBetIntoActions;
import sfh.games.hulhe.IpStrategy.IPCheckedToActions;
import sfh.games.hulhe.OopStrategy.OOPBetActions;
import sfh.games.hulhe.OopStrategy.OOPCheckActions;
import static sfh.games.hulhe.HulheGameState.DEBUG;

import com.google.common.base.Predicate;
import com.google.common.base.Strings;
import com.google.common.collect.*;

import org.pokersource.game.*;

import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

// Game state, hero, villain
public abstract class AbstractHulheStrategy<
H extends AbstractHulheStrategy<?,?>, V extends AbstractHulheStrategy<?,?>>
implements Strategy<HulheGameState, H, V> {

    // A representation for all possible actions for a given hand.  Every street should be
    // represented by a single character to make sorting easier.  See visualizeRange()
    public interface ActionSequence {
        // just a hack to get the enum name
        String name();
    };

    static final int FLOP = 0;
    static final int TURN = 1;
    static final int RIVER = 2;

    // Hands, actions, frequencies.  For any given hand, the frequencies of all its actions
    // should sum to 1.  An empty entry for a hand/action pair means the frequency of that action
    // is 0.  The set of hands represented in this table should be identical to the hands
    // used in the actions table for the opposing strategy.
    protected Table<Long, ActionSequence, Double> actions = HashBasedTable.create();

    protected AbstractHulheStrategy(Table<Long, ActionSequence, Double> actions) {
        this.actions.putAll(actions);
        checkSanity();
    }


    /**
     * Return a map of each possible ActionSequence and its frequency for the given hand.
     */
    protected Map<ActionSequence, Double> getActions(Long hand) {
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
    protected void updateBestActionForHand(HulheGameState gs, Long hand, 
        ActionSequence[] possibleActions, V villain,
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


        // Give every action with the highest EV equal weight
        int numBestActions = 0;
        List<ActionSequence> bestActions = Lists.newArrayList();
        for (ActionSequence action : valueList.keySet()) {
            if (valueList.get(action) == bestValue) {
                numBestActions++;
                bestActions.add(action);
            }
        }

        for (ActionSequence action : bestActions) {
            bestFreqs.put(hand, action, 1.0 / numBestActions);
        }
    }

    @Override
    public double mergeFrom(H other, double epsilon) {
        Table<Long, ActionSequence, Double> newFreqs = HashBasedTable.create();

        for (Long hand : actions.rowKeySet()) {
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
        return difference(other);
    }

    /**
     * A completely arbitrary measure of the distance between two strategies.  In this case,
     * for every hand, the difference in the strategies is the average difference between 
     * frequencies for each action sequence, squared.  Difference for the full strategy is the
     * average difference per hand.
     */
    private double difference(H other) {
        double sumDeltas = 0.0;
        for (Long hand : actions.rowKeySet()) {
            Map<ActionSequence, Double> otherActions = other.getActions(hand);
            double delta = 0.0;
            for (ActionSequence action : getValidActions()) {
                // If either table has no entry for this hand/action pair, the frequency of the action is 0.
                double thisFreq = 0.0;
                double otherFreq = 0.0;
                if (actions.contains(hand, action)) {
                    thisFreq = actions.get(hand, action);
                }
                if (otherActions.containsKey(action)) {
                    otherFreq = otherActions.get(action);
                }
                delta += Math.pow(thisFreq - otherFreq, 2);
            }
            sumDeltas += delta / getValidActions().size();
        }

        // average delta per hand
        return sumDeltas / actions.rowKeySet().size();
    }

    /**
     * @return a set of the valid ActionSequences this strategy can use
     */
    abstract Set<ActionSequence> getValidActions();

    // A pretty ugly way to sort hands in descending order.
    protected Comparator<Long> getHandComparator() {
        return new Comparator<Long>() {
            public int compare(Long first, Long second) {
                // Try the first card in each hand
                int rankDiff = Deck.parseRank(Deck.cardMaskString(second).split(" ")[0].substring(0, 1)) -
                    Deck.parseRank(Deck.cardMaskString(first).split(" ")[0].substring(0, 1));
                if (rankDiff != 0) {
                    return rankDiff;
                }

                // Try the second card in each hand.  Since cards within hands are sorted, this
                // makes pairs come before unpaired hands
                int secondRankDiff = Deck.parseRank(Deck.cardMaskString(second).split(" ")[1].substring(0, 1)) -
                    Deck.parseRank(Deck.cardMaskString(first).split(" ")[1].substring(0, 1));
                if (secondRankDiff != 0) {
                    return secondRankDiff;
                }

                // Both cards same, check suit of first card
                int suitDiff = Deck.parseSuit(Deck.cardMaskString(second).split(" ")[0].substring(1, 2)) -
                    Deck.parseSuit(Deck.cardMaskString(first).split(" ")[0].substring(1, 2));
                if (suitDiff != 0) {
                    return suitDiff;
                }

                // Finally, check suit of second card
                return Deck.parseSuit(Deck.cardMaskString(second).split(" ")[1].substring(1, 2)) -
                    Deck.parseSuit(Deck.cardMaskString(first).split(" ")[1].substring(1, 2));
            }
        };
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("By hand:\n");
        
        SortedSet<Long> sortedHands = Sets.<Long>newTreeSet(getHandComparator());
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

        sb.append(visualizeRange());

        return sb.toString();
    }

    abstract String visualizeRange();

    protected void appendActions(StringBuilder sb, String prefix, int indent, ActionSequence... actionsForDisplay) {
        if (actionsForDisplay.length == 0) {
            throw new IllegalArgumentException("Must have at least one action");
        }
        Map<Long, Double> hands = Maps.newHashMap();
        for (ActionSequence action : actionsForDisplay) {
            incrementFrequencies(hands, actions.column(action));
        }
        Multimap<Double, Long> freqToHandMap = TreeMultimap.create(Ordering.natural(), getHandComparator());
        double totalFreq = getTotalFreqAndPopulate(hands, freqToHandMap);
        
        sb.append(prefix);
        appendFrequencies(sb, freqToHandMap, totalFreq, indent);
    }

    private void incrementFrequencies(Map<Long, Double> base, Map<Long, Double> add) {
        for (Long handToAdd : add.keySet()) {
            if (base.containsKey(handToAdd)) {
                base.put(handToAdd, base.get(handToAdd) + add.get(handToAdd));
            } else {
                base.put(handToAdd, add.get(handToAdd));
            }
        }
    }
    
    private double getTotalFreqAndPopulate(Map<Long, Double> handFrequencies, Multimap<Double, Long> freqToHandMap) {
        double totalFreq = 0.0;
        for (Long hand : handFrequencies.keySet()) {
            double freq = handFrequencies.get(hand);
            totalFreq += freq;
            freqToHandMap.put(freq, hand);
        }
        return totalFreq;
    }
    
    private void appendFrequencies(StringBuilder sb, Multimap<Double, Long> freqToHandMap, double totalFreq,
            int indent) {
        boolean first = true;
        if (totalFreq > 0) {
            for (Double freq : freqToHandMap.keySet()) {
                if (!first) {
                    sb.append(Strings.padStart("", indent * 2, ' '));
                }
                first = false;
                sb.append(String.format("%6.2f%%   ", freq * freqToHandMap.get(freq).size() * 100 / totalFreq));
                for (long hand : freqToHandMap.get(freq)) {
                    sb.append(Deck.cardMaskString(hand, ""));
                    sb.append(" ");
                }
                sb.append("\n");
            }
        } else {
            
            sb.append("   None");
        }
        sb.append("\n");
    }

    abstract void normalize();

    /**
     * Return a table where all the frequencies for each set of ActionSequences sum to 1.
     */
    protected Table<Long, ActionSequence, Double> normalized(
        Table<Long, ActionSequence, Double> freqs, ActionSequence[]... actionSets) {

        Table<Long, ActionSequence, Double> newFreqs = HashBasedTable.create();
        for (Long hand : freqs.rowKeySet()) {
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
     * OOP player, the action set consists of all actions.  For the IP player, there are separate
     * action sets depending on whether he's facing a bet or a check.
     */
    protected void checkSanity(ActionSequence[]... actionSets) {
        for (Long hand : actions.rowKeySet()) {
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
