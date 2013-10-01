package sfh;

import sfh.games.utg3.EPStrategy;
import sfh.games.utg3.UTGStrategy;
import sfh.games.utg3.UTG3GameState;
import com.google.common.collect.Maps;
import org.pokersource.game.*;

public class SfhRunner {

    public static void main(String[] args) {
	Strategy utg = new UTGStrategy();
	Strategy ep = new EPStrategy();
	GameState gs = new UTG3GameState(6.5, Deck.parseCardMask("QhTc5h2c9d"));
	System.out.println(gs.getValue(utg, ep));
    }

    public static void play(int iterations, Strategy strategy1, Strategy strategy2) {
        double epsilon = 0.02;
        for (int i = 1; i < iterations; i++) {
            strategy1.mergeFrom(strategy1.getBestResponse(strategy2), epsilon);
            strategy2.mergeFrom(strategy1.getBestResponse(strategy1), epsilon);
            
            if ((i % 10000) == 0) {

                System.out.println(i + " iterations");
                System.out.println("hero: " + strategy1);
                System.out.println("villain: " + strategy2);
            }
        }
    }
    
}
