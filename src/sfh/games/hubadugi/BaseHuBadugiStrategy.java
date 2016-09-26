package sfh.games.hubadugi;

import com.google.common.collect.ImmutableMap;
import sfh.badugi.Card;
import sfh.badugi.CardSet;
import sfh.badugi.Hand;

import java.util.BitSet;
import java.util.HashMap;
import java.util.Map;

public class BaseHuBadugiStrategy {
  // Mapping for every hand to its list of cards to discard
  // TODO: Make this dependent on previous discards, number of cards discarded if in position...probably more
  protected Map<Hand, CardSet> discardStrategy = new HashMap<>();

  public BaseHuBadugiStrategy() {
    setDefaultZeroDiscardStrategy();
  }

  public void setDefaultZeroDiscardStrategy() {
    for (BitSet mask : Hand.HAND_RANK_CACHE.keySet()) {
      setDiscardStrategy(new Hand(mask), new CardSet(new BitSet()));
    }
  }

  public void setDiscardStrategy(Hand hand, CardSet toDiscard) {
    for (Card discard : toDiscard) {
      if (!hand.hasCard(discard)) {
        throw new IllegalArgumentException(String.format("Hand %s cannot discard %s", hand, toDiscard));
      }
    }
    discardStrategy.put(hand, toDiscard);
  }

  public CardSet getDiscardStrategy(Hand hand) {
    return discardStrategy.get(hand);
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
    CardSet discards = discardStrategy.get(hand);
    if (discards == null) {
      throw new IllegalArgumentException("Strategy does not contain discard strategy for hand " + hand);
    }

    if (discards.numCards() == 0) {
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
    for (CardSet drawn : deck.drawNCards(discards.numCards())) {
      generated.put(new Hand(afterDiscard.with(drawn.getCardArray())), deck.without(drawn));
    }

    return generated.build();
  }
}
