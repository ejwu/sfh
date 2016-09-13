package sfh.games.hubadugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.badugi.Card;
import sfh.badugi.CardSet;
import sfh.badugi.Deck;
import sfh.badugi.Hand;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class BaseHuBadugiStrategyTest {
  @Test
  public void drawingForHandWithNoDiscardStrategyShouldThrow() {
    BaseHuBadugiStrategy strategy = new BaseHuBadugiStrategy();
    Deck deck = new Deck();
    Hand hand = new Hand(deck.draw("Ac"), deck.draw("2h"), deck.draw("3d"), deck.draw("4s"));
    try {
      strategy.generatePossibleHands(deck, hand);
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void drawingForHandWithZeroDiscardStrategy() {
    BaseHuBadugiStrategy strategy = new BaseHuBadugiStrategy();
    strategy.setDefaultZeroDiscardStrategy();
    Deck deck = new Deck();
    Hand hand = new Hand(deck.draw("Ac"), deck.draw("2h"), deck.draw("3d"), deck.draw("4s"));
    Map<Hand, CardSet> drawn = strategy.generatePossibleHands(deck, hand);
    assertEquals(1, drawn.size());
    assertTrue(drawn.containsKey(hand));
    assertEquals(deck, drawn.get(hand));
  }

  @Test
  public void discardStrategyMustBeValid() {
    BaseHuBadugiStrategy strategy = new BaseHuBadugiStrategy();
    Hand hand = new Hand(new Card("Ac"), new Card("2h"), new Card("3d"), new Card("4s"));
    try {
      strategy.setDiscardStrategy(hand, new CardSet(new Card("Ad")));
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void discardOne() {
    BaseHuBadugiStrategy strategy = new BaseHuBadugiStrategy();
    Deck deck = new Deck();
    Card keep1 = deck.draw("Ac");
    Card keep2 = deck.draw("2d");
    Card keep3 = deck.draw("3h");
    Card discard = deck.draw("Kh");
    Hand hand = new Hand(keep1, keep2, keep3, discard);
    strategy.setDiscardStrategy(hand, new CardSet(discard));
    Map<Hand, CardSet> drawn = strategy.generatePossibleHands(deck, hand);

    // Drawing 1 card from a 48 card deck should have 48 possible results
    assertEquals(48, drawn.size());

    // All draw results should be distinct
    Set<Hand> drawResults = new HashSet<>(drawn.keySet());
    assertEquals(48, drawResults.size());

    // All deck results should be distinct
    Set<CardSet> deckResults = new HashSet<>(drawn.values());
    assertEquals(48, deckResults.size());

    // All drawn hands should contain the kept cards and not the discarded card
    for (Hand drawnHand : drawn.keySet()) {
      assertTrue(drawnHand.hasCard(keep1));
      assertTrue(drawnHand.hasCard(keep2));
      assertTrue(drawnHand.hasCard(keep3));
      assertFalse(drawnHand.hasCard(discard));
    }
  }
}
