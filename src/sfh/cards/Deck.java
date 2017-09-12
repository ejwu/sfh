package sfh.cards;

import sfh.badugi.BadugiHand;

import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * Deck is somewhat of a misnomer, it's more like a CardSet
 */
public class Deck extends CardSet {
  private Deck(BitSet mask) {
    super(mask);
  }

  /**
   * Create a full 52 card deck.
   */
  public static Deck createDeck() {
    BitSet mask = new BitSet(Card.DECK_LENGTH);
    for (int i = 0; i < Card.DECK_LENGTH; i++) {
      mask.set(i);
    }
    return new Deck(mask);
  }

  /**
   * Convenience method for removing 4 cards from a deck and forming them into a BadugiHand.
   */
  public BadugiHand drawHand(String card1, String card2, String card3, String card4) {
    return BadugiHand.createHand(draw(card1), draw(card2), draw(card3), draw(card4));
  }

  /**
   * @return all 4 card hands that can be generated from the current state of the deck
   */
  public Collection<BadugiHand> generateAllHands() {
    // TODO: Iterate in a smarter order so we don't overgenerate hands by a factor of 24
    Set<BadugiHand> allHands = new HashSet<>();
    for (Card first : this) {
      CardSet withoutFirst = this.without(first);
      for (Card second : withoutFirst) {
        CardSet withoutSecond = withoutFirst.without(second);
        for (Card third : withoutSecond) {
          CardSet withoutThird = withoutSecond.without(third);
          for (Card fourth : withoutThird) {
            allHands.add(BadugiHand.createHand(first, second, third, fourth));
          }
        }
      }
    }
    return allHands;
  }

}
