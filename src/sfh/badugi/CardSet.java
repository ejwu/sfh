package sfh.badugi;

import com.google.common.collect.Lists;

import java.util.ArrayList;
import java.util.BitSet;
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
    StringBuilder sb = new StringBuilder();
    for (Card card : cards) {
      sb.append(card);
    }
    return sb.toString();
  }
}
