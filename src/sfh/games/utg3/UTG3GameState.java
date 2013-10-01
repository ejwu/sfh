package sfh.games.utg3;

import sfh.GameState;

import com.google.common.collect.Maps;
import org.pokersource.game.*;

import java.util.*;

public class UTG3GameState implements GameState<UTGStrategy, EPStrategy> {

    // Pot size in big bets
    private double potSizeBB;

    // Cards on the board
    private long board;

    // Hands and the frequencies each player can have them
    // TODO: Normalize the frequencies to sum to 1?
    private Map<Long, Double> utgHands = Maps.newHashMap();
    private Map<Long, Double> epHands = Maps.newHashMap();

    public UTG3GameState(double potSizeBB, long board) {
	this.potSizeBB = potSizeBB;
	this.board = board;

	utgHands.put(Deck.parseCardMask("AcAh"), 1.0);
	epHands.put(Deck.parseCardMask("QcQs"), 0.5);
	epHands.put(Deck.parseCardMask("AsKs"), 0.5);
    }

    @Override
    public double getValue(UTGStrategy utg, EPStrategy ep) {
	return 0.0;
    }
}
