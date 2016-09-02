package sfh.badugi;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Deck is somewhat of a misnomer, it's more like a CardSet
 */
public class Deck extends CardSet {
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

  /**
   * @return all 4 card hands that can be generated from the current state of the deck
   */
  public Collection<Hand> generateAllHands() {
    // TODO: Iterate in a smarter order so we don't overgenerate hands by a factor of 24
    Set<Hand> allHands = new HashSet<>();
    for (Card first : this) {
      Deck withoutFirst = this.without(first);
      for (Card second : withoutFirst) {
        Deck withoutSecond = withoutFirst.without(second);
        for (Card third : withoutSecond) {
          Deck withoutThird = withoutSecond.without(third);
          for (Card fourth : withoutThird) {
            allHands.add(new Hand(first, second, third, fourth));
          }
        }
      }
    }
    return allHands;
  }

}
