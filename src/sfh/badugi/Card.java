package sfh.badugi;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.ImmutableBiMap;

import java.util.BitSet;

public class Card {
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

  @VisibleForTesting
  static final ImmutableBiMap<Character, Integer> SUIT_MAP = ImmutableBiMap.<Character, Integer>builder()
      .put('c', 0)
      .put('d', 1)
      .put('h', 2)
      .put('s', 3).build();

  private static final int NUM_CARDS_IN_SUIT = RANK_MAP.size();
  @VisibleForTesting
  static final int DECK_LENGTH = RANK_MAP.size() * SUIT_MAP.size();

  private BitSet mask;
  // TODO: See if this actually saves any time
  // Cache the set bit to possibly avoid a linear lookup.
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

  @Override
  public String toString() {
    return Character.toString(RANK_MAP.inverse().get(cachedIndex % NUM_CARDS_IN_SUIT)) +
        Character.toString(SUIT_MAP.inverse().get(cachedIndex / NUM_CARDS_IN_SUIT));
  }
}
