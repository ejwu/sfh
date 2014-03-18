package sfh.games.hulhe;

import sfh.games.hulhe.AbstractTwoStreetHulheStrategy.ActionSequence;
import sfh.games.hulhe.IpTwoStreetStrategy.IPBetIntoActions;
import sfh.games.hulhe.IpTwoStreetStrategy.IPCheckedToActions;
import sfh.games.hulhe.OopTwoStreetStrategy.OOPBetActions;
import sfh.games.hulhe.OopTwoStreetStrategy.OOPCheckActions;
import com.google.common.collect.Maps;

import java.util.Map;

public class OneStreetHandStrategy {
    Map<ActionSequence, Double> actions = Maps.newHashMap();

    public OneStreetHandStrategy(Map<ActionSequence, Double> actions) {
        this.actions.putAll(actions);
    }

    /*
     * Print frequencies for each action in this set on one line, or do nothing if none of these
     * actions are taken.
     */
    private void appendActionSet(StringBuilder sb, String indent, ActionSequence[] actionSet) {
        boolean printed = false;
        for (ActionSequence action : actionSet) {
            if (actions.containsKey(action)) {
                if (!printed) {
                    sb.append(indent);
                }
                sb.append(String.format("%4s %4.2f  ", action.name(), actions.get(action)));
                printed = true;
            }
        }
        if (printed) {
            sb.append("\n");
        }
    }

    /**
     * Pretty print this strategy, indented with the given string
     */
    public void appendTo(StringBuilder sb, String indent) {
        appendActionSet(sb, indent, OOPCheckActions.values());
        appendActionSet(sb, indent, OOPBetActions.values());
        appendActionSet(sb, indent, IPCheckedToActions.values());
        appendActionSet(sb, indent, IPBetIntoActions.values());
        sb.append("\n");
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        appendTo(sb, "");
        return sb.toString();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        OneStreetHandStrategy other = (OneStreetHandStrategy) obj;

        return this.actions.equals(other.actions);
    }

    @Override
    public int hashCode() {
        return actions.hashCode();
    }
}

