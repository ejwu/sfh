package sfh.games.utg3;

import sfh.Strategy;

public class EPStrategy implements Strategy<EPStrategy, UTGStrategy> {

    // All possible strategies when in position
    public enum IPActions {
	K, BF, BC, B3F, B3C, // when checked to
	C, F, RF, RC, R4 // When bet into
    }			     

    @Override
    public EPStrategy getBestResponse(UTGStrategy ep) {
	return null;
    }

    @Override
    public void mergeFrom(EPStrategy other, double epsilon) {

    }
}
