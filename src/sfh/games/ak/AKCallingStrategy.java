package sfh.games.ak;

import sfh.Strategy;

public class AKCallingStrategy implements Strategy<AKGameState, AKCallingStrategy, AKBettingStrategy> {
    public int callingFrequency = 0;

    public AKCallingStrategy(int freq) {
        this.callingFrequency = freq;
    }
    
    @Override
    public AKCallingStrategy getBestResponse(AKGameState gs, AKBettingStrategy villain) {
        double bestValue = 999999999;
        AKCallingStrategy bestStrategy = null;
        for (int i = 0; i <= 100; i++) {
            AKCallingStrategy strat = new AKCallingStrategy(i);
            double value = gs.getValue(villain, strat);
            if (value < bestValue) {
                bestValue = value;
                bestStrategy = strat;
            }
        }
        return bestStrategy;
    }

    @Override
    public double mergeFrom(AKCallingStrategy other, double epsilon) {
        callingFrequency = (int) (callingFrequency + ((other.callingFrequency - callingFrequency) * epsilon));
        return 0.0;
    }
    
    public String toString() {
        return String.valueOf(callingFrequency);
    }
}
