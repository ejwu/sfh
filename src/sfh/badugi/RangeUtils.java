package sfh.badugi;

import sfh.cards.CardSet;
import sfh.cards.Deck;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class RangeUtils {

  /**
   * Output various useful ranges.
   */
  public static void rangesByHandType() {
    List<BadugiHand> hands = new ArrayList<>();
    for (BitSet bs : BadugiHand.HAND_RANK_CACHE.keySet()) {
      hands.add(new BadugiHand(bs));
    }
    System.out.println(hands.size() + " total hands");
    List<BadugiHand> patHands = new ArrayList<>();
    List<BadugiHand> threeCardHands = new ArrayList<>();
    List<BadugiHand> twoCardHands = new ArrayList<>();
    for (BadugiHand hand : hands) {
      if (hand.getPlayableHand().numCards() == 4) {
        patHands.add(hand);
      } else if (hand.getPlayableHand().numCards() == 3) {
        threeCardHands.add(hand);
      } else if (hand.getPlayableHand().numCards() == 2) {
        twoCardHands.add(hand);
      }
    }
    System.out.println(patHands.size() + " pat hands");
    outputAsVentiles(patHands);
    System.out.println(threeCardHands.size() + " 3 card hands");
    outputAsVentiles(threeCardHands);
    System.out.println(twoCardHands.size() + " 2 card hands");
    outputAsVentiles(twoCardHands);

    Deck deck = Deck.createDeck();
    BadugiHand twoCardDraw = deck.drawHand("Ac", "2d", "Kc", "Kd");
    CardSet twoCard = twoCardDraw.without(new CardSet("Kc", "Kd"));
    Collection<CardSet> draws = deck.drawNCards(2);

    List<BadugiHand> drawnHands = new ArrayList<>();
    for (CardSet draw : draws) {
      BadugiHand newHand = new BadugiHand(twoCard, draw);
      drawnHands.add(newHand);
    }

    System.out.println(drawnHands.size() + " hands drawn from A2(KK)");
    outputAsVentiles(drawnHands);

    List<BadugiHand> patHandsFromDraw2 = new ArrayList<>();
    for (BadugiHand hand : drawnHands) {
      if (hand.isBadugi()) {
        patHandsFromDraw2.add(hand);
      }
    }
    System.out.println(patHandsFromDraw2.size() + " pat hands drawn from A2(KK)");
    outputAsVentiles(patHandsFromDraw2);

    deck = Deck.createDeck();
    twoCardDraw = deck.drawHand("6c", "7d", "Kc", "Kd");
    twoCard = twoCardDraw.without(new CardSet("Kc", "Kd"));
    draws = deck.drawNCards(2);

    drawnHands.clear();
    for (CardSet draw : draws) {
      BadugiHand newHand = new BadugiHand(twoCard, draw);
      drawnHands.add(newHand);
    }

    System.out.println(drawnHands.size() + " hands drawn from 67(KK)");
    outputAsVentiles(drawnHands);

    patHandsFromDraw2 = new ArrayList<>();
    for (BadugiHand hand : drawnHands) {
      if (hand.isBadugi()) {
        patHandsFromDraw2.add(hand);
      }
    }
    System.out.println(patHandsFromDraw2.size() + " pat hands drawn from 67(KK)");
    outputAsVentiles(patHandsFromDraw2);

  }

  public static void outputAsVentiles(List<BadugiHand> hands) {
    int ventileSize = hands.size() / 20;
    int count = 0;
    Collections.sort(hands);
    Collections.reverse(hands);
    for (BadugiHand hand : hands) {
      if (count % ventileSize == 0) {
        System.out.println(String.format("%3d (%d): %s", count / ventileSize, count, hand.getPlayableRankString()));
      }
      count++;
    }
  }

  /**
   * Parse a comma separated string into a collection of hands.
   *
   * "+" indicates a better hand, i.e. QJT9+ is every Q badugi or better.
   * TODO? Maybe some sort of exclusion notation, such that A8+ might not want to include 76.
   * @param rangeString QJT9+, 765+, A5+, etc.
   */
  public Collection<BadugiHand> parseRangeString(String rangeString) {
    for (String playableHand : rangeString.split(",")) {
      String hand = playableHand.trim();
    }
    return null;
  }

}
