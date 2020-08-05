package sfh.badugi;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

/**
 * A collection of BadugiHands.
 *
 * Notation: '+' means the given hand, as well as all better hands with the same number of playable cards.
 * Order of the cards listed doesn't matter.
 * Ex: QJT9+ is every Q-high badugi and better.
 * Ex: A25+ is A23, A24, A34, and A25.
 */
public class BadugiRange implements Iterable<BadugiHand> {
  // Unclear what the right collection type is.
  // List allows duplicates but sorting changes the iteration order, and contains() doesn't work well.
  // TreeSet doesn't work as is because BadugiHand.compareTo() isn't consistent with equals.
  // Going with List for now and sorting when outputting because it's not clear that the order matters yet.
  private List<BadugiHand> range = new ArrayList<>();

  /**
   * Comma separated list of range strings, e.g. "QJT9+, A25+"
   * @param rangeStrings
   */
  public BadugiRange(String rangeStrings) {
    for (String rangeString : rangeStrings.split(",")) {
      addRange(rangeString);
    }
  }

  public void add(BadugiHand hand) {
    range.add(hand);
  }

  public void addRange(String rangeString) {
    String s = rangeString.trim().toUpperCase();
    if (s.length() < 1 || s.length() > 5) {
      throw new IllegalArgumentException(rangeString + " is not a valid length");
    }
    if (!s.matches("[A-Z0-9+]*")) {
      throw new IllegalArgumentException(rangeString + " contains wrong characters");
    }
    // TODO: assert all chars in string are unique

    boolean endsWithPlus = s.endsWith("+");
    String rankOnly = s;
    if (endsWithPlus) {
      rankOnly = s.substring(0, s.length() - 1);
    }

    if (rankOnly.length() == 4) {
      add4CardHands(rankOnly, endsWithPlus);
    } else if (rankOnly.length() == 3) {
      add3CardHands(rankOnly, endsWithPlus);
    } else {
      throw new UnsupportedOperationException(rankOnly.length() + " size hands not implemented yet");
    }
    // TODO: every other hand type

  }

  private void add3CardHands(String threeCardHand, boolean endsWithPlus) {
    // Just hardcode a representative 3-card
    StringBuilder sb = new StringBuilder();
    sb.append(threeCardHand.charAt(0)).append('c');
    sb.append(threeCardHand.charAt(1)).append('d');
    sb.append(threeCardHand.charAt(2)).append('h');
    // Force last card to be paired and suited
    sb.append(threeCardHand.charAt(2)).append('d');
    BadugiHand hand = new BadugiHand(sb.toString());

    addHandsFromCache(hand, endsWithPlus);
  }

  private void add4CardHands(String fourCardHand, boolean endsWithPlus) {
    // Just hardcode a representative badugi
    StringBuilder sb = new StringBuilder();
    sb.append(fourCardHand.charAt(0)).append('c');
    sb.append(fourCardHand.charAt(1)).append('d');
    sb.append(fourCardHand.charAt(2)).append('h');
    sb.append(fourCardHand.charAt(3)).append('s');
    BadugiHand hand = new BadugiHand(sb.toString());

    addHandsFromCache(hand, endsWithPlus);
  }

  private void addHandsFromCache(BadugiHand hand, boolean endsWithPlus) {
    // TODO: Kind of an abuse of the cache, should make this more principled and probably move it to BadugiHand
    int rank = BadugiHand.HAND_RANK_CACHE.get(hand.getMask());
    for (BitSet bs : BadugiHand.RANK_HAND_CACHE.get(rank)) {
      range.add(new BadugiHand(bs));
    }
    if (endsWithPlus) {
      range.addAll(BadugiHand.getBetterHandsFromCache(hand, true));
    }
  }

  /**
   * @return the number of hands in this range
   */
  public int size() {
    return range.size();
  }

  @Override
  public String toString() {
    Collections.sort(range);
    StringBuilder sb = new StringBuilder("Range contains " + range.size() + " hands\n");

    // TODO: Coalesce into range strings instead of just listing every hand
    for (BadugiHand hand : range) {
      sb.append(String.format("%s (%s)\n", hand, hand.getPlayableRankString()));
    }
    return sb.toString();
  }

  @Override
  public Iterator<BadugiHand> iterator() {
    return range.iterator();
  }
}
