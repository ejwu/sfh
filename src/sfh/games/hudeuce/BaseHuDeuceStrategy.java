package sfh.games.hudeuce;

import com.google.common.collect.ImmutableMap;
import sfh.cards.Card;
import sfh.cards.CardSet;
import sfh.deucetoseven.DeuceToSevenHand;
import sfh.games.common.hudraw.BaseHuDrawStrategy;
import sfh.games.common.hudraw.DrawHand;

import java.util.BitSet;
import java.util.Map;

public class BaseHuDeuceStrategy extends BaseHuDrawStrategy<DeuceToSevenHand> {

  public BaseHuDeuceStrategy() {
    super();
  }

  @Override
  public void setDefaultZeroDiscardStrategy() {
    for (BitSet mask : DeuceToSevenHand.HAND_RANK_CACHE.keySet()) {
      setDiscardStrategy(new DeuceToSevenHand(mask), new CardSet(new BitSet()));
    }
  }

  // TODO: Unify this with the almost exact copy in the badugi code
  public Map<DeuceToSevenHand, CardSet> generatePossibleHands(CardSet deck, DeuceToSevenHand hand) {
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
        throw new IllegalStateException(String.format("DeuceToSevenHand %s cannot discard %s", hand, discards));
      }
      if (deck.hasCard(discard)) {
        throw new IllegalStateException(String.format("Deck %s contains discards %s", deck, discards));
      }
      afterDiscard = afterDiscard.without(discard);
    }

    // TODO: Could calculate right size for this to preallocate
    ImmutableMap.Builder<DeuceToSevenHand, CardSet> generated = ImmutableMap.builder();
    for (CardSet drawn : deck.drawNCards(discards.numCards())) {
      generated.put(new DeuceToSevenHand(afterDiscard.with(drawn.getCardArray())), deck.without(drawn));
    }

    return generated.build();

  }

}