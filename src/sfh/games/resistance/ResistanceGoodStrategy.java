package sfh.games.resistance;

import sfh.Strategy;

public class ResistanceGoodStrategy extends AbstractResistanceStrategy<ResistanceGameState, ResistanceGoodStrategy, ResistanceEvilStrategy> {

    public ResistanceGoodStrategy() {
    }
    
    @Override
    public ResistanceGoodStrategy getBestResponse(ResistanceGameState gs, ResistanceEvilStrategy villain) {
        return null;
    }

    @Override
    public double mergeFrom(ResistanceGoodStrategy other, double epsilon) {
        return 0;
    }

    public void getStrategy(ResistanceGameState gameState, int turn) {
      // TODO
    }

    public String toString() {
        return "null";
    }
}
