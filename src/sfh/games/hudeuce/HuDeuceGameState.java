package sfh.games.hudeuce;

import com.google.common.base.Stopwatch;
import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;
import sfh.cards.CardSet;
import sfh.deucetoseven.DeuceToSevenHand;
import sfh.games.common.hudraw.GameStateAndStrategy;
import sfh.games.common.hudraw.HuDrawGameState;

import java.util.Map;
import java.util.concurrent.TimeUnit;

public class HuDeuceGameState
    implements HuDrawGameState<HuDeuceOopStrategy, HuDeuceIpStrategy> {

  private static final LoadingCache<GameStateAndStrategy, Double> CACHE = CacheBuilder.newBuilder()
      .initialCapacity(1000000)
      .recordStats()
      .build(new CacheLoader<GameStateAndStrategy, Double>() {
        @Override
        public Double load(GameStateAndStrategy gss) throws Exception {
          return gss.calculateValue();
        }
      });

  private CardSet deck;
  private DeuceToSevenHand oopHand;
  private DeuceToSevenHand ipHand;

  public HuDeuceGameState(CardSet deck, DeuceToSevenHand oopHand, DeuceToSevenHand ipHand) {
    this.deck = deck;
    this.oopHand = oopHand;
    this.ipHand = ipHand;
  }

  DeuceToSevenHand getOopHand() {
    return oopHand;
  }

  DeuceToSevenHand getIpHand() {
    return ipHand;
  }

  @Override
  public double getValue(HuDeuceOopStrategy oop, HuDeuceIpStrategy ip) {
    try {
      System.out.println(CACHE.stats());
      GameStateAndStrategy gss = new GameStateAndStrategy(this, oop, ip);
      return CACHE.get(gss);
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }

  @Override
  synchronized public double calculateValue(HuDeuceOopStrategy oop, HuDeuceIpStrategy ip) {
    // Single draw, no betting
    Map<DeuceToSevenHand, CardSet> oopHands = oop.generatePossibleHands(deck, oopHand);
    double cumulativeOopValue = 0.0;
    System.out.println(oopHands.size() + " total OOP hands");
    int totalComp = 0;
    int totalIpHands = 0;

    Stopwatch sw = Stopwatch.createStarted();
    for (Map.Entry<DeuceToSevenHand, CardSet> handWithRemainingDeck : oopHands.entrySet()) {
      // For every possible deck state after OOP draws, try every possible result of IP drawing
      Map<DeuceToSevenHand, CardSet> ipHands = ip.generatePossibleHands(handWithRemainingDeck.getValue(), ipHand);
      // Total value of a particular OOP hand against all possible IP hands
      double cumulativeValueOfOopHand = 0.0;
      totalIpHands += ipHands.size();
      for (DeuceToSevenHand potentialIpHand : ipHands.keySet()) {
        DeuceToSevenHand potentialOopHand = handWithRemainingDeck.getKey();
        totalComp++;

        System.out.println("comparing " + potentialOopHand + " to " + potentialIpHand);

        if (potentialOopHand.compareTo(potentialIpHand) > 0) {
          System.out.println("IP");
          // OOP loses and gets no part of the pot
        } else if (potentialOopHand.compareTo(potentialIpHand) < 0) {
          // OOP wins and gets the whole pot
          System.out.println("OOP");
          cumulativeValueOfOopHand += 1;
        } else {
          System.out.println("tie");
          // Ties get half the pot
          cumulativeValueOfOopHand += 0.5;
        }
        System.out.println("cumvalue: " + cumulativeValueOfOopHand);
      }

      // Add the average expected value of this OOP hand to the sum
      cumulativeOopValue += cumulativeValueOfOopHand / ipHands.keySet().size();

      System.out.println(cumulativeValueOfOopHand);
      System.out.println(ipHands.keySet().size());
      System.out.println(cumulativeOopValue);
    }


    System.out.println(totalIpHands + " total IP hands");
    System.out.println(totalComp + " total comparisons done");
    long elapsed = sw.elapsed(TimeUnit.MILLISECONDS);
    System.out.println(elapsed + " ms");
    System.out.println((double) elapsed / oopHands.size() + " ms/oop hand");
    return cumulativeOopValue / oopHands.keySet().size();

  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    HuDeuceGameState that = (HuDeuceGameState) o;

    if (deck != null ? !deck.equals(that.deck) : that.deck != null) return false;
    if (oopHand != null ? !oopHand.equals(that.oopHand) : that.oopHand != null) return false;
    return ipHand != null ? ipHand.equals(that.ipHand) : that.ipHand == null;

  }

  @Override
  public int hashCode() {
    int result = deck != null ? deck.hashCode() : 0;
    result = 31 * result + (oopHand != null ? oopHand.hashCode() : 0);
    result = 31 * result + (ipHand != null ? ipHand.hashCode() : 0);
    return result;
  }
}

