package sfh.badugi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Multimap;
import com.google.common.collect.MultimapBuilder;
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
import java.util.SortedSet;

/**
 * A badugi hand of exactly 4 cards.
 */
public class BadugiHand extends CardSet implements Comparable<BadugiHand> {
  // A cache of hand to rank
  public static ImmutableMap<BitSet, Integer> HAND_RANK_CACHE = initializeCache();
  // A cache of rank to hands, ordered by rank.  TreeMultimap is helpful to keep ranks in
  // order and allows searching for all hands better than a given rank.
  public static Multimap<Integer, BitSet> RANK_HAND_CACHE = initializeReverseCache();


  public static BadugiHand createHand(Card c1, Card c2, Card c3, Card c4) {
    BitSet mask = new BitSet(Card.DECK_LENGTH);
    mask.or(c1.getMask());
    mask.or(c2.getMask());
    mask.or(c3.getMask());
    mask.or(c4.getMask());
    return new BadugiHand(mask);
  }

  public BadugiHand(CardSet cardSet) {
    this(cardSet.getMask());
  }

  public BadugiHand(Collection<Card> cards) {
    super(new CardSet(cards.toArray(new Card[]{})));
  }

  public BadugiHand(BitSet mask) {
    super(mask);
    if (mask.cardinality() != 4) {
      throw new IllegalArgumentException("BadugiHand must be made from 4 distinct cards");
    }
  }

  /**
   * @param handString must be a valid 8 character string representing 4 distinct cards
   */
  public BadugiHand(String handString) {
    super(new Card(handString.substring(0, 2)), new Card(handString.substring(2, 4)),
        new Card(handString.substring(4, 6)), new Card(handString.substring(6)));
  }

  public BadugiHand(CardSet... cardSets) {
    super(cardSets);
  }

  private static ImmutableMap<BitSet, Integer> initializeCache() {
    try (BufferedReader reader = new BufferedReader(new FileReader("../data/badugi/rankedHands.csv"))) {
      ImmutableMap.Builder<BitSet, Integer> cache = ImmutableMap.builder();
      reader.readLine(); // Skip header
      String line = reader.readLine();
      while (line != null) {
        String[] split = line.split(", ");
        cache.put(new BadugiHand(split[1].trim()).mask, Integer.valueOf(split[0].trim()));
        line = reader.readLine();
      }
      return cache.build();
    } catch (IOException e) {
      // Just blow up if we can't read the file
      System.out.println("Can't find path, current path (Intellij might want \"/src\" appended to this): " + System.getProperty("user.dir"));
      throw new RuntimeException(e);
    }
  }

  private static Multimap<Integer, BitSet> initializeReverseCache() {
    // Per Guava documentation, keySets from this Multimap can be safely cast to SortedSet
    Multimap<Integer, BitSet> reverseCache = MultimapBuilder.treeKeys().arrayListValues().build();
    for (BitSet hand : HAND_RANK_CACHE.keySet()) {
      reverseCache.put(HAND_RANK_CACHE.get(hand), hand);
    }

    return reverseCache;
  }

  private static final int BEST_3_CARD_RANK = 715; // A23
  private static final int BEST_2_CARD_RANK = 1001; // A2
  private static final int BEST_1_CARD_RANK = 1079; // A

  /**
   * TODO: This is kind of a hack and badly organized and needs to be looked over some more
   * @param onlySameCardinality if true, only return hands of the same cardinality as the given hand
   * @return all hands better than the given hand, not including the given hand.
   */
  static Collection<BadugiHand> getBetterHandsFromCache(BadugiHand hand, boolean onlySameCardinality) {
    List<BadugiHand> betterHands = new ArrayList<>();
    int threshold = 0;
    if (onlySameCardinality) {
      switch (hand.getPlayableHand().numCards()) {
        case 3:
          threshold = BEST_3_CARD_RANK;
          break;
        case 2:
          threshold = BEST_2_CARD_RANK;
          break;
        case 1:
          threshold = BEST_1_CARD_RANK;
          break;
        // 4 card hands can just use 0 threshold, as can calls allowing different cardinality
      }
    }
    // Per Guava documentation for MultimapBuilder, this cast is safe
    SortedSet<Integer> keySet = (SortedSet<Integer>) RANK_HAND_CACHE.keySet();
    SortedSet<Integer> betterRanks = keySet.subSet(threshold, HAND_RANK_CACHE.get(hand.getMask()));
    for (Integer betterRank : betterRanks) {
      for (BitSet bs : RANK_HAND_CACHE.get(betterRank)) {
        betterHands.add(new BadugiHand(bs));
      }
    }

    return betterHands;
  }

  /**
   * Note that this is not consistent with equals(), and as such putting these in a TreeSet or similar will not work.
   */
  @Override
  public int compareTo(BadugiHand other) {
    return easyCompareTo(other);
  }

  public int easyCompareTo(BadugiHand other) {
    return HAND_RANK_CACHE.get(this.mask) - HAND_RANK_CACHE.get(other.mask);
  }

  // TODO: replace this with some cache once it's generateable
  public int hardCompareTo(BadugiHand other) {
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

  public boolean isBadugi() {
    return getPlayableHand().getCards().size() == 4;
  }

  /**
   * Because I can never remember what compareTo() returns.
   */
  public boolean isBetterThan(BadugiHand other) {
    return this.compareTo(other) < 0;
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
