package sfh.games.hubadugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.badugi.BadugiHand;
import sfh.cards.Card;
import sfh.cards.CardSet;
import sfh.cards.Deck;

import java.util.BitSet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class HuBadugiOopStrategyTest extends BaseStrategyTest {
  @Test
  public void shouldDraw4WithNoWheelCardsAgainstWheel() {
    Deck deck = Deck.createDeck();
    BadugiHand oopHand = deck.drawHand("Kc", "Kd", "Kh", "Ks");
    BadugiHand ipHand = deck.drawHand("Ac", "2d", "3h", "4s");
    HuBadugiGameState gs = new HuBadugiGameState(deck, oopHand, ipHand);

    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy = strategy.getBestResponse(gs, getDefaultIpBadugiStrategy());
    assertEquals(oopHand, strategy.getDiscardStrategy(oopHand));
  }

  @Test
  public void shouldDraw3WithOneWheelCardAgainstWheel() {
    Deck deck = Deck.createDeck();
    BadugiHand oopHand = deck.drawHand("2c", "Kd", "Kh", "Ks");
    BadugiHand ipHand = deck.drawHand("Ac", "2d", "3h", "4s");
    HuBadugiGameState gs = new HuBadugiGameState(deck, oopHand, ipHand);

    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy = strategy.getBestResponse(gs, getDefaultIpBadugiStrategy());
    assertEquals(new CardSet("Kd", "Kh", "Ks"), strategy.getDiscardStrategy(oopHand));
  }

  @Test
  public void shouldDraw2WithTwoWheelCardsAgainstWheel() {
    Deck deck = Deck.createDeck();
    BadugiHand oopHand = deck.drawHand("2c", "4d", "Kh", "Ks");
    BadugiHand ipHand = deck.drawHand("Ac", "2d", "3h", "4s");
    HuBadugiGameState gs = new HuBadugiGameState(deck, oopHand, ipHand);

    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy = strategy.getBestResponse(gs, getDefaultIpBadugiStrategy());
    assertEquals(new CardSet("Kh", "Ks"), strategy.getDiscardStrategy(oopHand));
  }

  @Test
  public void shouldDraw1WithThreeWheelCardsAgainstWheel() {
    Deck deck = Deck.createDeck();
    BadugiHand oopHand = deck.drawHand("2c", "3d", "4h", "Ks");
    BadugiHand ipHand = deck.drawHand("Ac", "2d", "3h", "4s");
    HuBadugiGameState gs = new HuBadugiGameState(deck, oopHand, ipHand);

    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy = strategy.getBestResponse(gs, getDefaultIpBadugiStrategy());
    assertEquals(new CardSet("Ks"), strategy.getDiscardStrategy(oopHand));
  }

  @Test
  public void shouldDraw0WithWheelAgainstWheel() {
    Deck deck = Deck.createDeck();
    BadugiHand oopHand = deck.drawHand("2c", "3d", "4h", "As");
    BadugiHand ipHand = deck.drawHand("Ac", "2d", "3h", "4s");
    HuBadugiGameState gs = new HuBadugiGameState(deck, oopHand, ipHand);

    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy = strategy.getBestResponse(gs, getDefaultIpBadugiStrategy());
    assertEquals(new CardSet(new BitSet()), strategy.getDiscardStrategy(oopHand));
  }

  @Test
  public void shouldDraw2WithDead1CardDrawAgainstWheel() {
    Deck deck = Deck.createDeck();
    // OOP needs Ac to make wheel, but it's in IP's hand
    BadugiHand oopHand = deck.drawHand("2h", "3s", "4d", "Ks");
    BadugiHand ipHand = deck.drawHand("Ac", "2d", "3h", "4s");
    HuBadugiGameState gs = new HuBadugiGameState(deck, oopHand, ipHand);

    HuBadugiOopStrategy strategy = new HuBadugiOopStrategy();
    strategy = strategy.getBestResponse(gs, getDefaultIpBadugiStrategy());
    CardSet bestDiscards = strategy.getDiscardStrategy(oopHand);

    // Best discard will always include 2 cards, including the Ks.  2h/3s/4d are all equivalent
    assertEquals(2, bestDiscards.numCards());
    assertTrue(bestDiscards.hasCard(new Card("Ks")));
  }

}
