package sfh.badugi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import sfh.cards.Card;
import sfh.cards.CardSet;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * A badugi hand of exactly 4 cards.
 */
public class Hand extends CardSet implements Comparable<Hand> {
  public static ImmutableMap<BitSet, Integer> HAND_RANK_CACHE = initializeCache();


  public static Hand createHand(Card c1, Card c2, Card c3, Card c4) {
    BitSet mask = new BitSet(Card.DECK_LENGTH);
    mask.or(c1.getMask());
    mask.or(c2.getMask());
    mask.or(c3.getMask());
    mask.or(c4.getMask());
    return new Hand(mask);
  }

  public Hand(CardSet cardSet) {
    this(cardSet.getMask());
  }

  public Hand(BitSet mask) {
    super(mask);
    if (mask.cardinality() != 4) {
      throw new IllegalArgumentException("Hand must be made from 4 distinct cards");
    }
  }

  /**
   * @param handString must be a valid 8 character string representing 4 distinct cards
   */
  public Hand(String handString) {
    super(new Card(handString.substring(0, 2)), new Card(handString.substring(2, 4)),
        new Card(handString.substring(4, 6)), new Card(handString.substring(6)));
  }

  private static ImmutableMap<BitSet, Integer> initializeCache() {
    try (BufferedReader reader = new BufferedReader(new FileReader("../data/badugi/rankedHands.csv"))) {
      ImmutableMap.Builder<BitSet, Integer> cache = ImmutableMap.builder();
      reader.readLine(); // Skip header
      String line = reader.readLine();
      while (line != null) {
        String[] split = line.split(", ");
        cache.put(new Hand(split[1].trim()).mask, Integer.valueOf(split[0].trim()));
        line = reader.readLine();
      }
      return cache.build();
    } catch (IOException e) {
      // Just blow up if we can't read the file
      throw new RuntimeException(e);
    }
  }

  @Override
  public int compareTo(Hand other) {
    return easyCompareTo(other);
  }

  public int easyCompareTo(Hand other) {
    return HAND_RANK_CACHE.get(this.mask) - HAND_RANK_CACHE.get(other.mask);
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
    if (isSamePlayableHand(thisPlayableHand, otherPlayableHand)) {
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

  public Collection<CardSet> getAllValidDiscards() {
    List<CardSet> discards = new ArrayList<>();
    // discard 0
    discards.add(new CardSet(new BitSet()));

    // discard 1
    for (Card card : this) {
      discards.add(new CardSet(card));
    }

    // discard 2
    discards.addAll(getTwoCardSubsets());

    // discard 3
    discards.addAll(getThreeCardSubsets());

    // discard 4
    discards.add(this);

    return discards;
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

  public String getPlayableRankString() {
    return getPlayableHand().getRankString();
  }
}
