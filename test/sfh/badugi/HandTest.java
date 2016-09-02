package sfh.badugi;

import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.Collections;
import java.util.List;

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
}
