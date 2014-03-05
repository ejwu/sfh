package sfh;

import sfh.games.ak.AKBettingStrategy;
import sfh.games.ak.AKCallingStrategy;
import sfh.games.ak.AKGameState;

public class SfhAKGameRunner {

    /**
     * @param args
     */
    public static void main(String[] args) {
        // TODO Auto-generated method stub

        AKGameState gs = new AKGameState(8.0);
        AKBettingStrategy bet = new AKBettingStrategy(68);
        AKCallingStrategy call = new AKCallingStrategy(75);
        System.out.println(bet + " " + call);
        System.out.println(gs.getValue(bet, call));
        for (int i = 0; i < 1000; i++) {
            double epsilon = 5.0 / (i + 5);
            bet.mergeFrom(bet.getBestResponse(gs, call), epsilon);
            System.out.println("new bet strat");
            System.out.println(bet + " " + call);
            System.out.println(gs.getValue(bet, call));

            call.mergeFrom(call.getBestResponse(gs, bet), epsilon);
            System.out.println("new call strat");
            System.out.println(bet + " " + call);
            System.out.println(gs.getValue(bet, call));
        }
        
    }

}
