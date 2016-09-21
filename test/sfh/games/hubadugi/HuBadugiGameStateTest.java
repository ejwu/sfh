package sfh.games.hubadugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.badugi.Deck;
import sfh.badugi.Hand;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class HuBadugiGameStateTest {
  private void assertDoubleEquals(double expected, double actual) {
    assertEquals(expected, actual, 0.000000001);
  }

  @Test
  public void testGetValueOneEqualHand() {
    Deck deck = new Deck();
    Hand oop = deck.drawHand("Ac", "2d", "3h", "4s");
    Hand ip = deck.drawHand("Ad", "2h", "3s", "4c");

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(0, gameState.getValue(getDefaultOopStrategy(), getDefaultIpStrategy()));
  }

  @Test
  public void testGetValueOneHandOopWins() {
    Deck deck = new Deck();
    // A234
    Hand oop = deck.drawHand("Ac", "2d", "3h", "4s");
    // A235
    Hand ip = deck.drawHand("Ad", "2h", "3s", "5c");

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(1, gameState.getValue(getDefaultOopStrategy(), getDefaultIpStrategy()));
  }

  @Test
  public void testGetValueOneHandIpWins() {
    Deck deck = new Deck();
    // 75
    Hand oop = deck.drawHand("5c", "5d", "7h", "Kh");
    // KQJ
    Hand ip = deck.drawHand("Jd", "Qh", "Kc", "Ks");

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(-1, gameState.getValue(getDefaultOopStrategy(), getDefaultIpStrategy()));
  }

  private HuBadugiOopStrategy getDefaultOopStrategy() {
    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy.setDefaultZeroDiscardStrategy();
    return strategy;
  }

  private HuBadugiIpStrategy getDefaultIpStrategy() {
    HuBadugiIpStrategy strategy = new HuBadugiIpStrategy();
    strategy.setDefaultZeroDiscardStrategy();
    return strategy;
  }

}