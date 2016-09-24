package sfh.games.hubadugi;

import sfh.GameState;
import sfh.badugi.CardSet;
import sfh.badugi.Hand;

import java.util.Map;

public class HuBadugiGameState implements GameState<HuBadugiOopStrategy, HuBadugiIpStrategy> {

  private CardSet deck;
  private Hand oopHand;
  private Hand ipHand;

  public HuBadugiGameState(CardSet deck, Hand oopHand, Hand ipHand) {
    this.deck = deck;
    this.oopHand = oopHand;
    this.ipHand = ipHand;
  }

  // TODO: unclear if this should be exposed, but strategies need this to figure out best responses
  Hand getOopHand() {
    return oopHand;
  }

  Hand getIpHand() {
    return ipHand;
  }

  /**
   * Calculates the expected value for OOP in a 1 bet pot with no betting.
   */
  @Override
  public double getValue(HuBadugiOopStrategy oop, HuBadugiIpStrategy ip) {
    // Single draw, no betting
    Map<Hand, CardSet> oopHands = oop.generatePossibleHands(deck, oopHand);
    double cumulativeOopValue = 0.0;
    for (Map.Entry<Hand, CardSet> handWithRemainingDeck : oopHands.entrySet()) {
      // For every possible deck state after OOP draws, try every possible result of IP drawing
      Map<Hand, CardSet> ipHands = ip.generatePossibleHands(handWithRemainingDeck.getValue(), ipHand);
      // Total value of a particular OOP hand against all possible IP hands
      double cumulativeValueOfOopHand = 0.0;
      for (Hand potentialIpHand : ipHands.keySet()) {
        Hand potentialOopHand = handWithRemainingDeck.getKey();
        if (potentialOopHand.compareTo(potentialIpHand) > 0) {
          // OOP loses and gets no part of the pot
        } else if (potentialOopHand.compareTo(potentialIpHand) < 0) {
          // OOP wins and gets the whole pot
          cumulativeValueOfOopHand += 1;
        } else {
          // Ties get half the pot
          cumulativeValueOfOopHand += 0.5;
        }
      }
      // Add the average expected value of this OOP hand to the sum
      cumulativeOopValue += cumulativeValueOfOopHand / ipHands.keySet().size();
    }
    return cumulativeOopValue / oopHands.keySet().size();
  }
}
