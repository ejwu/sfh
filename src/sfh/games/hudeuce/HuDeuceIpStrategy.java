package sfh.games.hudeuce;

import sfh.Strategy;
import sfh.cards.CardSet;
import sfh.deucetoseven.DeuceToSevenHand;

public class HuDeuceIpStrategy extends BaseHuDeuceStrategy
    implements Strategy<HuDeuceGameState, HuDeuceIpStrategy, HuDeuceOopStrategy> {

  @Override
  public double mergeFrom(HuDeuceIpStrategy other, double epsilon) {
    // Totally ignore current strategy and copy the other one wherever available
    for (DeuceToSevenHand hand : other.discardStrategy.keySet()) {
      setDiscardStrategy(hand, other.getDiscardStrategy(hand));
    }
    return 0;
  }

  @Override
  public HuDeuceIpStrategy getBestResponse(HuDeuceGameState gs, HuDeuceOopStrategy villainStrategy) {
    DeuceToSevenHand ipHand = gs.getIpHand();
    HuDeuceIpStrategy bestStrategy = null;
    double bestValue = Double.MIN_VALUE;
    for (CardSet discard : ipHand.getAllValidDiscards()) {
      HuDeuceIpStrategy strategy = new HuDeuceIpStrategy();
      strategy.setDiscardStrategy(ipHand, discard);
      double value = gs.getValue(villainStrategy, strategy);
      // TODO: maybe do something smart if values are equal
      if (value > bestValue) {
        bestValue = value;
        bestStrategy = strategy;
      }
    }
    return bestStrategy;
  }

}
