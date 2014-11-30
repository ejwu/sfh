package sfh.games.resistance;

import sfh.Strategy;
import com.google.common.collect.ImmutableList;

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

    public TeamChoiceStrategy getStrategy(ResistanceGameState gameState, int turn) {
        return new TeamChoiceStrategy(ImmutableList.of(new ProbableTeam(1.0, new Team("0","1"))));
      // TODO
    }

    public String toString() {
        return "null";
    }
}
