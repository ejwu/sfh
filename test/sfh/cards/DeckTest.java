package sfh.cards;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class DeckTest {
  @Test
  public void newDeckIsRightSize() {
    Deck deck = Deck.createDeck();
    assertEquals(Card.DECK_LENGTH, deck.numCards());
  }

  @Test
  public void fullDeckShouldBeIterable() {
    Deck deck = Deck.createDeck();
    int count = 0;
    for (Card card : deck) {
      count++;
    }
    assertEquals(Card.DECK_LENGTH, count);
  }

  @Test
  public void partialDeckShouldBeIterable() {
    CardSet deck = Deck.createDeck();
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
    Deck deck = Deck.createDeck();
    int expectedHands = 52 * 51 * 50 * 49 / 24;
    assertEquals(expectedHands, deck.generateAllHands().size());
  }

  @Test
  public void generateAllHandsShouldEqualDraw4() {
    Deck deck = Deck.createDeck();
    assertEquals(deck.generateAllHands().size(), deck.drawNCards(4).size());
  }
}
