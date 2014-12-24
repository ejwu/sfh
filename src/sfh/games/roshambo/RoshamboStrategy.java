package sfh.games.roshambo;

import sfh.Strategy;

import java.util.Arrays;


public class RoshamboStrategy implements Strategy<RoshamboGameState, RoshamboStrategy, RoshamboStrategy> {

    public static final int ROCK = 0;
    public static final int PAPER = 1;
    public static final int SCISSORS = 2;
    
    public static final int[] ACTIONS = {ROCK, PAPER, SCISSORS};
    
    // Good ol' rock.
    // TODO: Make sure these always sum to 1
    public double[] frequencies = {1.0, 0.0, 0.0};

    protected static final double[][] PAYOFFS = {
        {0.0, -1.0, 1.0},
        {1.0, 0.0, -1.0},
        {-1.0, 1.0, 0.0}};

    /*    
    private static final double[][] PAYOFFS = {
        // R, P, S, H, SL
        {0.0, -1.0, 1.0, -2.0, -2.0},
        {2.0, 0.0, -1.0, -2.0, -2.0},
        {-1.0, 1.0, 0.0, -2.0, -2.0},
        {-2.0, -2.0, -2.0, -2.0, -2.0},
        {-2.0, -2.0, -2.0, -2.0, -2.0}};
    */
    /**
     * Default strategy is to play all rock, all the time
     */
    public RoshamboStrategy() {
    }
    
    public RoshamboStrategy(double[] newFreqs) {
        frequencies = newFreqs;
    }
    
    @Override
    public RoshamboStrategy getBestResponse(RoshamboGameState gs, RoshamboStrategy villain) {
        double rockValue = 0.0;
        double paperValue = 0.0;
        double scissorsValue = 0.0;
        for (int vAction : ACTIONS) {
            rockValue += PAYOFFS[ROCK][vAction] * villain.frequencies[vAction];
            paperValue += PAYOFFS[PAPER][vAction] * villain.frequencies[vAction];
            scissorsValue += PAYOFFS[SCISSORS][vAction] * villain.frequencies[vAction];
        }

        if (rockValue > scissorsValue && rockValue > paperValue) {
            return new RoshamboStrategy(new double[]{1.0, 0.0, 0.0});
        } else if (paperValue > scissorsValue && paperValue > rockValue) {
            return new RoshamboStrategy(new double[]{0.0, 1.0, 0.0});
        } else {
            return new RoshamboStrategy(new double[]{0.0, 0.0, 1.0});
        }
    }
    
    @Override
    public double mergeFrom(RoshamboStrategy other, double epsilon) {
        for (int i = 0; i < frequencies.length; i++) {
            double diff = frequencies[i] - other.frequencies[i];
            frequencies[i] -= (diff * epsilon);
        }
        normalize(frequencies);
        // not a real value
        return 0.0;
    }
    
    // hack, make it add to 1 properly
    protected void normalize(double[] freqs) {
        for (int i = 0; i < freqs.length; i++) {
            if (freqs[i] < 0) {
                freqs[i] = 0;
            }
        }
        double sum = freqs[0] + freqs[1] + freqs[2];
        freqs[0] = freqs[0] / sum;
        freqs[1] = freqs[1] / sum;
        freqs[2] = freqs[2] / sum;
    }
    
    public String toString() {
        return Arrays.toString(frequencies);
    }
}
