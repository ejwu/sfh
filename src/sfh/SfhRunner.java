package sfh;

import sfh.games.utg3.EPStrategy;
import sfh.games.utg3.UTGStrategy;
import sfh.games.utg3.UTG3GameState;

import com.google.common.collect.Maps;

import org.pokersource.game.*;

import pokerai.game.eval.spears2p2.StateTableEvaluator;

import java.util.Map;

public class SfhRunner {

    public static void main(String[] args) {
        StateTableEvaluator.initialize();

	Map<Long, Double> utgFrequencies = Maps.newHashMap();
	utgFrequencies.put(Deck.parseCardMask("AcAh"), 0.3333);
        utgFrequencies.put(Deck.parseCardMask("KcKh"), 0.3333);
        utgFrequencies.put(Deck.parseCardMask("QcQh"), 0.3334);

	Map<Long, Double> epFrequencies = Maps.newHashMap();
	epFrequencies.put(Deck.parseCardMask("AsAd"), 0.3333);
        epFrequencies.put(Deck.parseCardMask("KsKd"), 0.3333);
        epFrequencies.put(Deck.parseCardMask("QsQd"), 0.3334);

	UTGStrategy utg = UTGStrategy.create(utgFrequencies);
	EPStrategy ep = EPStrategy.create(epFrequencies);

	GameState<UTGStrategy, EPStrategy> gs =
	    new UTG3GameState(6.5, Deck.parseCardMask("JhTc5h2c9d"), utgFrequencies, epFrequencies);
	System.out.println("Game state:\n" + gs);

	play(100, gs, utg, ep);

    }

    public static void play(int iterations, GameState gs, Strategy strategy1, Strategy strategy2) {
	System.out.println("Original UTG strategy:\n\n" + strategy1);
	System.out.println("Original EP strategy:\n\n" + strategy2);
	System.out.println("Original UTG EV: " + gs.getValue(strategy1, strategy2));

        double epsilon = 0.1;
        for (int i = 0; i < iterations; i++) {
            strategy1.mergeFrom(strategy1.getBestResponse(gs, strategy2), epsilon);
            System.out.println("\n--------------------\n#" + i + " UTG strategy:\n\n" + strategy1);
            System.out.println("EV: " + gs.getValue(strategy1, strategy2));

            strategy2.mergeFrom(strategy2.getBestResponse(gs, strategy1), epsilon);
            System.out.println("\n--------------------\n#" + i + " EP strategy:\n\n" + strategy2);
            System.out.println("EV: " + gs.getValue(strategy1, strategy2));

        }

	System.out.println("\n-----------------------------\nFinal strategies:\n");
        System.out.println("UTG:");
        System.out.println(strategy1);
        System.out.println("EP");
        System.out.println(strategy2);
	System.out.println("Final UTG EV: " + gs.getValue(strategy1, strategy2));
    }
    
}
