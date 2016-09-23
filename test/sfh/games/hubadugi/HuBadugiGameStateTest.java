package sfh.games.hubadugi;

import org.junit.Before;
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
  private Deck deck;

  @Before
  public void setUp() {
    deck = new Deck();
  }

  // TODO: Move this to some base test case
  private void assertDoubleEquals(double expected, double actual) {
    assertEquals(expected, actual, 0.000000001);
  }

  @Test
  public void testGetValueOneEqualHand() {
    Hand oop = deck.drawHand("Ac", "2d", "3h", "4s");
    Hand ip = deck.drawHand("Ad", "2h", "3s", "4c");

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(0.5, gameState.getValue(getDefaultOopStrategy(), getDefaultIpStrategy()));
  }

  @Test
  public void testGetValueOneHandOopWins() {
    // A234
    Hand oop = deck.drawHand("Ac", "2d", "3h", "4s");
    // A235
    Hand ip = deck.drawHand("Ad", "2h", "3s", "5c");

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(1, gameState.getValue(getDefaultOopStrategy(), getDefaultIpStrategy()));
  }

  @Test
  public void testGetValueOneHandIpWins() {
    // 75
    Hand oop = deck.drawHand("5c", "5d", "7h", "Kh");
    // KQJ
    Hand ip = deck.drawHand("Jd", "Qh", "Kc", "Ks");

    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);
    assertDoubleEquals(0, gameState.getValue(getDefaultOopStrategy(), getDefaultIpStrategy()));
  }

  @Test
  public void testGetValueOneOopHandVsIpRangeNeedingBadugi() {
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

  @Test
  public void testGetValueSameOneCardDrawRanges() {
    Hand oop = deck.drawHand("Ac", "2d", "3h", "Kh");
    Hand ip = deck.drawHand("Ad", "2h", "3c", "Kc");
    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);

    // Both hands draw to A23
    HuBadugiOopStrategy oopStrategy = getOopStrategyWithDiscards(oop, "Kh");
    HuBadugiIpStrategy ipStrategy = getIpStrategyWithDiscards(ip, "Kc");
    // Same hands, same strategies, should be equal EV
    assertDoubleEquals(0.5, gameState.getValue(oopStrategy, ipStrategy));
  }

  @Test
  public void testGetValuePatOopVs2CardDrawIp() {
    // A23
    Hand oop = deck.drawHand("Ad", "2h", "3c", "Kd");
    // 45, so no two card draw can beat OOP unless it makes a badugi
    // None of the dead cards help make a badugi.
    Hand ip = deck.drawHand("4d", "5h", "4h", "5d");
    HuBadugiGameState gameState = new HuBadugiGameState(deck, oop, ip);

    // Draw to 45
    HuBadugiIpStrategy ipStrategy = getIpStrategyWithDiscards(ip, "4h", "5d");

    // 44 cards in the deck.  IP can draw 10+11 nonpairing offsuit cards with the first card (all the clubs and spades
    // A-3 and 6-K, except the dead 3c).  There are 10 badugi making second cards if the first card is a club, or 9
    // if it's a spade, unless it's the 3s, in which case there are 10.  So IP's chance of winning is:
    // 10/44 * 10/43 (drawing a good club, then spade) +
    // 10/44 * 9/43 (drawing a non-3 spade, then a club) +
    // 1/44 * 10/43 (drawing the 3s, then a club)
    assertDoubleEquals(1 - ((10.0 / 44 * 10.0 / 43) + (10.0 / 44 * 9.0 / 43) + (1.0 / 44 * 10.0 / 43)),
        gameState.getValue(getDefaultOopStrategy(), ipStrategy));
  }

  // TODO: these are identical except for type, seems bad
  private HuBadugiOopStrategy getOopStrategyWithDiscards(Hand hand, String... toDiscard) {
    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy.setDiscardStrategy(hand, new CardSet(toDiscard));
    return strategy;
  }

  private HuBadugiIpStrategy getIpStrategyWithDiscards(Hand hand, String... toDiscard) {
    HuBadugiIpStrategy strategy = new HuBadugiIpStrategy();
    strategy.setDiscardStrategy(hand, new CardSet(toDiscard));
    return strategy;
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