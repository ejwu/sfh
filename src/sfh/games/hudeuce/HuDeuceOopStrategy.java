package sfh.games.hudeuce;

import sfh.Strategy;
import sfh.cards.CardSet;
import sfh.deucetoseven.DeuceToSevenHand;

public class HuDeuceOopStrategy extends BaseHuDeuceStrategy
    implements Strategy<HuDeuceGameState, HuDeuceOopStrategy, HuDeuceIpStrategy> {

  @Override
  public double mergeFrom(HuDeuceOopStrategy other, double epsilon) {
    // Totally ignore current strategy and copy the other one wherever available
    for (DeuceToSevenHand hand : other.discardStrategy.keySet()) {
      setDiscardStrategy(hand, other.getDiscardStrategy(hand));
    }
    return 0;
  }

  @Override
  public HuDeuceOopStrategy getBestResponse(HuDeuceGameState gs, HuDeuceIpStrategy villainStrategy) {
    DeuceToSevenHand oopHand = gs.getOopHand();
    HuDeuceOopStrategy bestStrategy = null;
    double bestValue = Double.MIN_VALUE;
    for (CardSet discard : oopHand.getAllValidDiscards()) {
      HuDeuceOopStrategy strategy = new HuDeuceOopStrategy();
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
