package sfh.games.akq;

import sfh.Strategy;

public abstract class AbstractAKQStrategy<GS, H extends AbstractAKQStrategy, V extends AbstractAKQStrategy>
    implements Strategy<GS, H, V> {
    
    public static final int ACE = 0;
    public static final int KING = 1;
    public static final int QUEEN = 2;
    
    public static final int[] CARDS = {ACE, KING, QUEEN};
    
    // Each value should be between 0.0 and 1.0, inclusive
    protected double[] frequencies = {0.0, 0.0, 0.0}; // A, K, Q

    protected double getFrequency(int index) {
        return frequencies[index];
    }
       
    @Override
    public void mergeFrom(H other, double epsilon) {
        for (int i : CARDS) {
            double diff = frequencies[i] - other.getFrequency(i);
            frequencies[i] -= diff * epsilon;
        }
    }

}
