package sfh.badugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class CardSetTest {

  @Test
  public void withoutShouldFailIfCardNotPresent() {
    CardSet deck = Deck.createDeck();
    int size = deck.numCards();
    Card card = new Card("5c");
    CardSet without = deck.without(card);
    assertEquals(size - 1, without.numCards());
    try {
      without.without(card);
      fail("Shouldn't be able to remove same card twice");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test
  public void withShouldFailIfCardPresent() {
    CardSet deck = Deck.createDeck();
    Card card = new Card("3c");
    try {
      deck.with(card);
      fail("Shouldn't be able to add card already in card set");
    } catch (IllegalArgumentException expected) {
    }
    deck = deck.without(card);
    deck.with(card);
  }

  @Test
  public void testHasCard() {
    CardSet deck = Deck.createDeck();
    for (Card card : deck) {
      assertTrue(deck.hasCard(card));
    }
    Card removed = new Card("Ks");
    deck = deck.without(removed);
    assertFalse(deck.hasCard(removed));
  }

  @Test
  public void testhasAnyCard() {
    CardSet deck = Deck.createDeck();
    CardSet toRemove = new CardSet(new Card("Ac"), new Card("2c"), new Card("5d"), new Card("Qs"));
    CardSet leftToRemove = new CardSet(toRemove.mask);
    while (leftToRemove.numCards() > 0) {
      assertTrue(deck.hasAnyCard(toRemove));
      Card cardToRemove = leftToRemove.getCards().get(0);
      deck = deck.without(cardToRemove);
      leftToRemove = leftToRemove.without(cardToRemove);
    }
    assertFalse(deck.hasAnyCard(toRemove));
  }

  @Test
  public void getCardsShouldBeIdentical() {
    CardSet deck = Deck.createDeck();
    Card[] asArray = deck.getCardArray();
    List<Card> asList = deck.getCards();
    assertEquals(asArray.length, asList.size());
    int index = 0;
    for (Card card : asArray) {
      assertEquals(card, asList.get(index++));
    }

  }
}
