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
    Hand oop = new Hand(deck.draw("Ac"), deck.draw("2d"), deck.draw("3h"), deck.draw("4s"));
    Hand ip = new Hand(deck.draw("Ad"), deck.draw("2h"), deck.draw("3s"), deck.draw("4c"));

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(0, gameState.getValue(new HuBadugiOopStrategy(), new HuBadugiIpStrategy()));
  }

  @Test
  public void testGetValueOneHandOopWins() {
    Deck deck = new Deck();
    // A234
    Hand oop = new Hand(deck.draw("Ac"), deck.draw("2d"), deck.draw("3h"), deck.draw("4s"));
    // A235
    Hand ip = new Hand(deck.draw("Ad"), deck.draw("2h"), deck.draw("3s"), deck.draw("5c"));

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(1, gameState.getValue(new HuBadugiOopStrategy(), new HuBadugiIpStrategy()));
  }

  @Test
  public void testGetValueOneHandIpWins() {
    Deck deck = new Deck();
    // 75
    Hand oop = new Hand(deck.draw("5c"), deck.draw("5d"), deck.draw("7h"), deck.draw("Kh"));
    // KQJ
    Hand ip = new Hand(deck.draw("Jd"), deck.draw("Qh"), deck.draw("Kc"), deck.draw("Ks"));

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(-1, gameState.getValue(new HuBadugiOopStrategy(), new HuBadugiIpStrategy()));
  }

}