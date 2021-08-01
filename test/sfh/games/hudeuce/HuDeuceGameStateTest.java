package sfh.games.hudeuce;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.cards.Card;
import sfh.cards.CardSet;
import sfh.cards.Deck;
import sfh.deucetoseven.DeuceToSevenHand;
import sfh.games.hubadugi.BaseStrategyTest;

@RunWith(JUnit4.class)
public class HuDeuceGameStateTest extends BaseStrategyTest {
  private Deck deck;

  @Before
  public void setUp() {
    deck = Deck.createDeck();
  }

  @Test
  public void twoEqualPatHands() {
    DeuceToSevenHand oop = draw("2c3c4c5c7d");
    DeuceToSevenHand ip = draw("2d3d4d5d7h");

    HuDeuceGameState gs = new HuDeuceGameState(deck, oop, ip);
    assertDoubleEquals(0.5, gs.getValue(getDefaultOopDeuceStrategy(), getDefaultIpDeuceStrategy()));
  }

  @Test
  public void twoEqualOneCardDraws() {
    DeuceToSevenHand oop = draw("2c3d4d5d5h");
    HuDeuceOopStrategy oopStrategy = new HuDeuceOopStrategy();
    oopStrategy.setDiscardStrategy(oop, new CardSet(new Card("5h")));

    DeuceToSevenHand ip = draw("2d3s4s5s5c");
    HuDeuceIpStrategy ipStrategy = new HuDeuceIpStrategy();
    ipStrategy.setDiscardStrategy(ip, new CardSet(new Card("5c")));

    HuDeuceGameState gs = new HuDeuceGameState(deck, oop, ip);
    assertDoubleEquals(0.5, gs.getValue(oopStrategy, ipStrategy));
  }

  @Test
  public void oneCardDrawVsPat() {
    DeuceToSevenHand oop = draw("2c3d4d7d7h");
    HuDeuceOopStrategy oopStrategy = new HuDeuceOopStrategy();
    oopStrategy.setDiscardStrategy(oop, new CardSet(new Card("7h")));

    DeuceToSevenHand ip = draw("3s4s5s7s8c");

    HuDeuceGameState gs = new HuDeuceGameState(deck, oop, ip);
    // Oop should have 4 outs - 3 5's, 4 6's, and 3 8's
    assertDoubleEquals(10.0 / deck.numCards(), gs.getValue(oopStrategy, getDefaultIpDeuceStrategy()));
  }

  private DeuceToSevenHand draw(String handString) {
    return new DeuceToSevenHand(deck.drawHand(handString));
  }

  private HuDeuceOopStrategy getDefaultOopDeuceStrategy() {
    HuDeuceOopStrategy strategy = new HuDeuceOopStrategy();
    strategy.setDefaultZeroDiscardStrategy();
    return strategy;
  }

  private HuDeuceIpStrategy getDefaultIpDeuceStrategy() {
    HuDeuceIpStrategy strategy = new HuDeuceIpStrategy();
    strategy.setDefaultZeroDiscardStrategy();
    return strategy;
  }
}