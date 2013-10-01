package sfh.games.utg3;

import sfh.Strategy;

public abstract class AbstractUTG3Strategy<Hero, Villain> implements Strategy<Hero, Villain> {
    static final int FLOP = 0;
    static final int TURN = 1;
    static final int RIVER = 2;

}
