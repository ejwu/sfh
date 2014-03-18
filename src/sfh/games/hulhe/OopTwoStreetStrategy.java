package sfh.games.hulhe;

import static sfh.games.hulhe.HulheGameState.DEBUG;

import java.util.List;
import java.util.Map;
import java.util.Set;

import org.pokersource.game.Deck;

import sfh.games.hulhe.IpStrategy.IPCheckedToActions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

public class OopTwoStreetStrategy
    extends AbstractTwoStreetHulheStrategy<OopTwoStreetStrategy, IpTwoStreetStrategy> {

    // All possible actions when out of position on any street
    public enum OOPCheckActions implements ActionSequence {
        KF, KC, KRF, KRC, KR4
    }

    public enum OOPBetActions implements ActionSequence {
        BF, BC, B3F, B3C // bet first
    }

    private static final OneStreetHandStrategy OOP_DEFAULT_OSHS =
        createDefaultOneStreetHandStrategy();

    OopTwoStreetStrategy(Table<Long, OneStreetHandStrategy,
        Table<StreetResult, Long, OneStreetHandStrategy>> actions) {
        super(actions);
    }

    /**
     * Return the default strategy of KC for every hand.
     */
    public static OopTwoStreetStrategy createDefault(Iterable<Long> hands, Long deadCards){
        return new OopTwoStreetStrategy(
            getDefaultStrategy(hands, deadCards, OOP_DEFAULT_OSHS));
    }

    private static OneStreetHandStrategy createDefaultOneStreetHandStrategy() {
        Map<ActionSequence, Double> actions = Maps.newHashMap();
        for (OOPCheckActions action : OOPCheckActions.values()) {
            actions.put(action, 0.0);
        }
        for (OOPBetActions action : OOPBetActions.values()) {
            actions.put(action, 0.0);
        }
        actions.put(OOPCheckActions.KC, 1.0);
       
        return new OneStreetHandStrategy(actions);
    }

    @Override
    public OopTwoStreetStrategy getBestResponse(HulheGameState gs, IpTwoStreetStrategy ep) {
        return null;
    }

    @Override
    String visualizeRange() {
        return "";
    }

    @Override
    void normalize() {
        /*
        this.actions = normalized(actions, ObjectArrays.concat(
            OOPBetActions.values(), OOPCheckActions.values(), ActionSequence.class));
        */
    }

    @Override
    public void checkSanity() {
        checkSanity(ObjectArrays.concat(OOPBetActions.values(), OOPCheckActions.values(),
            ActionSequence.class));
    }

    @Override
    Set<ActionSequence> getValidActions() {
        return Sets.newHashSet(ObjectArrays.concat(
                OOPBetActions.values(), OOPCheckActions.values(), ActionSequence.class));
    }
}
