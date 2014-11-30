package sfh.games.resistance;

import com.google.common.collect.*;

import sfh.GameState;

import java.util.*;

public class ResistanceGameState implements GameState<ResistanceGoodStrategy, ResistanceEvilStrategy> {

  // Empty base class is root of game tree, containing no info.
  public ResistanceGameState() {
  }
   

  @Override
  public double getValue(ResistanceGoodStrategy good, ResistanceEvilStrategy evil) {
    // Create all possible assignments of good and evil
    // TODO: be smart
    List<AssignedState> states = Lists.newArrayList();
    final boolean t = true;
    final boolean f = false;
    states.add(new AssignedState(f, f, t, t, t));
    states.add(new AssignedState(f, t, f, t, t));
    states.add(new AssignedState(f, t, t, f, t));
    states.add(new AssignedState(f, t, t, t, f));
    states.add(new AssignedState(t, f, f, t, t));
    states.add(new AssignedState(t, f, t, f, t));
    states.add(new AssignedState(t, f, t, t, f));
    states.add(new AssignedState(t, t, f, f, t));
    states.add(new AssignedState(t, t, f, t, f));
    states.add(new AssignedState(t, t, t, f, f));

    double value = 0.0;
    for (AssignedState s : states) {
      value += s.getValue(good, evil) / states.size();
    }
    return value;
  }

}

class AssignedState extends ResistanceGameState {
  private static final int TURNS_TO_WIN = 3;
 
  public static class Turn {
    private Set<Integer> teamPlayers = Sets.newHashSet();
    private Boolean missionPassed = null;

    public Turn(boolean missionPassed, int... teamPlayers) {
      this.missionPassed = missionPassed;
      if (teamPlayers.length < 2) {
        throw new IllegalArgumentException("team must be at least 2 players");
      }
      for (Integer teamPlayer : teamPlayers) {
        if (this.teamPlayers.contains(teamPlayer)) {
          throw new IllegalArgumentException("team can't repeat players");
        }
        this.teamPlayers.add(teamPlayer);
      }
    }

    public boolean isPassed() {
      return missionPassed;
    }

    public Set<Integer> getTeam() {
      return teamPlayers;
    }
  }

  private final boolean[] isGood;
  private final int currentTurn;
  private final List<Turn> previousTurns = Lists.newArrayList();

  public AssignedState(boolean... isGood) {
    this.isGood = isGood;
    this.currentTurn = 0;
  }

  public AssignedState(AssignedState parent, Team team) {
    isGood = parent.isGood;
    currentTurn = parent.currentTurn + 1;
    previousTurns.addAll(parent.previousTurns);
    
    boolean passed = true; // TODO:XXX
    for (int teamPlayer : team.asIntArray()) {
      if (!isGood[teamPlayer]) {
        // TODO: get fail prob
        passed = false;
      }
    }
    previousTurns.add(new Turn(passed, team.asIntArray()));
  }
  
@Override 
  public double getValue(ResistanceGoodStrategy good, ResistanceEvilStrategy evil) {
    if (currentTurn == 5) {
      int numPassed = 0;
      for (Turn t : previousTurns) {
        if (t.isPassed()) {
          numPassed++;
        }
      }
      return (numPassed >= 3) ? 1 : 0;
    }
    TeamChoiceStrategy strat = good.getStrategy(this);
    double value = 0.0;
    for (ProbableTeam pt : strat) {
      value += pt.probability * this.withTeam(pt.team).getValue(good, evil);
    }
    return value;
  }

  ResistanceGameState withTeam(Team team) {
    return new AssignedState(this, team);
  }

}