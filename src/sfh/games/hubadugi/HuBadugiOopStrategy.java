package sfh.games.hubadugi;

import sfh.Strategy;

public class HuBadugiOopStrategy extends BaseHuBadugiStrategy
    implements Strategy<HuBadugiGameState, HuBadugiOopStrategy, HuBadugiIpStrategy> {
  @Override
  public double mergeFrom(HuBadugiOopStrategy other, double epsilon) {
    return 0;
  }

  @Override
  public HuBadugiOopStrategy getBestResponse(HuBadugiGameState o, HuBadugiIpStrategy villain) {
    return null;
  }
}
