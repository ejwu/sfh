package sfh.badugi;

import com.google.common.collect.HashMultiset;
import com.google.common.collect.Lists;
import com.google.common.collect.Multiset;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A badugi hand of exactly 4 cards.
 */
public class Hand extends CardSet implements Comparable<Hand> {
  public Hand(Card c1, Card c2, Card c3, Card c4) {
    mask = new BitSet(Card.DECK_LENGTH);
    mask.or(c1.getMask());
    mask.or(c2.getMask());
    mask.or(c3.getMask());
    mask.or(c4.getMask());
    if (mask.cardinality() != 4) {
      throw new IllegalArgumentException("Hand must be made from 4 distinct cards");
    }
  }

  @Override
  public int compareTo(Hand other) {
    return hardCompareTo(other);
  }

  // TODO: replace this with some cache once it's generateable
  public int hardCompareTo(Hand other) {
    CardSet thisPlayableHand = getPlayableHand();
    CardSet otherPlayableHand = other.getPlayableHand();
    if (thisPlayableHand.numCards() > otherPlayableHand.numCards()) {
      return -1;
    } else if (otherPlayableHand.numCards() > thisPlayableHand.numCards()) {
      return 1;
    }
    // Playable hands are the same size
    if (isSamePlayableHand(this, other)) {
      return 0;
    }

    CardSet bestHand = getBestCardSet(Lists.newArrayList(thisPlayableHand, otherPlayableHand));
    if (bestHand.equals(thisPlayableHand)) {
      return -1;
    } else if (bestHand.equals(otherPlayableHand)) {
      return 1;
    }
    throw new IllegalStateException("Failed to compare " + this + " with " + other);
  }

  /**
   * @return The best subset of this hand that plays as a badugi hand.
   */
  public CardSet getPlayableHand() {
    // 4 separate suits and 4 separate ranks is always a badugi
    if (getSuitSet().size() == 4 && getRankSet().size() == 4) {
      return this;
    }

    List<CardSet> playableThreeCards = filterForPlayableHands(getThreeCardSubsets());
    if (!playableThreeCards.isEmpty()) {
      return getBestCardSet(playableThreeCards);
    }

    List<CardSet> playableTwoCards = filterForPlayableHands(getTwoCardSubsets());
    if (!playableTwoCards.isEmpty()) {
      return getBestCardSet(playableTwoCards);
    }

    List<Card> cards = getCards();
    Card lowestCard = cards.get(0);
    Card.Rank lowestRank = lowestCard.getRank();
    for (Card card : cards) {
      if (card.getRank().ordinal() < lowestRank.ordinal()) {
        lowestRank = card.getRank();
        lowestCard = card;
      }
    }
    return new CardSet(lowestCard);
  }

  /**
   * @param cardSets All card sets must be the same cardinality and be fully playable.
   * @return the best CardSet.  In the case of ties, return any equivalent CardSet
   */
  private CardSet getBestCardSet(List<CardSet> cardSets) {
    Collections.sort(cardSets, new Comparator<CardSet>() {
      @Override
      public int compare(CardSet first, CardSet second) {
        List<Card.Rank> firstRanks = new ArrayList<>(first.getRankSet());
        List<Card.Rank> secondRanks = new ArrayList<>(second.getRankSet());
        Collections.sort(firstRanks);
        Collections.sort(secondRanks);
        Collections.reverse(firstRanks);
        Collections.reverse(secondRanks);
        int index = 0;
        while (index < first.numCards()) {
          if (firstRanks.get(index) != secondRanks.get(index)) {
            return firstRanks.get(index).ordinal() - secondRanks.get(index).ordinal();
          }
          index++;
        }
        return 0;
      }
    });

    return cardSets.get(0);
  }

  private boolean isSamePlayableHand(CardSet first, CardSet second) {
    return first.getRankSet().equals(second.getRankSet());
  }

  private List<CardSet> getThreeCardSubsets() {
    List<Card> cards = getCards();
    List<CardSet> threeCards = new ArrayList<>();
    threeCards.add(new CardSet(cards.get(0), cards.get(1), cards.get(2)));
    threeCards.add(new CardSet(cards.get(0), cards.get(1), cards.get(3)));
    threeCards.add(new CardSet(cards.get(0), cards.get(2), cards.get(3)));
    threeCards.add(new CardSet(cards.get(1), cards.get(2), cards.get(3)));
    return threeCards;
  }

  private List<CardSet> getTwoCardSubsets() {
    List<Card> cards = getCards();
    List<CardSet> twoCards = new ArrayList<>();
    twoCards.add(new CardSet(cards.get(0), cards.get(1)));
    twoCards.add(new CardSet(cards.get(0), cards.get(2)));
    twoCards.add(new CardSet(cards.get(0), cards.get(3)));
    twoCards.add(new CardSet(cards.get(1), cards.get(2)));
    twoCards.add(new CardSet(cards.get(1), cards.get(3)));
    twoCards.add(new CardSet(cards.get(2), cards.get(3)));
    return twoCards;
  }

  private List<CardSet> filterForPlayableHands(Iterable<CardSet> cardSets) {
    List<CardSet> playable = new ArrayList<>();
    for (CardSet cardSet : cardSets) {
      if (cardSet.numCards() == cardSet.getRankSet().size() &&
          cardSet.numCards() == cardSet.getSuitSet().size()) {
        playable.add(cardSet);
      }
    }
    return playable;
  }
}
