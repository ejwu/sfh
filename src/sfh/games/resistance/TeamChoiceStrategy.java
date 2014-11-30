package sfh.games.resistance;
import java.util.List;
import java.util.Iterator;

// A subset of players which attempts a mission together. Immutable.
class Team {
    private final String[] who;
    public Team(String... who) {
        this.who = who;
    }
    public String[] asStringArray() {
        // TODO: should not return mutable reference
        return who;
    }
}

class ProbableTeam {
    public final double probability;
    public final Team team;
    public ProbableTeam(double probability, Team team) {
        this.probability = probability;
        this.team = team;
    }
}

public class TeamChoiceStrategy implements Iterable<ProbableTeam> {
    private final List<ProbableTeam> list;
    public TeamChoiceStrategy(List<ProbableTeam> list) {
        this.list = list;
    }
    public Iterator<ProbableTeam> iterator() {
        return list.iterator();
    }
}