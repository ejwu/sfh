package sfh.games.common.hudraw;

import sfh.cards.Card;
import sfh.cards.CardSet;

import java.util.HashMap;
import java.util.Map;

public abstract class BaseHuDrawStrategy<H extends DrawHand> {

  // Mapping for every hand to its list of cards to discard
  // TODO: Make this dependent on previous discards, number of cards discarded if in position...probably more
  protected Map<H, CardSet> discardStrategy = new HashMap<>();

  protected BaseHuDrawStrategy() {
    setDefaultZeroDiscardStrategy();
  }

  public void setDiscardStrategy(H hand, CardSet toDiscard) {
    for (Card discard : toDiscard) {
      if (!hand.hasCard(discard)) {
        throw new IllegalArgumentException(String.format("Hand %s cannot discard %s", hand, toDiscard));
      }
    }
    discardStrategy.put(hand, toDiscard);
  }

  public CardSet getDiscardStrategy(H hand) {
    return discardStrategy.get(hand);
  }

  protected abstract void setDefaultZeroDiscardStrategy();

}
