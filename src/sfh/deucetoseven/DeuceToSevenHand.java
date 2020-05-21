package sfh.deucetoseven;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Iterables;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import com.google.common.collect.Streams;
import sfh.cards.Card;
import sfh.cards.CardSet;
import sfh.cards.Deck;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class DeuceToSevenHand extends CardSet implements Comparable<DeuceToSevenHand> {
  @VisibleForTesting
  static final String ERROR_WRONG_NUM_CARDS = "2-7 hand must have 5 cards";

  @VisibleForTesting
  DeuceToSevenHand(Card... cards) {
    super(cards);
    if (cards.length != 5) {
      throw new IllegalArgumentException(ERROR_WRONG_NUM_CARDS);
    }
  }

  @VisibleForTesting
  DeuceToSevenHand(String cardString) {
    super(cardString);
    if (getCards().size() != 5) {
      throw new IllegalArgumentException(ERROR_WRONG_NUM_CARDS);
    }
  }

  /**
   * Note that this implementation is not consistent with equals(), since different hands
   * can have the same rank in 2-7.
   */
  @Override
  public int compareTo(DeuceToSevenHand other) {
    return hardCompareTo(other);
  }

  private boolean onlyFirstExists(Card.Rank first, Card.Rank second) {
    return first != null && second == null;
  }

  private boolean onlySecondExists(Card.Rank first, Card.Rank second) {
    return first == null && second != null;
  }

  private boolean bothExist(Card.Rank first, Card.Rank second) {
    return first != null && second != null;
  }

  /**
   * @return a value suitable to use as the return value of compareTo() if either argument is non-null, or null if
   * both arguments are null
   */
  private Integer compareExistentRanks(Card.Rank first, Card.Rank second) {
    if (onlyFirstExists(first, second)) {
      return -1;
    } else if (onlySecondExists(first, second)) {
      return 1;
    } else if (bothExist(first, second)) {
      // Hack for 2-7 because the Ace is always high and Card.Rank.compareTo() thinks it's low
      if (first == Card.Rank.ACE && second != Card.Rank.ACE) {
        return -1;
      } else if (second == Card.Rank.ACE && first != Card.Rank.ACE) {
        return 1;
      }
      // Lowball hands, so reverse the usual rankings
      return first.compareTo(second) * -1;
    }
    return null;
  }

  /**
   * Placeholder compareTo() used to generate a cache of hand values.
   */
  private int hardCompareTo(DeuceToSevenHand other) {
    // Straight flush
    Integer toReturn = compareExistentRanks(getStraightFlush(), other.getStraightFlush());
    if (toReturn != null) {
      return toReturn;
    }

    // Quads
    toReturn = compareExistentRanks(getQuads(), other.getQuads());
    // Could also compare kickers, but tied quads shouldn't be possible
    if (toReturn != null) {
      return toReturn;
    }

    // Full house
    toReturn = compareExistentRanks(getFullHouse(), other.getFullHouse());
    // Could also compare pairs, but shouldn't be possible
    if (toReturn != null) {
      return toReturn;
    }

    // Flush
    if (isFlush() && other.isFlush()) {
      Iterator<Card> itOther = Lists.reverse(other.getCardsInIncreasingRankOrder()).iterator();
      for (Card card : Lists.reverse(getCardsInIncreasingRankOrder())) {
        Card otherCard = itOther.next();
        toReturn = compareExistentRanks(card.getRank(), otherCard.getRank());
        if (toReturn != null && toReturn != 0) {
          return toReturn;
        }
      }
      if (toReturn == 0) {
        return toReturn;
      }
    } else if (isFlush()) {
      return -1;
    } else if (other.isFlush()) {
      return 1;
    }

    // Straight
    toReturn = compareExistentRanks(getStraight(), other.getStraight());
    if (toReturn != null) {
      return toReturn;
    }

    // Trips
    toReturn = compareExistentRanks(getTrips(), other.getTrips());
    if (toReturn != null) {
      return toReturn;
    }

    // Two pair
    if (getTwoPair() != null && other.getTwoPair() != null) {
      return compareListOfRanks(getTwoPair(), other.getTwoPair());
    } else if (getTwoPair() != null) {
      return -1;
    } else if (other.getTwoPair() != null) {
      return 1;
    }

    // Pair
    if (getOnePair() != null && other.getOnePair() != null) {
      return compareListOfRanks(getOnePair(), other.getOnePair());
    } else if (getOnePair() != null) {
      return -1;
    } else if (other.getOnePair() != null) {
      return 1;
    }

    // High card
    return compareListOfRanks(
        getCardsInDecreasingRankOrder().stream().map(
            Card::getRank).collect(Collectors.toList()),
        other.getCardsInDecreasingRankOrder().stream().map(
            Card::getRank).collect(Collectors.toList()));

  }

  /**
   * @return the highest rank in this hand if it's a straight flush, otherwise null.
   */
  @VisibleForTesting
  Card.Rank getStraightFlush() {
    if (isFlush() && isStraight()) {
      return getCardsInIncreasingRankOrder().get(4).getRank();
    }
    return null;
  }

  /**
   * @return the rank of the quads iff this hand has quads, otherwise null
   */
  @VisibleForTesting
  Card.Rank getQuads() {
    Map<Card.Rank, Integer> rankCounts = getRankCounts();
    for (Card.Rank rank : rankCounts.keySet()) {
      if (rankCounts.get(rank) == 4) {
        return rank;
      }
    }
    return null;
  }

  /**
   * @return the rank of the trips in a full house if it exists, otherwise null
   */
  @VisibleForTesting
  Card.Rank getFullHouse() {
    Map<Card.Rank, Integer> rankCounts = getRankCounts();

    if (rankCounts.size() == 2) {
      for (Card.Rank rank : rankCounts.keySet()) {
        if (rankCounts.get(rank) == 3) {
          return rank;
        }
      }
    }
    return null;
  }

  @VisibleForTesting
  Card.Rank getStraight() {
    if (isStraight()) {return getCardsInIncreasingRankOrder().get(4).getRank();
    }
    return null;
  }

  /**
   * Note that straights in 2-7 are different in that the Ace always plays high, so 2345A is not a straight.
   */
  @VisibleForTesting
  boolean isStraight() {
    Card.Rank previous = null;
    for (Card card : getCardsInIncreasingRankOrder()) {
      if (!isNextRank(previous, card.getRank())) {
        return false;
      }
      previous = card.getRank();
    }
    return true;
  }

  @VisibleForTesting
  Card.Rank getTrips() {
    Map<Card.Rank, Integer> rankCounts = getRankCounts();
    // Trips must have 3 distinct ranks
    if (rankCounts.size() == 3) {
      for (Card.Rank rank : rankCounts.keySet()) {
        if (rankCounts.get(rank) == 3) {
          return rank;
        }
      }
      // Should be two pair if we have 3 ranks but no trips
    }
    return null;
  }

  /**
   * If the hand is two pair, return the rank of the high pair, low pair, then kicker in that order.
   * Otherwise, return null.
   */
  @VisibleForTesting
  List<Card.Rank> getTwoPair() {
    Map<Card.Rank, Integer> rankCounts = getRankCounts();
    if (rankCounts.size() != 3) {
      return null;
    }
    List<Card.Rank> ranks = new ArrayList<>();
    Card.Rank kicker = null;
    for (Card card : getCardsInDecreasingRankOrder()) {
      if (rankCounts.get(card.getRank()) == 2) {
        // Pair goes first
        ranks.add(card.getRank());
      } else if (rankCounts.get(card.getRank()) == 1) {
        if (kicker != null) {
          throw new IllegalStateException("Two pair shouldn't have multiple kickers");
        }
        // Kicker gets added last
        kicker = card.getRank();
      } else {
        throw new IllegalStateException("Two pair shouldn't have ranks with cardinality other than 1 or 2");
      }
    }
    ranks.add(kicker);

    return ranks;
  }

  /**
   * If the hand is one pair, return the rank of the pair, then the kickers in descending order.
   */
  @VisibleForTesting
  List<Card.Rank> getOnePair() {
    Map<Card.Rank, Integer> rankCounts = getRankCounts();
    if (rankCounts.size() != 4) {
      return null;
    }

    List<Card.Rank> ranks = new ArrayList<>();

    for (Card card : getCardsInDecreasingRankOrder()) {
      if (rankCounts.get(card.getRank()) == 2) {
        // Pair goes first
        ranks.add(0, card.getRank());
      } else {
        ranks.add(card.getRank());
      }
    }

    return ranks;
  }

  private int compareListOfRanks(Iterable<Card.Rank> first, Iterable<Card.Rank> second) {
    if (Iterables.size(first) != Iterables.size(second)) {
      throw new IllegalArgumentException("Both lists of ranks must be same size to be comparable");
    }

    Iterator<Card.Rank> i = first.iterator();
    Iterator<Card.Rank> i2 = second.iterator();

    while (i.hasNext() && i2.hasNext()) {
      Card.Rank firstRank = i.next();
      Card.Rank secondRank = i2.next();
      int rankComparison = compareDeuceToSevenRank(firstRank, secondRank);
      if (rankComparison != 0) {
        return rankComparison;
      }
    }

    return 0;
  }

  private int compareDeuceToSevenRank(Card.Rank first, Card.Rank second) {
    if (first == second) {
      return 0;
    } else if (first == Card.Rank.ACE) {
      return -1;
    } else if (second == Card.Rank.ACE) {
      return 1;
    } else {
      return first.compareTo(second) * -1;
    }
  }

  private Map<Card.Rank, Integer> getRankCounts() {
    Map<Card.Rank, Integer> rankCounts = new HashMap<>();
    for (Card card : this) {
      rankCounts.put(card.getRank(), rankCounts.getOrDefault(card.getRank(), 0) + 1);
    }
    return rankCounts;
  }

  private boolean isNextRank(Card.Rank previous, Card.Rank current) {
    if (previous == null) {
      return true;
    }
    // Hack because the Ace is always high in 2-7
    if (previous == Card.Rank.KING && current == Card.Rank.ACE) {
      return true;
    }
    return (current.ordinal()) - 1 == previous.ordinal();
  }

  @Override
  protected List<Card> getCardsInIncreasingRankOrder() {
    List<Card> ordered = super.getCardsInIncreasingRankOrder();
    List<Card> aces = new ArrayList<>();
    for (Card card : ordered) {
      if (card.getRank() == Card.Rank.ACE) {
        aces.add(card);
      }
    }
    for (Card ace : aces) {
      ordered.remove(0);
      ordered.add(ace);
    }
    return ordered;
  }

  protected List<Card> getCardsInDecreasingRankOrder() {
    List<Card> cards = getCardsInIncreasingRankOrder();
    Collections.reverse(cards);
    return cards;
  }

  public Collection<DeuceToSevenHand> generateAllHands() {
    Set<Card> deck = Sets.newHashSet(Deck.createDeck().getCards());
    List<DeuceToSevenHand> allHands = new ArrayList<>();
    for (Set<Card> hand : Sets.combinations(deck, 5)) {
      allHands.add(new DeuceToSevenHand(hand.toArray(new Card[]{})));
    }
    return allHands;
  }

}
