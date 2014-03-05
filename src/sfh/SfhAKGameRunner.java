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

        final double pot = 10.0;
        final double dampening = 500.0;
        AKGameState gs = new AKGameState(pot);
        AKBettingStrategy bet = new AKBettingStrategy(50);
        AKCallingStrategy call = new AKCallingStrategy(50);
        System.out.println(bet + " " + call);
        System.out.println(gs.getValue(bet, call));
        double value = pot / 2;
        for (int i = 0; i < 1000; i++) {
            System.out.println("   Iteration " + i);
            int oldBet = bet.bettingFrequency;
            int oldCall = call.callingFrequency;
            double newValue;
            double epsilon;
            AKBettingStrategy newBet = bet.getBestResponse(gs, call);
            newValue = gs.getValue(newBet, call);
            System.out.println("new bet strat");
            System.out.println("  " + newBet + " " + call);
            System.out.println(newValue);
            epsilon = (dampening / (i + dampening)) * Math.pow(Math.abs((newValue - value) / pot), .25);
            bet.mergeFrom(newBet, epsilon);
            value = gs.getValue(bet, call);
            System.out.println("merged bet strat");
            System.out.println(bet + " " + call);
            System.out.println(value);

            AKCallingStrategy newCall = call.getBestResponse(gs, bet);
            newValue = gs.getValue(bet, newCall);
            System.out.println("new call strat");
            System.out.println("  " + bet + " " + newCall);
            System.out.println(newValue);
            epsilon = (dampening / (i + dampening)) * Math.pow(Math.abs((newValue - value) / pot),.25);
            call.mergeFrom(newCall, epsilon);
            value = gs.getValue(bet, call);
            System.out.println("merged call strat");
            System.out.println(bet + " " + call);
            System.out.println(value);
            if (bet.bettingFrequency == oldBet && call.callingFrequency == oldCall) {
                break;
            }
        }
        
    }

}
