package sfh.badugi;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.File;
import java.io.FileWriter;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class HandTest {
  @Test
  public void constructHand() {
    Hand hand = new Hand(new Card("Ac"), new Card("2d"), new Card("3h"), new Card("4s"));
    assertEquals("Ac2d3h4s", hand.toString());
  }

  @Test
  public void handWithDuplicateCardsShouldThrow() {
    try {
      new Hand(new Card("Ac"), new Card("2d"), new Card("3h"), new Card("Ac"));
      fail("Shouldn't be able to construct hand with duplicate cards");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void best24HandsShouldBeEqual() {
    List<Hand> allHands = Lists.newArrayList(new Deck().generateAllHands());
    Collections.sort(allHands);
    Hand bestHand = allHands.get(0);
    int count = 0;
    for (Hand hand : allHands) {
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
    Hand a25 = new Hand(new Card("As"), new Card("2c"), new Card("3s"), new Card("5h"));
    Hand a23 = new Hand(new Card("Ah"), new Card("2d"), new Card("3s"), new Card("5h"));
    assertTrue(a23.compareTo(a25) < 0);
  }

  // Only used to generate the text cache of hand rankings
  @Ignore
  public void bsMethodForWritingCache() throws Exception {
    List<Hand> allHands = Lists.newArrayList(new Deck().generateAllHands());
    Collections.sort(allHands);
    Multimap<Integer, Hand> sortedHands = ArrayListMultimap.create();
    int handRank = 0;
    Hand currentHand = allHands.get(0);
    for (Hand hand : allHands) {
      if (hand.compareTo(currentHand) != 0) {
        handRank++;
        currentHand = hand;
      }
      sortedHands.put(handRank, hand);
    }

    StringBuilder csvString = new StringBuilder();
    csvString.append(String.format("%5s, %10s, %10s, %10s\n", "rank", "hand", "playable", "readable"));
    for (Integer rank : new TreeSet<>(sortedHands.keySet())) {
      for (Hand hand : sortedHands.get(rank)) {
        csvString.append(String.format("%5d, %10s, %10s, %10s\n",
            rank, hand, hand.getPlayableHand(), hand.getPlayableRankString()));
      }
    }

    FileWriter writer = new FileWriter("./data/badugi/rankedHands.csv");
    writer.write(csvString.toString());
    writer.flush();
  }
}
