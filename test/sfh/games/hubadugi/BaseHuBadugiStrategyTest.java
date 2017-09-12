package sfh.games.hubadugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.badugi.BadugiHand;
import sfh.cards.Card;
import sfh.cards.CardSet;
import sfh.cards.Deck;

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
  public void drawingForHandWithZeroDiscardStrategy() {
    BaseHuBadugiStrategy strategy = new BaseHuBadugiStrategy();
    strategy.setDefaultZeroDiscardStrategy();
    Deck deck = Deck.createDeck();
    BadugiHand hand = BadugiHand.createHand(deck.draw("Ac"), deck.draw("2h"), deck.draw("3d"), deck.draw("4s"));
    Map<BadugiHand, CardSet> drawn = strategy.generatePossibleHands(deck, hand);
    assertEquals(1, drawn.size());
    assertTrue(drawn.containsKey(hand));
    assertEquals(deck, drawn.get(hand));
  }

  @Test
  public void discardStrategyMustBeValid() {
    BaseHuBadugiStrategy strategy = new BaseHuBadugiStrategy();
    BadugiHand hand = BadugiHand.createHand(new Card("Ac"), new Card("2h"), new Card("3d"), new Card("4s"));
    try {
      strategy.setDiscardStrategy(hand, new CardSet(new Card("Ad")));
      fail();
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void discardOne() {
    BaseHuBadugiStrategy strategy = new BaseHuBadugiStrategy();
    Deck deck = Deck.createDeck();
    Card keep1 = deck.draw("Ac");
    Card keep2 = deck.draw("2d");
    Card keep3 = deck.draw("3h");
    Card discard = deck.draw("Kh");
    BadugiHand hand = BadugiHand.createHand(keep1, keep2, keep3, discard);
    strategy.setDiscardStrategy(hand, new CardSet(discard));
    Map<BadugiHand, CardSet> drawn = strategy.generatePossibleHands(deck, hand);

    // Drawing 1 card from a 48 card deck should have 48 possible results
    assertEquals(48, drawn.size());

    // All draw results should be distinct
    Set<BadugiHand> drawResults = new HashSet<>(drawn.keySet());
    assertEquals(48, drawResults.size());

    // All deck results should be distinct
    Set<CardSet> deckResults = new HashSet<>(drawn.values());
    assertEquals(48, deckResults.size());

    // All drawn hands should contain the kept cards and not the discarded card
    for (BadugiHand drawnHand : drawn.keySet()) {
      assertTrue(drawnHand.hasCard(keep1));
      assertTrue(drawnHand.hasCard(keep2));
      assertTrue(drawnHand.hasCard(keep3));
      assertFalse(drawnHand.hasCard(discard));
    }
  }

  @Test
  public void generatePossibleHandsWithDupes() {
    BaseHuBadugiStrategy strategy = new BaseHuBadugiStrategy();
    Deck deck = Deck.createDeck();
    Card keep1 = deck.draw("Ac");
    Card keep2 = deck.draw("2d");
    Card discard1 = deck.draw("Kh");
    Card discard2 = deck.draw("Ks");
    BadugiHand hand = BadugiHand.createHand(keep1, keep2, discard1, discard2);
    strategy.setDiscardStrategy(hand, new CardSet(discard1, discard2));

    Map<BadugiHand, CardSet> drawn = strategy.generatePossibleHands(deck, hand);

    assertEquals(1128, drawn.keySet().size());
  }
}
