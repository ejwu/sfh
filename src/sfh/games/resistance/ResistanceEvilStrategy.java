package sfh.games.resistance;

import sfh.Strategy;

public class ResistanceEvilStrategy implements Strategy<ResistanceGameState, ResistanceEvilStrategy, ResistanceGoodStrategy> {

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

    public String toString() {
        return "null";
    }
}
