package sfh.games.resistance;

import sfh.Strategy;

public class ResistanceEvilStrategy extends AbstractResistanceStrategy<ResistanceGameState, ResistanceEvilStrategy, ResistanceGoodStrategy> {

    public ResistanceEvilStrategy() {
    }
    
    @Override
    public ResistanceEvilStrategy getBestResponse(ResistanceGameState gs, ResistanceGoodStrategy villain) {
        return null;
    }

    @Override
    public double mergeFrom(ResistanceEvilStrategy other, double epsilon) {
        return 0;
    }

    // Returns the probability of failing the current mission.
    public double getFailStrategy(ResistanceGameState gameState) {
        return 0.0;
    }
    
    public String toString() {
        return "null";
    }
}
