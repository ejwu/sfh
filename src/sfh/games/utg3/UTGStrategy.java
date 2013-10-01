package sfh.games.utg3;

import sfh.Strategy;

public class UTGStrategy extends AbstractUTG3Strategy<UTGStrategy, EPStrategy> {

    // All possible actions when out of position
    public enum OOPActions {
	KF, KC, KRF, KRC, KR4, // check first
	BF, BC, B3F, B3C // bet first
    }



    @Override
    public UTGStrategy getBestResponse(EPStrategy ep) {
	return null;
    }

    @Override
    public void mergeFrom(UTGStrategy other, double epsilon) {

    }
}
