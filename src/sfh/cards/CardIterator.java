package sfh.cards;

import java.util.BitSet;
import java.util.Iterator;

public class CardIterator implements Iterator<Card> {
  private BitSet mask;
  private int currentIndex = 0;

  public CardIterator(BitSet mask) {
    this.mask = new BitSet(mask.size());
    this.mask.or(mask);
  }

  @Override
  public Card next() {
    int nextSetBit = mask.nextSetBit(currentIndex);
    if (nextSetBit < 0) {
      throw new IllegalStateException("No more cards");
    }
    Card card = new Card(nextSetBit);
    currentIndex = nextSetBit + 1;
    return card;
  }

  @Override
  public boolean hasNext() {
    return mask.nextSetBit(currentIndex) >= 0;
  }

  @Override
  public void remove() {
    throw new UnsupportedOperationException();
  }
}
