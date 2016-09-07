package sfh.badugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class DeckTest {
  @Test
  public void newDeckIsRightSize() {
    Deck deck = new Deck();
    assertEquals(Card.DECK_LENGTH, deck.numCards());
  }

  @Test
  public void fullDeckShouldBeIterable() {
    Deck deck = new Deck();
    int count = 0;
    for (Card card : deck) {
      count++;
    }
    assertEquals(Card.DECK_LENGTH, count);
  }

  @Test
  public void partialDeckShouldBeIterable() {
    CardSet deck = new Deck();
    deck = deck.without(new Card("5d"));
    deck = deck.without(new Card("Ks"));
    deck = deck.without(new Card("Ac"));
    int count = 0;
    for (Card card : deck) {
      count++;
    }
    assertEquals(Card.DECK_LENGTH - 3, count);
  }

  @Test
  public void generateAllHandShouldHaveRightNumber() {
    Deck deck = new Deck();
    int expectedHands = 52 * 51 * 50 * 49 / 24;
    assertEquals(expectedHands, deck.generateAllHands().size());
  }

  @Test
  public void generateAllHandsShouldEqualDraw4() {
    Deck deck = new Deck();
    assertEquals(deck.generateAllHands().size(), deck.drawNCards(4).size());
  }
}
