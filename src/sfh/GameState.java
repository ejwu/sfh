package sfh;

public interface GameState<P1, P2> {
    // Get the value of the game given two players' strategies
    double getValue(P1 p1, P2 p2);
}
