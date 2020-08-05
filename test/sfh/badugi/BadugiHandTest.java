package sfh.badugi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.cards.Card;
import sfh.cards.CardSet;
import sfh.cards.Deck;

import java.io.FileWriter;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class BadugiHandTest {
  @Test
  public void constructHand() {
    BadugiHand hand = BadugiHand.createHand(new Card("Ac"), new Card("2d"), new Card("3h"), new Card("4s"));
    assertEquals("Ac2d3h4s", hand.toString());
  }

  @Test
  public void handWithDuplicateCardsShouldThrow() {
    try {
      BadugiHand.createHand(new Card("Ac"), new Card("2d"), new Card("3h"), new Card("Ac"));
      fail("Shouldn't be able to construct hand with duplicate cards");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void best24HandsShouldBeEqual() {
    List<BadugiHand> allHands = Lists.newArrayList(Deck.createDeck().generateAllHands());
    Collections.sort(allHands);
    BadugiHand bestHand = allHands.get(0);
    int count = 0;
    for (BadugiHand hand : allHands) {
      // 24 nut badugi hands = 4 * 3 * 2 * 1
      if (count < 24) {
        assertEquals(0, hand.compareTo(bestHand));
      } else {
        assertTrue(hand.compareTo(bestHand) > 0);
      }
      count++;
    }
  }

  // Sample broken comparison
  // 1671,   As2c3s5h,     As2c5h,        A25
  // 1671,   Ah2d3s5h,     Ah2d3s,        A23
  @Test
  public void a23ShouldBeatA25() {
    BadugiHand a25 = BadugiHand.createHand(new Card("As"), new Card("2c"), new Card("3s"), new Card("5h"));
    BadugiHand a23 = BadugiHand.createHand(new Card("Ah"), new Card("2d"), new Card("3s"), new Card("5h"));
    assertTrue(a23.isBetterThan(a25));
  }

  @Test
  public void a34ShouldBeatA25() {
    BadugiHand a25 = BadugiHand.createHand(new Card("As"), new Card("2c"), new Card("3s"), new Card("5h"));
    BadugiHand a34 = BadugiHand.createHand(new Card("Ah"), new Card("3d"), new Card("4s"), new Card("5s"));
    assertTrue(a34.isBetterThan(a25));
  }

  @Test
  public void wheelsShouldTie() {
    BadugiHand first = new BadugiHand("Ac2d3h4s");
    BadugiHand second = new BadugiHand("Ad2h3c4s");
    assertEquals(0, first.compareTo(second));
    assertFalse(first.isBetterThan(second));
  }

  @Test
  public void testValidDiscards() {
    BadugiHand hand = new BadugiHand("8dTc4cJs");
    Collection<CardSet> discards = hand.getAllValidDiscards();

    // 1 0-card discard + 4 1-card + 6 2-card + 4 3-card + 1 4-card = 16
    assertEquals(16, discards.size());

    // 0 discards
    assertTrue(discards.contains(new CardSet(new BitSet())));

    // 1 discard
    for (Card card : hand) {
      assertTrue(discards.contains(new CardSet(card)));
    }

    // 2 discards
    assertTrue(discards.contains(new CardSet("8d", "Tc")));
    assertTrue(discards.contains(new CardSet("8d", "4c")));
    assertTrue(discards.contains(new CardSet("8d", "Js")));
    assertTrue(discards.contains(new CardSet("Tc", "4c")));
    assertTrue(discards.contains(new CardSet("Tc", "Js")));
    assertTrue(discards.contains(new CardSet("4c", "Js")));

    // 3 discards
    for (Card card : hand) {
      assertTrue(discards.contains(hand.without(card)));
    }

    // 4 discards
    assertTrue(discards.contains(hand));
  }

  // Only used to generate the text cache of hand rankings
  @Ignore
  public void bsMethodForWritingCache() throws Exception {
    List<BadugiHand> allHands = Lists.newArrayList(Deck.createDeck().generateAllHands());
    Collections.sort(allHands);
    Multimap<Integer, BadugiHand> sortedHands = ArrayListMultimap.create();
    int handRank = 0;
    BadugiHand currentHand = allHands.get(0);
    for (BadugiHand hand : allHands) {
      if (hand.compareTo(currentHand) != 0) {
        handRank++;
        currentHand = hand;
      }
      sortedHands.put(handRank, hand);
    }

    StringBuilder csvString = new StringBuilder();
    csvString.append(String.format("%5s, %10s, %10s, %10s\n", "rank", "hand", "playable", "readable"));
    for (Integer rank : new TreeSet<>(sortedHands.keySet())) {
      for (BadugiHand hand : sortedHands.get(rank)) {
        csvString.append(String.format("%5d, %10s, %10s, %10s\n",
            rank, hand, hand.getPlayableHand(), hand.getPlayableRankString()));
      }
    }

    FileWriter writer = new FileWriter("./data/badugi/rankedHands.csv");
    writer.write(csvString.toString());
    writer.flush();
  }
}
