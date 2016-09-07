package sfh.badugi;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class CardSet implements Iterable<Card> {
  protected BitSet mask;

  public CardSet(Card... cards) {
    mask = new BitSet();
    for (Card card : cards) {
      mask.or(card.getMask());
    }
  }

  public CardSet(BitSet mask) {
    this.mask = mask;
  }

  // TODO: unify this and List version?
  public Card[] getCardArray() {
    Card[] cards = new Card[mask.cardinality()];
    int index = 0;
    for (Card card : this) {
      cards[index++] = card;
    }
    return cards;
  }

  public List<Card> getCards() {
    List<Card> cards = Lists.newArrayListWithCapacity(mask.cardinality());
    for (Card card : this) {
      cards.add(card);
    }
    return cards;
  }

  public int numCards() {
    return mask.cardinality();
  }

  public Set<Card.Suit> getSuitSet() {
    Set<Card.Suit> suits = new HashSet<>();
    for (Card card : this) {
      suits.add(card.getSuit());
    }
    return suits;
  }

  public Set<Card.Rank> getRankSet() {
    Set<Card.Rank> ranks = new HashSet<>();
    for (Card card : this) {
      ranks.add(card.getRank());
    }
    return ranks;
  }

  /**
   * @return all combinations of numToDraw cards that can be drawn from this card set
   */
  public Collection<CardSet> drawNCards(int numToDraw) {
    Set<CardSet> generated = new HashSet<>();
    if (numToDraw == 0) {
      return generated;
    }

    for (Card card : getCards()) {
      if (numToDraw == 1) {
        generated.add(new CardSet(card));
      } else {
        CardSet without = this.without(card);
        for (CardSet cardSet : without.drawNCards(numToDraw - 1)) {
          generated.add(cardSet.with(card));
        }
      }
    }
    return generated;
  }

  /**
   * @return a copy of this card set without the given card.
   * @throws IllegalArgumentException if the card isn't currently in the card set.
   */
  public CardSet without(Card card) {
    if (!mask.intersects(card.getMask())) {
      throw new IllegalArgumentException("Deck does not contain card: " + card);
    }
    BitSet copy = new BitSet(Card.DECK_LENGTH);
    copy.or(mask);
    copy.xor(card.getMask());
    return new CardSet(copy);
  }

  /**
   * @return a copy of this card set with the given cards.
   * @throws java.lang.IllegalArgumentException if the card is already in the set
   */
  public CardSet with(Card... cards) {
    BitSet copy = new BitSet(Card.DECK_LENGTH);
    copy.or(mask);
    for (Card card : cards) {
      if (copy.intersects(card.getMask())) {
        throw new IllegalArgumentException(
            String.format("Card set [%s] already contains card [%s]", this.toString(), card));
      }
      copy.or(card.getMask());
    }
    return new CardSet(copy);
  }

  public boolean hasCard(Card card) {
    return mask.intersects(card.getMask());
  }

  public boolean hasAnyCard(CardSet cardSet) {
    return mask.intersects(cardSet.mask);
  }

  @Override
  public Iterator<Card> iterator() {
    return new CardIterator(mask);
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    CardSet hand = (CardSet) o;

    return !(mask != null ? !mask.equals(hand.mask) : hand.mask != null);
  }

  @Override
  public int hashCode() {
    return mask != null ? mask.hashCode() : 0;
  }

  @Override
  public String toString() {
    List<Card> cards = getCardsInIncreasingRankOrder();
    StringBuilder sb = new StringBuilder();
    for (Card card : cards) {
      sb.append(card);
    }
    return sb.toString();
  }

  private List<Card> getCardsInIncreasingRankOrder() {
    List<Card> cards = getCards();
    Collections.sort(cards, new Comparator<Card>() {
      @Override
      public int compare(Card first, Card second) {
        if (first.getRank() != second.getRank()) {
          return first.getRank().ordinal() - second.getRank().ordinal();
        }
        return first.getSuit().ordinal() - second.getSuit().ordinal();
      }
    });
    return cards;
  }

  public String getRankString() {
    StringBuilder sb = new StringBuilder();
    for (Card card : getCardsInIncreasingRankOrder()) {
      sb.append(card.toString().charAt(0));
    }
    return sb.toString();
  }
}
