package sfh.games.hulhe;

import static sfh.games.hulhe.HulheGameState.DEBUG;

import java.util.Map;
import java.util.Set;

import org.pokersource.game.Deck;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Ordering;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.TreeMultimap;

public class IpTwoStreetStrategy
    extends AbstractTwoStreetHulheStrategy<IpTwoStreetStrategy, OopTwoStreetStrategy> {

    // All possible strategies when in position
    public enum IPCheckedToActions implements ActionSequence {
        K, BF, BC, B3F, B3C
    }

    public enum IPBetIntoActions implements ActionSequence {
        F, C, RF, RC, R4
    }			     

    private static final OneStreetHandStrategy IP_DEFAULT_OSHS =
        createDefaultOneStreetHandStrategy();

    IpTwoStreetStrategy(Table<Long, OneStreetHandStrategy,
        Table<StreetResult, Long, OneStreetHandStrategy>> actions) {
        super(actions);
    }

    /**
     * Return the default strategy of K or C for every hand.
     */
    public static IpTwoStreetStrategy createDefault(Iterable<Long> hands, Long deadCards){
        return new IpTwoStreetStrategy(getDefaultStrategy(hands, deadCards, IP_DEFAULT_OSHS));
    }

    private static OneStreetHandStrategy createDefaultOneStreetHandStrategy() {
        Map<ActionSequence, Double> actions = Maps.newHashMap();
        for (IPCheckedToActions action : IPCheckedToActions.values()) {
            actions.put(action, 0.0);
        }
        actions.put(IPCheckedToActions.K, 1.0);

        for (IPBetIntoActions action : IPBetIntoActions.values()) {
            actions.put(action, 0.0);
        }
        actions.put(IPBetIntoActions.C, 1.0);
       
        return new OneStreetHandStrategy(actions);
    }

    @Override
    public IpTwoStreetStrategy getBestResponse(HulheGameState gs, OopTwoStreetStrategy utg) {
        /*
        Table<Long, ActionSequence, Double> bestBetFreqs = HashBasedTable.create();
        Table<Long, ActionSequence, Double> bestCheckFreqs = HashBasedTable.create();

        for (Long hand : actions.rowKeySet()) {
            if (DEBUG) {
                System.out.println("\noptimizing " + Deck.cardMaskString(hand, ""));
            }

            updateBestActionForHand(gs, hand, IPBetIntoActions.values(), utg, bestBetFreqs, false);
            updateBestActionForHand(gs, hand, IPCheckedToActions.values(), utg, bestCheckFreqs,
                false);
        }
        bestBetFreqs = normalized(bestBetFreqs, IPBetIntoActions.values());
        bestCheckFreqs = normalized(bestCheckFreqs, IPCheckedToActions.values());
        bestBetFreqs.putAll(bestCheckFreqs);
        return new IpTwoStreetStrategy(bestBetFreqs);
        */
        return null;
    }

    @Override
    String visualizeRange() {
        StringBuilder sb = new StringBuilder();
        /*
        sb.append("By action:\n");

        sb.append("When checked to:\n");

        appendActions(sb, "K:  ", 2, IPCheckedToActions.K);
        appendActions(sb, "B*: ", 2, IPCheckedToActions.BF, IPCheckedToActions.BC, IPCheckedToActions.B3F,
            IPCheckedToActions.B3C);

        appendActions(sb, "  BC: ", 3, IPCheckedToActions.BC);
        appendActions(sb, "  BF: ", 3, IPCheckedToActions.BF);
        appendActions(sb, "  B3*:", 3, IPCheckedToActions.B3F, IPCheckedToActions.B3C);

        appendActions(sb, "    B3F:", 4, IPCheckedToActions.B3F);
        appendActions(sb, "    B3C:", 4, IPCheckedToActions.B3C);

        sb.append("When bet into:\n");

        appendActions(sb, "F:  ", 2, IPBetIntoActions.F);
        appendActions(sb, "C:  ", 2, IPBetIntoActions.C);
        appendActions(sb, "R*: ", 2, IPBetIntoActions.RF, IPBetIntoActions.RC, IPBetIntoActions.R4);

        appendActions(sb, "  RF: ", 3, IPBetIntoActions.RF);
        appendActions(sb, "  RC: ", 3, IPBetIntoActions.RC);
        appendActions(sb, "  R4: ", 3, IPBetIntoActions.R4);
        */
        return sb.toString();
    }

    @Override
    void normalize() {
        //       this.actions = normalized(actions, IPBetIntoActions.values(), IPCheckedToActions.values());
    }

    @Override
    void checkSanity() {
        checkSanity(IPBetIntoActions.values(), IPCheckedToActions.values());
    }

    @Override
    Set<ActionSequence> getValidActions() {
        return Sets.newHashSet(
            ObjectArrays.concat(IPBetIntoActions.values(), IPCheckedToActions.values(), ActionSequence.class));
    }

}
