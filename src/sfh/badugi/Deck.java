package sfh.badugi;

import java.util.BitSet;

/**
 * Deck is somewhat of a misnomer, it's more like a CardSet
 */
public class Deck {
  private BitSet mask;

  /**
   * Create a full 52 card deck.
   */
  public Deck() {
    mask = new BitSet(Card.DECK_LENGTH);
    for (int i = 0; i < Card.DECK_LENGTH; i++) {
      mask.set(i);
    }
  }

  private Deck(BitSet mask) {
    this.mask = mask;
  }

  public int numCards() {
    return mask.cardinality();
  }

  /**
   * @return a copy of this deck without the given card.
   * @throws IllegalArgumentException if the card isn't currently in the deck.
   */
  public Deck without(Card card) {
    if (!mask.intersects(card.getMask())) {
      throw new IllegalArgumentException("Deck does not contain card: " + card);
    }
    BitSet copy = new BitSet(Card.DECK_LENGTH);
    copy.or(mask);
    copy.xor(card.getMask());
    return new Deck(copy);
  }

}
