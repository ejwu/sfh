package sfh.games.hulhe;

import static sfh.games.hulhe.HulheGameState.DEBUG;

import java.util.Map;
import java.util.Set;

import org.pokersource.game.Deck;

import sfh.games.hulhe.IpStrategy.IPCheckedToActions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class OopStrategy extends AbstractHulheStrategy<OopStrategy, IpStrategy> {

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
                ObjectArrays.concat(OOPBetActions.values(), OOPCheckActions.values(), ActionSequence.class),
                    ep, bestFreqs, true);
        }
        return new OopStrategy(normalized(bestFreqs, ObjectArrays.concat(
            OOPBetActions.values(), OOPCheckActions.values(), ActionSequence.class)));
    }

    @Override
    String visualizeRange() {
        StringBuilder sb = new StringBuilder();
        sb.append("By action:\n");

        appendActions(sb, "K*: ", 2, OOPCheckActions.values());
        appendActions(sb, "  KF: ", 3, OOPCheckActions.KF);
        appendActions(sb, "  KC: ", 3, OOPCheckActions.KC);
        appendActions(sb, "  KR*:", 3, OOPCheckActions.KRF, OOPCheckActions.KRC, OOPCheckActions.KR4);

        appendActions(sb, "    KRF:", 4, OOPCheckActions.KRF);
        appendActions(sb, "    KRC:", 4, OOPCheckActions.KRC);
        appendActions(sb, "    KR4:", 4, OOPCheckActions.KR4);

        appendActions(sb, "B*: ", 2, OOPBetActions.values());
        appendActions(sb, "  BF: ", 3, OOPBetActions.BF);
        appendActions(sb, "  BC: ", 3, OOPBetActions.BC);
        appendActions(sb, "  B3*:", 3, OOPBetActions.B3F, OOPBetActions.B3C);

        appendActions(sb, "    B3F:", 4, OOPBetActions.B3F);
        appendActions(sb, "    B3C:", 4, OOPBetActions.B3C);

        return sb.toString();
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
