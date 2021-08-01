package sfh.games.common.hudraw;

import sfh.cards.Card;
import sfh.cards.CardSet;

import java.util.BitSet;
import java.util.Collection;

public abstract class DrawHand extends CardSet {

  protected DrawHand(Collection<Card> cards) {
    super(new CardSet(cards.toArray(new Card[]{})));
  }

  protected DrawHand(BitSet mask) {
    super(mask);
  }

  protected DrawHand(Card... cards) {
    super(cards);
  }

  protected DrawHand(CardSet... cardSets) {
    super(cardSets);
  }

  protected DrawHand(String cardString) {
    super(cardString);
  }

  // TODO: do some better combinatorics instead of brute forcing every hand size
  abstract public Collection<CardSet> getAllValidDiscards();

}
