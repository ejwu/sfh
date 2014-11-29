package sfh.games.resistance;

import com.google.common.collect.*;

import sfh.GameState;

import java.util.*;

public class ResistanceGameState implements GameState<ResistanceGoodStrategy, ResistanceEvilStrategy> {

  private static final int TURNS_TO_WIN = 3;
  
  public static class Turn {
    private Set<String> teamPlayers = Sets.newHashSet();
    private Boolean missionPassed = null;

    public Turn(boolean missionPassed, String... teamPlayers) {
      this.missionPassed = missionPassed;
      if (teamPlayers.length < 2) {
        throw new IllegalArgumentException("team must be at least 2 players");
      }
      for (String teamPlayer : teamPlayers) {
        if (this.teamPlayers.contains(teamPlayer)) {
          throw new IllegalArgumentException("team can't repeat players");
        }
        this.teamPlayers.add(teamPlayer);
      }
    }

    public boolean isPassed() {
      return missionPassed;
    }

    public Set<String> getTeam() {
      return teamPlayers;
    }
  }

  private List<Turn> previousTurns = Lists.newArrayList();
  private int numPassed = 0;
  private int numFailed = 0;

  public ResistanceGameState() {
  }
    
  public void addTurn(Turn turn) {
    previousTurns.add(turn);
    if (turn.isPassed()) {
      numPassed++;
    } else {
      numFailed++;
    }
  }

  @Override
  public double getValue(ResistanceGoodStrategy good, ResistanceEvilStrategy evil) {
    int currentTurn = numPassed + numFailed;

    good.getStrategy(this, currentTurn);
    evil.getStrategy(this, currentTurn);

    return 0.0;
  }

}
