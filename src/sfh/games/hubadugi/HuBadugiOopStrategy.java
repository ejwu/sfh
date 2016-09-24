package sfh.games.hubadugi;

import sfh.Strategy;
import sfh.badugi.CardSet;
import sfh.badugi.Hand;

public class HuBadugiOopStrategy extends BaseHuBadugiStrategy
    implements Strategy<HuBadugiGameState, HuBadugiOopStrategy, HuBadugiIpStrategy> {
  @Override
  public double mergeFrom(HuBadugiOopStrategy other, double epsilon) {
    return 0;
  }

  @Override
  public HuBadugiOopStrategy getBestResponse(HuBadugiGameState gs, HuBadugiIpStrategy villainStrategy) {
    Hand oopHand = gs.getOopHand();
    HuBadugiOopStrategy bestStrategy = null;
    double bestValue = Double.MIN_VALUE;
    for (CardSet discard : oopHand.getAllValidDiscards()) {
      HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
      strategy.setDiscardStrategy(oopHand, discard);
      double value = gs.getValue(strategy, villainStrategy);
      // TODO: maybe do something smart if values are equal
      if (value > bestValue) {
        bestValue = value;
        bestStrategy = strategy;
      }
    }
    return bestStrategy;
  }
}
