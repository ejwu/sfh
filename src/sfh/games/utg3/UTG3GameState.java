package sfh.games.utg3;

import sfh.GameState;
import sfh.games.utg3.AbstractUTG3Strategy.ActionSequence;
import sfh.games.utg3.EPStrategy.IPBetIntoActions;
import sfh.games.utg3.EPStrategy.IPCheckedToActions;
import sfh.games.utg3.UTGStrategy.OOPActions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import org.pokersource.game.*;

import java.util.*;

public class UTG3GameState implements GameState<UTGStrategy, EPStrategy> {

    public enum StreetResults {
	// Both players put in some bets
	ZERO_BETS,
	ONE_BET,
	TWO_BETS,
	THREE_BETS,
	FOUR_BETS,
	// UTG folds after putting in N bets
	UTG_FOLD_ZERO,
	UTG_FOLD_ONE,
	UTG_FOLD_TWO,
	UTG_FOLD_THREE,
	// EP folds after putting in N bets
	EP_FOLD_ZERO,
	EP_FOLD_ONE,
	EP_FOLD_TWO,
	EP_FOLD_THREE,
    }

    // Precalculate the results of every UTG strategy vs. every EP strategy.
    private static final Table<UTGStrategy.OOPActions,
	AbstractUTG3Strategy.ActionSequence, StreetResults> results = HashBasedTable.create();

    static {
	results.put(OOPActions.KF, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPActions.KF, IPCheckedToActions.BF, StreetResults.UTG_FOLD_ZERO);
	results.put(OOPActions.KF, IPCheckedToActions.BC, StreetResults.UTG_FOLD_ZERO);
	results.put(OOPActions.KF, IPCheckedToActions.B3F, StreetResults.UTG_FOLD_ZERO);
	results.put(OOPActions.KF, IPCheckedToActions.B3C, StreetResults.UTG_FOLD_ZERO);

	results.put(OOPActions.KC, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPActions.KC, IPCheckedToActions.BF, StreetResults.ONE_BET);
	results.put(OOPActions.KC, IPCheckedToActions.BC, StreetResults.ONE_BET);
	results.put(OOPActions.KC, IPCheckedToActions.B3F, StreetResults.ONE_BET);
	results.put(OOPActions.KC, IPCheckedToActions.B3C, StreetResults.ONE_BET);

	results.put(OOPActions.KRF, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPActions.KRF, IPCheckedToActions.BF, StreetResults.EP_FOLD_ONE);
	results.put(OOPActions.KRF, IPCheckedToActions.BC, StreetResults.TWO_BETS);
	results.put(OOPActions.KRF, IPCheckedToActions.B3F, StreetResults.UTG_FOLD_TWO);
	results.put(OOPActions.KRF, IPCheckedToActions.B3C, StreetResults.UTG_FOLD_TWO);

	results.put(OOPActions.KRC, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPActions.KRC, IPCheckedToActions.BF, StreetResults.EP_FOLD_ONE);
	results.put(OOPActions.KRC, IPCheckedToActions.BC, StreetResults.TWO_BETS);
	results.put(OOPActions.KRC, IPCheckedToActions.B3F, StreetResults.THREE_BETS);
	results.put(OOPActions.KRC, IPCheckedToActions.B3C, StreetResults.THREE_BETS);

	results.put(OOPActions.KR4, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPActions.KR4, IPCheckedToActions.BF, StreetResults.EP_FOLD_ONE);
	results.put(OOPActions.KR4, IPCheckedToActions.BC, StreetResults.TWO_BETS);
	results.put(OOPActions.KR4, IPCheckedToActions.B3F, StreetResults.EP_FOLD_THREE);
	results.put(OOPActions.KR4, IPCheckedToActions.B3C, StreetResults.FOUR_BETS);



	//...and many more
    }

    // Pot size in big bets
    private double potSizeBB;

    // Cards on the board
    private long board;

    // Hands and the frequencies each player can have them
    // TODO: Normalize the frequencies to sum to 1?
    // TODO: Does this belong here?
    private Map<Long, Double> utgHands = Maps.newHashMap();
    private Map<Long, Double> epHands = Maps.newHashMap();

    public UTG3GameState(double potSizeBB, long board) {
	this.potSizeBB = potSizeBB;
	this.board = board;

	utgHands.put(Deck.parseCardMask("AcAh"), 1.0);
	epHands.put(Deck.parseCardMask("QcQs"), 0.5);
	//	epHands.put(Deck.parseCardMask("AsKs"), 0.5);
    }

    @Override
    public double getValue(UTGStrategy utg, EPStrategy ep) {
	for (Long utgHand : utgHands.keySet()) {
	    for (Long epHand : epHands.keySet()) {
		// TODO: filter for hands that contain duplicate cards
		double handEv = eval(utg.getActions(utgHand),
				     ep.getActions(epHand),
				     getEV(utgHand, epHand, board, potSizeBB, 1.0),
				     potSizeBB,
				     1.0); // River, bet 1 unit
	    }
	}

	return 0.0;
    }

    // TODO: This probably won't work on the flop and turn - maybe value of the game does
    // Return the EV for hand1 on the given board with the given potsize, independent of action
    private double getEV(long hand1, long hand2, long board, double potSize, double betSize) {
	return 3.5;
    }


    /**
     *
     *
     * @param staticEV value of the game if the action goes check/check
     */
    private double eval(Map<ActionSequence, Double> utgStrategies,
			Map<ActionSequence, Double> epStrategies,
			double staticEV,
			double potSize,
			double betSize) {
	for (ActionSequence utgStrategy : utgStrategies.keySet()) {
	    for (ActionSequence epStrategy : epStrategies.keySet()) {
		// do something smart
	    }
	}
	return 0.0;
    }
}
