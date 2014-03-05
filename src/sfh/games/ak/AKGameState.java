package sfh.games.ak;

import sfh.GameState;

public class AKGameState implements GameState<AKBettingStrategy, AKCallingStrategy> {

    double potSize = 0.0;
    public AKGameState(double potSize) {
        this.potSize = potSize;
    }
    
    @Override
    public double getValue(AKBettingStrategy p1, AKCallingStrategy p2) {
        
        double value = 0.0;
        
        // A vs A, assume always bet, always call
        // P1 gets half the pot the quarter of the time both have As
        value += .25 * potSize / 2;
        
        // A vs K, win potSize + 1 when p2 calls, otherwise win potSize
        value += .25 * (potSize + 1) * p2.callingFrequency / 100.0;
        value += .25 * potSize * (1 - p2.callingFrequency / 100.0);
        
        // K vs A, lose a bet every time P1 bets
        value += .25 * -1 * p1.bettingFrequency / 100.0;
        // no loss when checking
        
        // K vs K, win the pot when P2 folds, get half the pot when P2 calls, get half the pot when P1 doesn't bet at all
        value += .25 * potSize * p1.bettingFrequency / 100.0 * (1 - p2.callingFrequency / 100.0);
        value += .25 * (potSize / 2) * p1.bettingFrequency / 100.0 * p2.callingFrequency / 100.0;
        value += .25 * (potSize / 2) * (1 - p1.bettingFrequency / 100.0);
        
        // Start with the no-bluff, no-snap value.
        double x = potSize;
        double v2 = (0.25) * (x/2.0 + x + 0 + x/2.0); // == x / 2, as there is no advantage to p1
        // Adjust for bluffing and snapping
        double p = p1.bettingFrequency / 100.0;
        double q = p2.callingFrequency / 100.0;
        // AA: No change
        // AK: earn an extra bet when P2 calls
        v2 += .25 * 1 * q;
        // KA: lose a bet when bluffing
        v2 += .25 * -1 * p;
        // KK: win half the pot when folding out p2
        v2 += .25 * p * (1-q) * x / 2.0;

        if (Math.abs(v2 - value) > .00001) {
            throw new RuntimeException("want " + v2 + " = " + value);
        }
        return value;
    }

}
