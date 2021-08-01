package sfh.games.hubadugi;

import static org.junit.Assert.assertEquals;

public class BaseStrategyTest {
  // TODO: Move this to some base test case
  protected void assertDoubleEquals(double expected, double actual) {
    assertEquals(expected, actual, 0.000000001);
  }

  protected HuBadugiOopStrategy getDefaultOopBadugiStrategy() {
    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy.setDefaultZeroDiscardStrategy();
    return strategy;
  }

  protected HuBadugiIpStrategy getDefaultIpBadugiStrategy() {
    HuBadugiIpStrategy strategy = new HuBadugiIpStrategy();
    strategy.setDefaultZeroDiscardStrategy();
    return strategy;
  }


}
