package sfh.games.roshambo;

import sfh.Strategy;
import java.util.Arrays;

/**
 * Strategy for a player forced to play rock at least 40% of the time.
 */
public class Roshambo40Strategy extends RoshamboStrategy {

    public Roshambo40Strategy() {
        super();
    }

    public Roshambo40Strategy(double[] frequencies) {
        super(frequencies);
    }

    @Override
    public Roshambo40Strategy getBestResponse(RoshamboGameState gs, RoshamboStrategy villain) {
        double paper = 0.0;
        double scissors = 0.0;
        for (int vAction : ACTIONS) {
            paper += PAYOFFS[PAPER][vAction] * villain.frequencies[vAction];
            scissors += PAYOFFS[SCISSORS][vAction] * villain.frequencies[vAction];
        }

        if (paper > scissors) {
            double[] freqs = {0.4, 0.6, 0.0};
            return new Roshambo40Strategy(new double[]{0.4, 0.6, 0.0});
        } else {
            double[] freqs = {0.4, 0.0, 0.6};
            return new Roshambo40Strategy(new double[]{0.4, 0.0, 0.6});
        }
    }

}
