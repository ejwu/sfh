package sfh.badugi;

import java.util.BitSet;

public class Hand {
  private BitSet mask;

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
  public String toString() {
    StringBuilder sb = new StringBuilder();
    int nextSetBit = mask.nextSetBit(0);
    while (nextSetBit >= 0) {
      sb.append(new Card(nextSetBit).toString());
      nextSetBit = mask.nextSetBit(nextSetBit + 1);
    }
    return sb.toString();
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    Hand hand = (Hand) o;

    return !(mask != null ? !mask.equals(hand.mask) : hand.mask != null);

  }

  @Override
  public int hashCode() {
    return mask != null ? mask.hashCode() : 0;
  }
}
