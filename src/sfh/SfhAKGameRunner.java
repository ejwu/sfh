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

        final double pot = 12.0;
        final double dampening = 10.0;
        AKGameState gs = new AKGameState(pot);
        AKBettingStrategy bet = new AKBettingStrategy(50);
        AKCallingStrategy call = new AKCallingStrategy(50);
        System.out.println(bet + " " + call);
        System.out.println(gs.getValue(bet, call));
        double value = pot / 2;
        for (int i = 0; i < 10000; i++) {
            System.out.println("   Iteration " + i);
            double oldBet = bet.bettingFrequency;
            double oldCall = call.callingFrequency;
            double newValue;
            double epsilon;
            AKBettingStrategy newBet = bet.getBestResponse(gs, call);
            newValue = gs.getValue(newBet, call);
            System.out.println("new bet strat");
            System.out.println("  " + newBet + " " + call);
            System.out.println(newValue);
            epsilon = (dampening / (i + dampening)) * Math.pow(Math.abs((newValue - value) / pot), .01);
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
            epsilon = (dampening / (i + dampening)) * Math.pow(Math.abs((newValue - value) / pot),.01);
            call.mergeFrom(newCall, epsilon);
            value = gs.getValue(bet, call);
            System.out.println("merged call strat");
            System.out.println(bet + " " + call);
            System.out.println(value);
            if (Math.abs(bet.bettingFrequency - oldBet) < 1e-6 &&
                Math.abs(call.callingFrequency - oldCall) < 1e-6) {
                break;
            }
        }
        
    }

}
