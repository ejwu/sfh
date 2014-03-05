package sfh.games.ak;

import sfh.Strategy;

public class AKBettingStrategy implements Strategy<AKGameState, AKBettingStrategy, AKCallingStrategy> {

    public int bettingFrequency = 0;
    
    public AKBettingStrategy(int freq) {
        this.bettingFrequency = freq;
    }
    
    @Override
    public AKBettingStrategy getBestResponse(AKGameState gs, AKCallingStrategy villain) {
        double bestValue = -999999999;
        AKBettingStrategy bestStrategy = null;
        for (int i = 0; i <= 100; i++) {
            AKBettingStrategy strat = new AKBettingStrategy(i);
            double value = gs.getValue(strat, villain);
            if (value > bestValue) {
                bestValue = value;
                bestStrategy = strat;
            }
        }
        return bestStrategy;
    }

    @Override
    public double mergeFrom(AKBettingStrategy other, double epsilon) {
        bettingFrequency = (int) (bettingFrequency + ((other.bettingFrequency - bettingFrequency) * epsilon));
        return 0;
    }

    public String toString() {
        return String.valueOf(bettingFrequency);
    }
}
