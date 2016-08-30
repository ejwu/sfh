package sfh.badugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class DeckTest {
  @Test
  public void withoutCanOnlyBeCalledOnce() {
    Deck deck = new Deck();
    assertEquals(Card.DECK_LENGTH, deck.numCards());
    Card card = new Card("5c");
    Deck without = deck.without(card);
    assertEquals(Card.DECK_LENGTH - 1, without.numCards());
    try {
      without.without(card);
      fail("Shouldn't be able to remove same card twice");
    } catch (IllegalArgumentException expected) {
    }
  }
}
