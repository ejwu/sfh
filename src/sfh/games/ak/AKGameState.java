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
        return value;
    }

}
