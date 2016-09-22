package sfh.games.hubadugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.badugi.Card;
import sfh.badugi.CardSet;
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
    assertDoubleEquals(0.5, gameState.getValue(getDefaultOopStrategy(), getDefaultIpStrategy()));
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
    assertDoubleEquals(0, gameState.getValue(getDefaultOopStrategy(), getDefaultIpStrategy()));
  }

  @Test
  public void testGetValueOneOopHandVsIpRangeNeedingBadugi() {
    Deck deck = new Deck();
    // A23K
    Hand oop = deck.drawHand("Ac", "2d", "3h", "Ks");
    // A24
    Hand ip = deck.drawHand("Ad", "2h", "4s", "Kh");

    HuBadugiIpStrategy ipStrategy = new HuBadugiIpStrategy();
    // IP hand discards the K
    ipStrategy.setDiscardStrategy(ip, new CardSet(new Card("Kh")));

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    // 44 cards left in deck, 3 and 5-Q of clubs are winners (9).  IP wins 9/44 times,  OOP wins 35/44, or
    // .79545454...
    double value = gameState.getValue(getDefaultOopStrategy(), ipStrategy);
    assertDoubleEquals(35.0 / 44.0, value);
  }

  @Test
  public void testGetValueOopRangeNeedingBadugiVsOneIpHand() {
    Deck deck = new Deck();
    // 358
    Hand oop = deck.drawHand("3d", "5s", "8c", "8s");
    // A25
    Hand ip = deck.drawHand("As", "2h", "5d", "5c");

    HuBadugiOopStrategy oopStrategy = new HuBadugiOopStrategy();
    // OOP hand draws to 358
    oopStrategy.setDiscardStrategy(oop, new CardSet(new Card("8s")));

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    // 44 cards in deck, A, 4, 6-7, 9-K of hearts are winners (9) for OOP.  2h is in IP's hand.
    // EV for OOP is 9/44 == .2045454545...
    double value = gameState.getValue(oopStrategy, getDefaultIpStrategy());
    assertDoubleEquals(9.0 / 44.0, value);
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