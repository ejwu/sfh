package sfh.games.resistance;

import sfh.GameState;

public class ResistanceGameState implements GameState<ResistanceGoodStrategy, ResistanceEvilStrategy> {

    public ResistanceGameState() {
    }
    
    @Override
    public double getValue(ResistanceGoodStrategy p1, ResistanceEvilStrategy p2) {
            return 0.0;
    }

}
