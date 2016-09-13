package sfh.games.hubadugi;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import sfh.badugi.Card;
import sfh.badugi.CardSet;
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
   * @return all possible hands that can be drawn from the deck given the current hand and discard strategy, as well
   * as the remaining deck after drawing each hand
   */
  public Map<Hand, CardSet> generatePossibleHands(CardSet deck, Hand hand) {
    // TODO: remove when optimizing
    if (deck.hasAnyCard(hand)) {
      throw new IllegalArgumentException(String.format("Deck %s contains cards in hand %s", deck, hand));
    }
    List<Card> discards = discardStrategy.get(hand);
    if (discards.isEmpty()) {
      return ImmutableMap.of(hand, deck);
    }

    CardSet afterDiscard = hand;
    for (Card discard : discards) {
      // TODO: remove when optimizing
      if (!hand.hasCard(discard)) {
        throw new IllegalStateException(String.format("Hand %s cannot discard %s", hand, discards));
      }
      if (deck.hasCard(discard)) {
        throw new IllegalStateException(String.format("Deck %s contains discards %s", deck, discards));
      }
      afterDiscard = afterDiscard.without(discard);
    }

    // TODO: Could calculate right size for this to preallocate
    ImmutableMap.Builder<Hand, CardSet> generated = ImmutableMap.builder();
    for (CardSet drawn : deck.drawNCards(discards.size())) {
      generated.put(new Hand(afterDiscard.with(drawn.getCardArray())), deck.without(drawn));
    }

    return generated.build();
  }
}
