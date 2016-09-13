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

  @Override
  public double getValue(HuBadugiOopStrategy oop, HuBadugiIpStrategy ip) {
    // Single draw, no betting
    Map<Hand, CardSet> oopHands = oop.generatePossibleHands(deck, oopHand);
    double cumulativeOopValue = 0.0;
    for (Map.Entry<Hand, CardSet> handWithRemainingDeck : oopHands.entrySet()) {
      Map<Hand, CardSet> ipHands = ip.generatePossibleHands(handWithRemainingDeck.getValue(), ipHand);
      double cumulativeValueOfOopHand = 0.0;
      for (Hand potentialIpHand : ipHands.keySet()) {
        Hand potentialOopHand = handWithRemainingDeck.getKey();
        if (potentialOopHand.compareTo(potentialIpHand) > 0) {
          cumulativeValueOfOopHand -= 1;
        } else if (potentialOopHand.compareTo(potentialIpHand) < 0) {
          cumulativeValueOfOopHand += 1;
        } else {
          // cumulative value doesn't change for a tie
        }
      }
      cumulativeOopValue += cumulativeValueOfOopHand / ipHands.keySet().size();
    }
    return cumulativeOopValue / oopHands.keySet().size();
  }
}
