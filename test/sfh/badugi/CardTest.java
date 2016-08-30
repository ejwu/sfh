package sfh.badugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;

@RunWith(JUnit4.class)
public class CardTest {
  @Test
  public void testFromAndToString() {
    for (Character rank : Card.RANK_MAP.keySet()) {
      for (Character suit : Card.SUIT_MAP.keySet()) {
        String cardString = Character.toString(rank) + Character.toString(suit);
        Card card = new Card(cardString);
        assertEquals(cardString, card.toString());
      }
    }
  }

  @Test
  public void testFromBitSet() {
    for (int i = 0; i < Card.DECK_LENGTH; i++) {
      BitSet bitSet = new BitSet(Card.DECK_LENGTH);
      bitSet.set(i);
      Card card = new Card(bitSet);
      // Backdoor test to make sure that cachedIndex doesn't blow up when initializing with a BitSet.
      card.toString();
    }
  }
}