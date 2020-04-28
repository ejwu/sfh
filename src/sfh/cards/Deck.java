package sfh.cards;

import com.google.common.collect.Sets;
import sfh.badugi.BadugiHand;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

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
    Set<BadugiHand> allHands = new HashSet<>();
    for (Set<Card> hand : Sets.combinations(Sets.newHashSet(getCards()), 4)) {
      allHands.add(new BadugiHand(hand));
    }
    return allHands;
  }
}
