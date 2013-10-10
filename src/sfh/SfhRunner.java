package sfh;

import sfh.games.utg3.EPStrategy;
import sfh.games.utg3.UTGStrategy;
import sfh.games.utg3.UTG3GameState;

import com.google.common.collect.Maps;

import org.pokersource.game.*;

import java.util.Map;

public class SfhRunner {

    public static void main(String[] args) {
	Map<Long, Double> utgFrequencies = Maps.newHashMap();
	utgFrequencies.put(Deck.parseCardMask("AcAh"), 0.5);
	utgFrequencies.put(Deck.parseCardMask("KcJc"), 0.5);

	Map<Long, Double> epFrequencies = Maps.newHashMap();
	epFrequencies.put(Deck.parseCardMask("QcQs"), 1.0);
	UTGStrategy utg = new UTGStrategy(utgFrequencies);
	EPStrategy ep = new EPStrategy(epFrequencies);

	GameState<UTGStrategy, EPStrategy> gs =
	    new UTG3GameState(6.5, Deck.parseCardMask("QhTc5h2c9d"), utgFrequencies, epFrequencies);
	System.out.println(gs);

	System.out.println("UTG strategy:\n\n" + utg);
	System.out.println("EP strategy:\n\n" + ep);
	System.out.println("UTG EV: " + gs.getValue(utg, ep));

	play(1, gs, utg, ep);

    }

    public static void play(int iterations, GameState gs, Strategy strategy1, Strategy strategy2) {
        double epsilon = 0.02;
        for (int i = 0; i < iterations; i++) {
            strategy1.mergeFrom(strategy1.getBestResponse(gs, strategy2), epsilon);
	    //            strategy2.mergeFrom(strategy1.getBestResponse(strategy1), epsilon);
            
        }

	System.out.println("\n-----------------------------\n");
	System.out.println("UTG strategy:\n\n" + strategy1);
	System.out.println("EP strategy:\n\n" + strategy2);
	System.out.println("UTG EV: " + gs.getValue(strategy1, strategy2));
    }
    
}
