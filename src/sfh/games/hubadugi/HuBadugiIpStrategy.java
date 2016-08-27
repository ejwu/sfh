package sfh.games.hubadugi;

import sfh.Strategy;

public class HuBadugiIpStrategy implements Strategy<HuBadugiGameState, HuBadugiIpStrategy, HuBadugiOopStrategy> {

  @Override
  public HuBadugiIpStrategy getBestResponse(HuBadugiGameState o, HuBadugiOopStrategy villain) {
    return null;
  }

  @Override
  public double mergeFrom(HuBadugiIpStrategy other, double epsilon) {
    return 0;
  }
}