package sfh.games.hubadugi;

import sfh.Strategy;
import sfh.badugi.CardSet;
import sfh.badugi.Hand;

public class HuBadugiIpStrategy extends BaseHuBadugiStrategy
    implements Strategy<HuBadugiGameState, HuBadugiIpStrategy, HuBadugiOopStrategy> {

  @Override
  public double mergeFrom(HuBadugiIpStrategy other, double epsilon) {
    for (Hand hand : other.discardStrategy.keySet()) {
      setDiscardStrategy(hand, other.getDiscardStrategy(hand));
    }
    return 0;
  }

  @Override
  public HuBadugiIpStrategy getBestResponse(HuBadugiGameState gs, HuBadugiOopStrategy villain) {
    Hand ipHand = gs.getIpHand();
    HuBadugiIpStrategy bestStrategy = null;
    double bestValue = Double.MAX_VALUE;
    for (CardSet discard : ipHand.getAllValidDiscards()) {
      System.out.println("checking discard: " + discard);
      HuBadugiIpStrategy strategy = new HuBadugiIpStrategy();
      strategy.setDiscardStrategy(ipHand, discard);
      double value = gs.getValue(villain, strategy);
      System.out.println(value);
      // TODO: maybe do something smart if values are equal
      if (value < bestValue) {
        bestValue = value;
        bestStrategy = strategy;
      }
    }
    return bestStrategy;
  }

}
