package sfh.games.hubadugi;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.badugi.Card;
import sfh.badugi.CardSet;
import sfh.badugi.Deck;
import sfh.badugi.Hand;

import java.util.BitSet;

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
  public void testGetValueIdentical1CardDraw() {
    // Both A23, drawing to spades
    Hand oop = deck.drawHand("Ac", "2d", "3h", "Kh");
    Hand ip = deck.drawHand("Ad", "2h", "3c", "Kc");
    CardSet oopDiscards = new CardSet("Kh");
    CardSet ipDiscards = new CardSet("Kc");

    testBoth(oop, ip, oopDiscards, ipDiscards, 0.5);
  }

  @Test
  public void testGetValuePatVs2CardDraw() {
    // A23
    Hand oop = deck.drawHand("Ad", "2h", "3c", "Kd");
    // 45, so no two card draw can beat OOP unless it makes a badugi
    // None of the dead cards help make a badugi.
    Hand ip = deck.drawHand("4d", "5h", "4h", "5d");
    CardSet oopDiscards = new CardSet(new BitSet());
    // Draw to 45
    CardSet ipDiscards = new CardSet("4h", "5d");

    // 44 cards in the deck.  IP can draw 10+11 nonpairing offsuit cards with the first card (all the clubs and spades
    // A-3 and 6-K, except the dead 3c).  There are 10 badugi making second cards if the first card is a club, or 9
    // if it's a spade, unless it's the 3s, in which case there are 10.  So IP's chance of winning is:
    // 10/44 * 10/43 (drawing a good club, then spade) +
    // 10/44 * 9/43 (drawing a non-3 spade, then a club) +
    // 1/44 * 10/43 (drawing the 3s, then a club)
    testBoth(oop, ip, oopDiscards, ipDiscards,
        1 - ((10.0 / 44 * 10.0 / 43) + (10.0 / 44 * 9.0 / 43) + (1.0 / 44 * 10.0 / 43)));
  }

  @Test
  public void testSame2CardDraws() {
    Hand oop = deck.drawHand("As", "2h", "Ah", "2s");
    Hand ip = deck.drawHand("Ad", "2c", "Ac", "2d");
    CardSet oopDiscards = new CardSet("Ah", "2s");
    CardSet ipDiscards = new CardSet("Ac", "2d");

    testBoth(oop, ip, oopDiscards, ipDiscards, 0.5);
  }

  @Test
  public void testSame2CardDrawsBadIpDeadCards() {
    Hand oop = deck.drawHand("As", "2h", "Ah", "2s");
    Hand ip = deck.drawHand("Ad", "2c", "Kc", "Kh");
    CardSet oopDiscards = new CardSet("Ah", "2s");
    CardSet ipDiscards = new CardSet("Kc", "Kh");

    // Actual value not calculated.
    // This value is higher than testSame2CardDraws because the IP hand is discarding useful cards.
    testBoth(oop, ip, oopDiscards, ipDiscards, 0.5095217223);
  }

  @Test
  public void testDifferent2CardDraws() {
    Hand oop = deck.drawHand("As", "2c", "Qs", "Qc");
    Hand ip = deck.drawHand("Ah", "3d", "Qh", "Qd");
    CardSet oopDiscards = new CardSet("Qs", "Qc");
    CardSet ipDiscards = new CardSet("Qh", "Qd");

    // Intuitively OOP has an advantage due to having a better draw.  Actual value hasn't been calculated, this test
    // mostly tests that the symmetric nature is correct.
    testBoth(oop, ip, oopDiscards, ipDiscards, 0.5582082882);
  }

  @Test
  public void testDifferent2CardDrawsBadIpDeadCards() {
    Hand oop = deck.drawHand("As", "2c", "Qs", "Qc");
    Hand ip = deck.drawHand("Ah", "3d", "Ks", "Kc");
    CardSet oopDiscards = new CardSet("Qs", "Qc");
    CardSet ipDiscards = new CardSet("Ks", "Kc");

    // Intuitively OOP has an advantage due to having a better draw.  Actual value hasn't been calculated, this test
    // mostly tests that the symmetric nature is correct.
    // The value for this is higher than testDifferent2CardDraws since the IP draw is worse due to discarding useful
    // cards.
    testBoth(oop, ip, oopDiscards, ipDiscards, 0.58574768);
  }


  @Test
  public void testDifferent2CardDrawsWorseIpHand() {
    Hand oop = deck.drawHand("As", "2c", "Qs", "Qc");
    Hand ip = deck.drawHand("3h", "4d", "Qh", "Qd");
    CardSet oopDiscards = new CardSet("Qs", "Qc");
    CardSet ipDiscards = new CardSet("Qh", "Qd");

    // Intuitively OOP has an advantage due to having a better draw.  Actual value hasn't been calculated, this test
    // mostly tests that the symmetric nature is correct.
    // The value for this is higher than testDifferent2CardDraws since the IP draw is worse due to keeping worse cards.
    testBoth(oop, ip, oopDiscards, ipDiscards, 0.568552595);
  }

  @Test
  public void testDifferent2CardDrawsEvenWorseIpHand() {
    Hand oop = deck.drawHand("As", "2c", "Qs", "Qc");
    Hand ip = deck.drawHand("7h", "8d", "Qh", "Qd");
    CardSet oopDiscards = new CardSet("Qs", "Qc");
    CardSet ipDiscards = new CardSet("Qh", "Qd");

    // Actual value hasn't been calculated.
    // The value for this is should be higher than testDifferent2CardDrawsWorseIpHand.
    testBoth(oop, ip, oopDiscards, ipDiscards, 0.62198129909);
  }

  // Test that situations are symmetric and generate the correct values when IP and OOP hands are swapped
  private void testBoth(Hand oopHand, Hand ipHand, CardSet oopDiscards, CardSet ipDiscards, double oopValue) {
    Deck deckWithoutHands = new Deck();
    for (Card card : oopHand) {
      deckWithoutHands.draw(card);
    }
    for (Card card : ipHand) {
      deckWithoutHands.draw(card);
    }

    HuBadugiGameState regular = new HuBadugiGameState(deckWithoutHands, oopHand, ipHand);
    HuBadugiOopStrategy regularOopStrategy = getOopStrategyWithDiscards(oopHand, oopDiscards);
    HuBadugiIpStrategy regularIpStrategy = getIpStrategyWithDiscards(ipHand, ipDiscards);
    assertDoubleEquals(oopValue, regular.getValue(regularOopStrategy, regularIpStrategy));

    HuBadugiGameState reversed = new HuBadugiGameState(deckWithoutHands, ipHand, oopHand);
    HuBadugiOopStrategy reversedOopStrategy = getOopStrategyWithDiscards(ipHand, ipDiscards);
    HuBadugiIpStrategy reversedIpStrategy = getIpStrategyWithDiscards(oopHand, oopDiscards);
    assertDoubleEquals(1 - oopValue, reversed.getValue(reversedOopStrategy, reversedIpStrategy));
  }

  // TODO: these are identical except for type, seems bad
  private HuBadugiOopStrategy getOopStrategyWithDiscards(Hand hand, CardSet toDiscard) {
    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy.setDiscardStrategy(hand, toDiscard);
    return strategy;
  }

  private HuBadugiIpStrategy getIpStrategyWithDiscards(Hand hand, CardSet toDiscard) {
    HuBadugiIpStrategy strategy = new HuBadugiIpStrategy();
    strategy.setDiscardStrategy(hand, toDiscard);
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