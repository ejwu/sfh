package sfh.games.hubadugi;

import sfh.badugi.Card;
import sfh.badugi.Deck;
import sfh.badugi.Hand;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BaseHuBadugiStrategy {
  // Mapping for every hand to its list of cards to discard
  // TODO: Make this dependent on previous discards, number of cards discarded if in position...probably more
  protected Map<Hand, List<Card>> discardStrategy = new HashMap<>();

  public BaseHuBadugiStrategy() {
    for (BitSet mask : Hand.HAND_RANK_CACHE.keySet()) {
      discardStrategy.put(new Hand(mask), new ArrayList<Card>());
    }
  }

  /**
   * @return all possible hands that can be drawn from the deck given the current hand and discard strategy
   */
  public List<Hand> generatePossibleHands(Deck deck, Hand hand) {

    return null;
  }
}
