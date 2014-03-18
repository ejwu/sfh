package sfh.games.hulhe;

import sfh.GameState;
import sfh.Strategy;
import sfh.games.hulhe.IpTwoStreetStrategy.IPBetIntoActions;
import sfh.games.hulhe.IpTwoStreetStrategy.IPCheckedToActions;
import sfh.games.hulhe.OneStreetHandStrategy;
import sfh.games.hulhe.OopTwoStreetStrategy.OOPBetActions;
import sfh.games.hulhe.OopTwoStreetStrategy.OOPCheckActions;
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
public abstract class AbstractTwoStreetHulheStrategy<
    H extends AbstractTwoStreetHulheStrategy<?,?>, V extends AbstractTwoStreetHulheStrategy<?,?>>
    implements Strategy<HulheGameState, H, V> {

    // A representation for all possible actions for a given hand.  Every street should be
    // represented by a single character to make sorting easier.  See visualizeRange()
    public interface ActionSequence {
        // just a hack to get the enum name
        String name();
    };

    // All possible combinations of results for a street that end with another street being seen
    public enum StreetResult {
        KK, KBC, KBRC, KBR3C, KBR34C, BC, BRC, BR3C, BR34C
    }

    // First table is hand->turn strategy->complete river strategy for all possible results from
    // the turn.
    // Second table is StreetResult->river card->strategy.  Not all StreetResults need to exist.
    // For every StreetResult that does exist, all unknown cards must have a column.
    protected Table<Long, OneStreetHandStrategy, Table<StreetResult, Long, OneStreetHandStrategy>>
        turnAndRiverActions = HashBasedTable.create();

    protected AbstractTwoStreetHulheStrategy(
        Table<Long, OneStreetHandStrategy,
            Table<StreetResult, Long, OneStreetHandStrategy>> actions) {

        turnAndRiverActions.putAll(actions);
        checkSanity2();
    }

    // TODO: Not actually using frequencies here
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

    /**
     * Create the table of default strategies for every hand.
     */
    protected static Table<Long, OneStreetHandStrategy, Table<
        StreetResult, Long, OneStreetHandStrategy>> getDefaultStrategy(
            Iterable<Long> hands, Long deadCards, OneStreetHandStrategy defaultOshs) {

        Table<Long, OneStreetHandStrategy,
            Table<StreetResult, Long, OneStreetHandStrategy>> allActions = HashBasedTable.create();

        for (Long hand : hands) {;
            Table<StreetResult, Long, OneStreetHandStrategy> riverStrategies =
                HashBasedTable.create();
            
            for (StreetResult streetResult : StreetResult.values()) {
                long allDeadCards = deadCards | hand;
                for (Long card : DeckUtils.deckWithout(allDeadCards)) {
                    riverStrategies.put(streetResult, card, defaultOshs);
                }
            }

            allActions.put(hand, defaultOshs, riverStrategies);
        }
        return allActions;
    }

    // update bestFreqs
    protected void updateBestActionForHand(HulheGameState gs, Long hand, 
        ActionSequence[] possibleActions, V villain,
        Table<Long, ActionSequence, Double> bestFreqs, boolean isOop) {
        /*
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
        */
    }

    @Override
    public double mergeFrom(H other, double epsilon) {
        /*
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
        */
        return 0.0;
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
        /*
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
        */
    }

    void checkSanity2() {
    };


    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("  Turn:\n");
        // Map of every strategy to the hands using that strategy, used to group hands with the
        // same strategy
        Multimap<OneStreetHandStrategy, Long> equalHands = ArrayListMultimap.create();

        for (Long hand : turnAndRiverActions.rowKeySet()) {
            // backdoor assertion here that every hand only has one turn strategy
            OneStreetHandStrategy oshStrategy = Iterables.getOnlyElement(
                turnAndRiverActions.row(hand).keySet());
            equalHands.put(oshStrategy, hand);
        }

        for (OneStreetHandStrategy turnStrategy : equalHands.keySet()) {
            List<Long> sortedHands = Lists.newArrayList(equalHands.get(turnStrategy));
            Collections.sort(sortedHands, DeckUtils.getHandComparator());
            String indent = "  ";
            sb.append(indent);
            for (Long hand : sortedHands) {
                sb.append(Deck.cardMaskString(hand, ""));
                sb.append(" ");
            }
            sb.append("\n");
            turnStrategy.appendTo(sb, indent);

            indent += "  ";
            String innerIndent = indent + "  ";
            sb.append(indent);
            sb.append("Turn results:\n");
            for (StreetResult sr : StreetResult.values()) {
                sb.append(indent);
                sb.append(sr.name());
                sb.append("\n");


                for (Long hand : sortedHands) {
                    sb.append(indent);
                    sb.append(Deck.cardMaskString(hand, ""));
                    sb.append("\n");

                    Multimap<OneStreetHandStrategy, Long> equalRiverCardStrategies =
                        ArrayListMultimap.create();
                    Map<Long, OneStreetHandStrategy> riverActions =
                        turnAndRiverActions.get(hand, turnStrategy).row(sr);

                    List<Long> sortedRiverCards = Lists.newArrayList(riverActions.keySet());
                    Collections.sort(sortedRiverCards, DeckUtils.getCardComparator());
                    for (Long card : sortedRiverCards) {
                        equalRiverCardStrategies.put(riverActions.get(card), card);
                    }

                    for (OneStreetHandStrategy riverStrategy : equalRiverCardStrategies.keySet()) {
                        sb.append(indent);
                        for (Long card : equalRiverCardStrategies.get(riverStrategy)) {
                            sb.append(Deck.cardMaskString(card));
                            sb.append(" ");
                        }
                        sb.append("\n");
                        riverStrategy.appendTo(sb, innerIndent);
                    }
                }
            }
        }



        return sb.toString();
    }

    abstract String visualizeRange();

    protected void appendActions(StringBuilder sb, String prefix, int indent, ActionSequence... actionsForDisplay) {
        /*
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
*/
    }
    /*
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
    */
    abstract void normalize();

    /**
     * Return a table where all the frequencies for each set of ActionSequences sum to 1.
     */
    protected Table<Long, ActionSequence, Double> normalized(
        Table<Long, ActionSequence, Double> freqs, ActionSequence[]... actionSets) {
        /*
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
        */
        return null;
    }

    /**
     * Verify that this strategy is well formed.  Action frequencies should add up to 1 where
     * appropriate.
     */
    //abstract void checkSanity();

    /**
     * Check that all actions within each action set have frequencies adding up to 1.  For the 
     * OOP player, the action set consists of all actions.  For the IP player, there are separate
     * action sets depending on whether he's facing a bet or a check.
     */
    /*
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
    */

    /**
     * @return a set of the valid ActionSequences this strategy can use
     */
    abstract Set<ActionSequence> getValidActions();

}
