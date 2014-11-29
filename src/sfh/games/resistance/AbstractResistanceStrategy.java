package sfh.games.resistance;

import sfh.Strategy;

public abstract class AbstractResistanceStrategy<GS, H, V> implements Strategy<GS, H, V> {
  @Override
  abstract public H getBestResponse(GS gs, V villain);
  @Override
  abstract public double mergeFrom(H other, double epsilon);

  abstract void getStrategy(GS gameState, int turn);
}