package sfh.cards;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableBiMap;
import com.google.common.collect.ImmutableMap;

import java.util.BitSet;

public class Card {
  public enum Suit {
    CLUB, DIAMOND, HEART, SPADE
  }

  public enum Rank {
    ACE, TWO, THREE, FOUR, FIVE, SIX, SEVEN, EIGHT, NINE, TEN, JACK, QUEEN, KING;
  }

  @VisibleForTesting
  static final ImmutableBiMap<Character, Integer> RANK_MAP = ImmutableBiMap.<Character, Integer>builder()
      .put('A', 0)
      .put('2', 1)
      .put('3', 2)
      .put('4', 3)
      .put('5', 4)
      .put('6', 5)
      .put('7', 6)
      .put('8', 7)
      .put('9', 8)
      .put('T', 9)
      .put('J', 10)
      .put('Q', 11)
      .put('K', 12).build();

  private static final ImmutableMap<Character, Rank> RANK_ENUM_MAP = ImmutableMap.<Character, Rank>builder()
      .put('A', Rank.ACE)
      .put('2', Rank.TWO)
      .put('3', Rank.THREE)
      .put('4', Rank.FOUR)
      .put('5', Rank.FIVE)
      .put('6', Rank.SIX)
      .put('7', Rank.SEVEN)
      .put('8', Rank.EIGHT)
      .put('9', Rank.NINE)
      .put('T', Rank.TEN)
      .put('J', Rank.JACK)
      .put('Q', Rank.QUEEN)
      .put('K', Rank.KING).build();

  @VisibleForTesting
  static final ImmutableBiMap<Character, Integer> SUIT_MAP = ImmutableBiMap.<Character, Integer>builder()
      .put('c', 0)
      .put('d', 1)
      .put('h', 2)
      .put('s', 3).build();

  private static final int NUM_CARDS_IN_SUIT = RANK_MAP.size();
  public static final int DECK_LENGTH = RANK_MAP.size() * SUIT_MAP.size();

  private BitSet mask;
  // TODO: See if this actually saves any time
  // Cache the set bit to possibly avoid a linear lookup.  This should always be set after construction.
  private int cachedIndex;

  public Card(BitSet bitSet) {
    if (bitSet.cardinality() != 1) {
      throw new IllegalArgumentException("BitSet must have exactly 1 bit set");
    }
    cachedIndex = bitSet.nextSetBit(0);
    if (cachedIndex >= DECK_LENGTH) {
      throw new IllegalArgumentException("Index of set bit must be less than deck length");
    }
    this.mask = bitSet;
  }

  Card(int bitIndex) {
    cachedIndex = bitIndex;
    mask = new BitSet(DECK_LENGTH);
    mask.set(cachedIndex);
  }

  public Card(String cardString) {
    if (cardString.length() != 2) {
      throw new IllegalArgumentException("Card string must be 2 characters");
    }
    cachedIndex = (NUM_CARDS_IN_SUIT * SUIT_MAP.get(cardString.charAt(1))) + RANK_MAP.get(cardString.charAt(0));

    mask = new BitSet(DECK_LENGTH);
    mask.set(cachedIndex);
  }

  public BitSet getMask() {
    return mask;
  }

  public Suit getSuit() {
    char suitChar = SUIT_MAP.inverse().get(cachedIndex / NUM_CARDS_IN_SUIT);
    if ('c' == suitChar) {
      return Suit.CLUB;
    } else if ('d' == suitChar) {
      return Suit.DIAMOND;
    } else if ('h' == suitChar) {
      return Suit.HEART;
    } else if ('s' == suitChar) {
      return Suit.SPADE;
    }
    throw new IllegalStateException("Card is not correct");
  }

  public Rank getRank() {
    Character rankChar = RANK_MAP.inverse().get(cachedIndex % NUM_CARDS_IN_SUIT);
    if (!RANK_ENUM_MAP.containsKey(rankChar)) {
      throw new IllegalStateException("Rank is not correct");
    }
    return RANK_ENUM_MAP.get(rankChar);
  }

  @Override
  public String toString() {
    return Character.toString(RANK_MAP.inverse().get(cachedIndex % NUM_CARDS_IN_SUIT)) +
        Character.toString(SUIT_MAP.inverse().get(cachedIndex / NUM_CARDS_IN_SUIT));
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Card card = (Card) o;

    return !(mask != null ? !mask.equals(card.mask) : card.mask != null);

  }

  @Override
  public int hashCode() {
    return mask != null ? mask.hashCode() : 0;
  }
}
