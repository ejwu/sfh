package sfh;

import sfh.games.hulhe.IpStrategy;
import sfh.games.hulhe.HulheGameState;
import sfh.games.hulhe.OopStrategy;

import com.google.common.collect.Maps;

import org.pokersource.game.*;

import pokerai.game.eval.spears2p2.StateTableEvaluator;

import java.util.Map;

public class SfhRunner {

	public static void main(String[] args) {
		StateTableEvaluator.initialize();

		Map<Long, Double> oopFrequencies = OopStrategy.createEqualFrequencies(
				"7c7d", "7c7h", "7c7s", "7d7h", "7d7s", "7h7s",
				//            "8d8h", "8d8s", "8h8s",
				"9c9d", "9c9h", "9c9s", "9d9h", "9d9s", "9h9s",
				"TcTd", "TcTh", "TcTs", "TdTh", "TdTs", "ThTs",
				"JcJd", "JcJh", "JdJh",
				"QcQd", "QcQh", "QcQs", "QdQh", "QdQs", "QhQs",
				"KcKd", "KcKh", "KcKs", "KdKh", "KdKs", "KhKs",
				"AcAd", "AcAh", "AcAs", "AdAh", "AdAs", "AhAs"
				);

		Map<Long, Double> ipFrequencies = IpStrategy.createEqualFrequencies(
				"8h6h", "8s6s",
				"8d7d", "8h7h", "8s7s",
				"8d9d", "8h9h", "8s9s",
				"8dTd", "8hTh", "8sTs",
				"Ad8d", "Ah8h", "As8s",
				"Ac6c", "Ah6h", "As6s",
				"7c6c", "7h6h", "7s6s",
				"9c7c", "9d7d", "9h7h", "9s7s",
				"7c5c", "7d5d", "7h5h", "7s5s",
				"7c7d", "7c7h", "7c7s", "7d7h", "7d7s", "7h7s",
				// add some real hands
				"8d8h", "8d8s", "8h8s",
				"6c6h", "6c6s", "6h6s"
				//            "5c4c", "5d4d", "5h4h", "5s4s"
				);

		OopStrategy oop = OopStrategy.create(oopFrequencies);
		IpStrategy ip = IpStrategy.create(ipFrequencies);

		GameState<OopStrategy, IpStrategy> gs =
				new HulheGameState(13.25, Deck.parseCardMask("8c6d2hJs2s"), oopFrequencies, ipFrequencies);
		System.out.println("Game state:\n" + gs);

		play(10, gs, oop, ip);
	}

	public static void play(int iterations, GameState gs, Strategy strategy1, Strategy strategy2) {
		System.out.println("Original UTG strategy:\n\n" + strategy1);
		System.out.println("Original IP strategy:\n\n" + strategy2);
		System.out.println("Original OOP EV: " + gs.getValue(strategy1, strategy2));

		double epsilon = 0.5;
		for (int i = 0; i < iterations; i++) {
			strategy1.mergeFrom(strategy1.getBestResponse(gs, strategy2), epsilon);
			System.out.println("\n--------------------\n#" + i + " UTG strategy:\n\n" + strategy1);
			System.out.println("EV: " + gs.getValue(strategy1, strategy2));

			strategy2.mergeFrom(strategy2.getBestResponse(gs, strategy1), epsilon);
			System.out.println("\n--------------------\n#" + i + " IP strategy:\n\n" + strategy2);
			System.out.println("EV: " + gs.getValue(strategy1, strategy2));

		}

		System.out.println("\n-----------------------------\nFinal strategies:\n");
		System.out.println("UTG:");
		System.out.println(strategy1);
		System.out.println("IP");
		System.out.println(strategy2);
		System.out.println("Final UTG EV: " + gs.getValue(strategy1, strategy2));
	}

}
