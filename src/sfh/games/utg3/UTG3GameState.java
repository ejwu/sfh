package sfh.games.utg3;

import sfh.GameState;
import sfh.games.utg3.AbstractUTG3Strategy.ActionSequence;
import sfh.games.utg3.EPStrategy.IPBetIntoActions;
import sfh.games.utg3.EPStrategy.IPCheckedToActions;
import sfh.games.utg3.UTGStrategy.OOPBetActions;
import sfh.games.utg3.UTGStrategy.OOPCheckActions;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Maps;
import com.google.common.collect.Table;

import org.pokersource.game.*;

import pokerai.game.eval.spears2p2.Hand;
import pokerai.game.eval.spears2p2.StateTableEvaluator;

import java.util.*;

public class UTG3GameState implements GameState<UTGStrategy, EPStrategy> {

    public static final boolean DEBUG = false;

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
    private static final Table<ActionSequence, ActionSequence, StreetResults> results =
	HashBasedTable.create();

    static {
	results.put(OOPCheckActions.KF, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPCheckActions.KF, IPCheckedToActions.BF, StreetResults.UTG_FOLD_ZERO);
	results.put(OOPCheckActions.KF, IPCheckedToActions.BC, StreetResults.UTG_FOLD_ZERO);
	results.put(OOPCheckActions.KF, IPCheckedToActions.B3F, StreetResults.UTG_FOLD_ZERO);
	results.put(OOPCheckActions.KF, IPCheckedToActions.B3C, StreetResults.UTG_FOLD_ZERO);

	results.put(OOPCheckActions.KC, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPCheckActions.KC, IPCheckedToActions.BF, StreetResults.ONE_BET);
	results.put(OOPCheckActions.KC, IPCheckedToActions.BC, StreetResults.ONE_BET);
	results.put(OOPCheckActions.KC, IPCheckedToActions.B3F, StreetResults.ONE_BET);
	results.put(OOPCheckActions.KC, IPCheckedToActions.B3C, StreetResults.ONE_BET);

	results.put(OOPCheckActions.KRF, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPCheckActions.KRF, IPCheckedToActions.BF, StreetResults.EP_FOLD_ONE);
	results.put(OOPCheckActions.KRF, IPCheckedToActions.BC, StreetResults.TWO_BETS);
	results.put(OOPCheckActions.KRF, IPCheckedToActions.B3F, StreetResults.UTG_FOLD_TWO);
	results.put(OOPCheckActions.KRF, IPCheckedToActions.B3C, StreetResults.UTG_FOLD_TWO);

	results.put(OOPCheckActions.KRC, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPCheckActions.KRC, IPCheckedToActions.BF, StreetResults.EP_FOLD_ONE);
	results.put(OOPCheckActions.KRC, IPCheckedToActions.BC, StreetResults.TWO_BETS);
	results.put(OOPCheckActions.KRC, IPCheckedToActions.B3F, StreetResults.THREE_BETS);
	results.put(OOPCheckActions.KRC, IPCheckedToActions.B3C, StreetResults.THREE_BETS);

	results.put(OOPCheckActions.KR4, IPCheckedToActions.K, StreetResults.ZERO_BETS);
	results.put(OOPCheckActions.KR4, IPCheckedToActions.BF, StreetResults.EP_FOLD_ONE);
	results.put(OOPCheckActions.KR4, IPCheckedToActions.BC, StreetResults.TWO_BETS);
	results.put(OOPCheckActions.KR4, IPCheckedToActions.B3F, StreetResults.EP_FOLD_THREE);
	results.put(OOPCheckActions.KR4, IPCheckedToActions.B3C, StreetResults.FOUR_BETS);

	results.put(OOPBetActions.BF, IPBetIntoActions.F, StreetResults.EP_FOLD_ZERO);
	results.put(OOPBetActions.BF, IPBetIntoActions.C, StreetResults.ONE_BET);
	results.put(OOPBetActions.BF, IPBetIntoActions.RF, StreetResults.UTG_FOLD_ONE);
	results.put(OOPBetActions.BF, IPBetIntoActions.RC, StreetResults.UTG_FOLD_ONE);
	results.put(OOPBetActions.BF, IPBetIntoActions.R4, StreetResults.UTG_FOLD_ONE);

	results.put(OOPBetActions.BC, IPBetIntoActions.F, StreetResults.EP_FOLD_ZERO);
	results.put(OOPBetActions.BC, IPBetIntoActions.C, StreetResults.ONE_BET);
	results.put(OOPBetActions.BC, IPBetIntoActions.RF, StreetResults.TWO_BETS);
	results.put(OOPBetActions.BC, IPBetIntoActions.RC, StreetResults.TWO_BETS);
	results.put(OOPBetActions.BC, IPBetIntoActions.R4, StreetResults.TWO_BETS);

	results.put(OOPBetActions.B3F, IPBetIntoActions.F, StreetResults.EP_FOLD_ZERO);
	results.put(OOPBetActions.B3F, IPBetIntoActions.C, StreetResults.ONE_BET);
	results.put(OOPBetActions.B3F, IPBetIntoActions.RF, StreetResults.EP_FOLD_TWO);
	results.put(OOPBetActions.B3F, IPBetIntoActions.RC, StreetResults.THREE_BETS);
	results.put(OOPBetActions.B3F, IPBetIntoActions.R4, StreetResults.UTG_FOLD_THREE);

	results.put(OOPBetActions.B3C, IPBetIntoActions.F, StreetResults.EP_FOLD_ZERO);
	results.put(OOPBetActions.B3C, IPBetIntoActions.C, StreetResults.ONE_BET);
	results.put(OOPBetActions.B3C, IPBetIntoActions.RF, StreetResults.EP_FOLD_TWO);
	results.put(OOPBetActions.B3C, IPBetIntoActions.RC, StreetResults.THREE_BETS);
	results.put(OOPBetActions.B3C, IPBetIntoActions.R4, StreetResults.FOUR_BETS);
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

    public UTG3GameState(double potSizeBB, long board,
	Map<Long, Double> utgHands, Map<Long, Double> epHands) {
	
	this.potSizeBB = potSizeBB;
	this.board = board;

	this.utgHands.putAll(utgHands);
	this.epHands.putAll(epHands);
    }

    @Override
    public double getValue(UTGStrategy utg, EPStrategy ep) {
        return getValue(utg, ep, false);
    }

    // partial allows getting the value of a strategy for only the hands specified in the strategy,
    // ignoring other hands possible in the game state
    public double getValue(UTGStrategy utg, EPStrategy ep, boolean allowPartial) {
	double value = 0.0;
        double sum = 0.0;

	for (Map.Entry<Long, Double> utgEntry : utgHands.entrySet()) {
	    if (utgEntry.getValue() > 0.0) {
		for (Map.Entry<Long, Double> epEntry : epHands.entrySet()) {
		    if (epEntry.getValue() > 0.0) {
                        if (!allowPartial ||
                            (!utg.getActions(utgEntry.getKey()).isEmpty() &&
                                !ep.getActions(epEntry.getKey()).isEmpty())) {
                            if (DEBUG) {
                                System.out.println(Deck.cardMaskString(utgEntry.getKey(), "") +
                                    " vs " + Deck.cardMaskString(epEntry.getKey(), "") + "\n");
                            }
                            // TODO: filter for hands that contain duplicate cards
                            double handEv = eval(utg.getActions(utgEntry.getKey()),
                                ep.getActions(epEntry.getKey()),
                                getEV(utgEntry.getKey(), epEntry.getKey(), board,
                                    potSizeBB),
                                potSizeBB);
                            value += utgEntry.getValue() * epEntry.getValue() * handEv;
                            sum += utgEntry.getValue() * epEntry.getValue();
                        }
                    }
		}
	    }
	}

        // If no partial strategies are allowed, sum should be 1.  Otherwise, sum should be the
        // percentage of results reachable from a partial strategy;

        if (!allowPartial && Math.abs(sum - 1.0) > 0.0000000001) {
            throw new IllegalStateException("Sum must be 1.0 for full strategies, is " + sum);
        }

        if (allowPartial && sum > .9999999999) {
            throw new IllegalStateException("Partial strategies cannot reach more than 100%");
        }

	return value / sum;
    }

    // TODO: This probably won't work on the flop and turn - maybe value of the game does
    // Return the EV for hand1 on the given board with the given potsize, independent of action

    // FIXME: river only

    // Returns the percentage of the pot hand1 gets
    private double getEV(long hand1, long hand2, long board, double potSize) {
        long h1l = hand1 | board;
        long h2l = hand2 | board;

        Hand h1 = Hand.parse(Deck.cardMaskString(h1l, ""));
        Hand h2 = Hand.parse(Deck.cardMaskString(h2l, ""));

        int rank1 = StateTableEvaluator.getRank(h1);
        int rank2 = StateTableEvaluator.getRank(h2);

        if (rank1 > rank2) {
            return 1;
        } else if (rank2 > rank1) {
            return 0;
        }
        return 0.5;
    }

    // such a brutal hack
    private boolean isValidPair(ActionSequence utg, ActionSequence ep) {
	return (utg instanceof OOPCheckActions && ep instanceof IPCheckedToActions)
	    || (utg instanceof OOPBetActions && ep instanceof IPBetIntoActions);
    }

    /**
     * Return the value of this game state given the full set of strategies for both players
     *
     * @param staticEV value of the game if the action goes check/check
     */
    private double eval(Map<ActionSequence, Double> utgStrategies,
			Map<ActionSequence, Double> epStrategies,
			double staticEV,
			double potSize) {
	double value = 0.0;
	// The value of a pair of strategies for UTG
	double strategyPairValue = 0.0;
	for (Map.Entry<ActionSequence, Double> utgEntry : utgStrategies.entrySet()) {
	    if (utgEntry.getValue() > 0.0) {
		double utgFrequency = utgEntry.getValue();
		for (Map.Entry<ActionSequence, Double> epEntry : epStrategies.entrySet()) {
		    // isValidPair is a hack
		    if (epEntry.getValue() > 0.0
			&& isValidPair(utgEntry.getKey(), epEntry.getKey())) {

			double epFrequency = epEntry.getValue();
			StreetResults result = results.get(utgEntry.getKey(), epEntry.getKey());
			switch (result) {
			case ZERO_BETS:
			    strategyPairValue = staticEV * potSize;
			    break;
			case ONE_BET:
			    strategyPairValue = staticEV * (potSize + 2) - 1;
			    break;
			case TWO_BETS:
			    strategyPairValue = staticEV * (potSize + 4) - 2;
			    break;
			case THREE_BETS:
			    strategyPairValue = staticEV * (potSize + 6) - 3;
			    break;
			case FOUR_BETS:
			    strategyPairValue = staticEV * (potSize + 8) - 4;
			    break;
			case UTG_FOLD_ZERO:
			    strategyPairValue = 0;
			    break;
			case UTG_FOLD_ONE:
			    strategyPairValue = -1;
			    break;
			case UTG_FOLD_TWO:
			    strategyPairValue = -2;
			    break;
			case UTG_FOLD_THREE:
			    strategyPairValue = -3;
			    break;
			case EP_FOLD_ZERO:
			    strategyPairValue = potSize;
			    break;
			case EP_FOLD_ONE:
			    strategyPairValue = potSize + 1;
			    break;
			case EP_FOLD_TWO:
			    strategyPairValue = potSize + 2;
			    break;
			case EP_FOLD_THREE:
			    strategyPairValue = potSize + 3;
			    break;
			default:
			    throw new IllegalStateException("Should always have a result");
			}
			
			double globalValue = utgFrequency * epFrequency * strategyPairValue;

			value += globalValue;
                        if (DEBUG) {
                            System.out.println(utgEntry + " " + epEntry + " " + result + " " +
                                strategyPairValue + " " + globalValue);
                        }
		    }

		}
	    }
	}
        if (DEBUG) {
            System.out.println("Overall: " + value + "\n");
        }
	return value;
    }

    private String formatHands(Map<Long, Double> hands) {
	StringBuilder sb = new StringBuilder();
	for (Long hand : hands.keySet()) {
	    sb.append(Deck.cardMaskString(hand, "") + ": " + hands.get(hand) + "  ");
	}
	return sb.toString();
    } 

    @Override
    public String toString() {
	StringBuilder sb = new StringBuilder();
	sb.append("UTG:\n");
	sb.append(formatHands(utgHands));
	sb.append("\n\n");
	sb.append("EP:\n");
	sb.append(formatHands(epHands));
	sb.append("\n");
	sb.append("\nBoard: " + Deck.cardMaskString(board) + "\n");
	sb.append("Pot size (BB): " + potSizeBB + "\n");
	return sb.toString();
    }
}
