package sfh.deucetoseven;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;
import sfh.cards.Card;
import sfh.cards.Card.Rank;

import static junit.framework.TestCase.assertEquals;
import static junit.framework.TestCase.assertFalse;
import static junit.framework.TestCase.assertNull;
import static junit.framework.TestCase.fail;
import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

@RunWith(JUnit4.class)
public class DeuceToSevenHandTest {

  @Test
  public void handRequires5Cards() {
    try {
      DeuceToSevenHand fourCardHand = new DeuceToSevenHand(new Card("2c"), new Card("3c"), new Card("4c"), new Card("5c"));
      fail("Should have thrown for 4 card hand");
    } catch (IllegalArgumentException e) {
      assertEquals(DeuceToSevenHand.ERROR_WRONG_NUM_CARDS, e.getMessage());
    }

    DeuceToSevenHand fiveCardHand = new DeuceToSevenHand(new Card("2c"), new Card("3c"), new Card("4c"), new Card("5c"), new Card("7d"));

    try {
      DeuceToSevenHand sixCardHand2 = new DeuceToSevenHand(new Card("2c"), new Card("3c"), new Card("4c"), new Card("5c"), new Card("7d"), new Card("8s"));
      fail("Should have thrown for 6 card hand");
    } catch (IllegalArgumentException e) {
      assertEquals(DeuceToSevenHand.ERROR_WRONG_NUM_CARDS, e.getMessage());
    }
  }

  @Test
  public void wheelsAreSameRank() {
    DeuceToSevenHand wheel1 = new DeuceToSevenHand("2c3c4c5c7d");
    DeuceToSevenHand wheel2 = new DeuceToSevenHand("2d3h4s5h7s");
    assertEquals(0, wheel1.compareTo(wheel2));
    assertEquals(0, wheel2.compareTo(wheel1));
  }

  @Test
  public void number1BeatsNumber2() {
    DeuceToSevenHand number1 = new DeuceToSevenHand("2d3h4s5c7d");
    DeuceToSevenHand number2 = new DeuceToSevenHand("2h3s4c5d8h");

    assertIsBetterThan(number1, number2);
  }

  @Test
  public void straightFlushVsStraightFlush() {
    DeuceToSevenHand AHigh = new DeuceToSevenHand("AsKsQsJsTs");
    DeuceToSevenHand KHigh = new DeuceToSevenHand("KcQcJcTc9c");
    DeuceToSevenHand KHigh2 = new DeuceToSevenHand("KdQdJdTd9d");
    DeuceToSevenHand QHigh = new DeuceToSevenHand("QhJhTh9h8h");

    assertEquals(Rank.ACE, AHigh.getStraightFlush());
    assertEquals(Rank.KING, KHigh.getStraightFlush());
    assertEquals(Rank.KING, KHigh2.getStraightFlush());
    assertEquals(Rank.QUEEN, QHigh.getStraightFlush());

    assertIsBetterThan(KHigh, AHigh);
    assertIsBetterThan(QHigh, KHigh);
    assertHandEquals(KHigh, KHigh2);
  }

  @Test
  public void quadsVsStraightFlush() {
    DeuceToSevenHand quads = new DeuceToSevenHand("2c2d2h2s7c");
    DeuceToSevenHand straightFlush = new DeuceToSevenHand("3d4d5d6d7d");

    assertEquals(Rank.TWO, quads.getQuads());
    assertEquals(Rank.SEVEN, straightFlush.getStraightFlush());

    assertIsBetterThan(quads, straightFlush);
  }

  @Test
  public void quadsVsQuads() {
    DeuceToSevenHand quadK = new DeuceToSevenHand("KcKdKhKs2c");
    DeuceToSevenHand quadQ = new DeuceToSevenHand("QcQdQhQs3d");

    assertEquals(Rank.KING, quadK.getQuads());
    assertEquals(Rank.QUEEN, quadQ.getQuads());

    assertIsBetterThan(quadQ, quadK);
  }

  @Test
  public void fullHouseVsQuads() {
    DeuceToSevenHand quads = new DeuceToSevenHand("2c2d2h2s3c");
    DeuceToSevenHand fullHouse = new DeuceToSevenHand("KcKdKhQcQs");

    assertEquals(Rank.TWO, quads.getQuads());
    assertEquals(Rank.KING, fullHouse.getFullHouse());

    assertIsBetterThan(fullHouse, quads);
  }

  @Test
  public void fullHouseVsfullHouse() {
    DeuceToSevenHand acesFull = new DeuceToSevenHand("AcAdAhQsQc");
    DeuceToSevenHand kingsFull = new DeuceToSevenHand("KcKdKs2s2h");
    DeuceToSevenHand treysFull = new DeuceToSevenHand("3c3h3dQdQh");

    assertEquals(Rank.ACE, acesFull.getFullHouse());
    assertEquals(Rank.KING, kingsFull.getFullHouse());
    assertEquals(Rank.THREE, treysFull.getFullHouse());

    assertIsBetterThan(treysFull, kingsFull);
    assertIsBetterThan(kingsFull, acesFull);
    assertIsBetterThan(treysFull, acesFull);
  }

  @Test
  public void flushVsFullHouse() {
    DeuceToSevenHand fullHouse = new DeuceToSevenHand("2c2d2h3c3h");
    DeuceToSevenHand flush = new DeuceToSevenHand("2s3s4s5s7s");

    assertEquals(Rank.TWO, fullHouse.getFullHouse());
    assertTrue(flush.isFlush());
    assertNull(flush.getStraightFlush());

    assertIsBetterThan(flush, fullHouse);
  }

  @Test
  public void flushVsFlush() {
    DeuceToSevenHand number1Flush = new DeuceToSevenHand("2c3c4c5c7c");
    DeuceToSevenHand number1Flush2 = new DeuceToSevenHand("7h5h4h3h2h");
    DeuceToSevenHand number2Flush = new DeuceToSevenHand("2d3d4d5d8d");

    assertTrue(number1Flush.isFlush());
    assertTrue(number1Flush2.isFlush());
    assertTrue(number2Flush.isFlush());

    assertIsBetterThan(number1Flush, number2Flush);
    assertIsBetterThan(number1Flush2, number2Flush);
    assertHandEquals(number1Flush, number1Flush2);
  }

  @Test
  public void straightFlushVsFlush() {
    DeuceToSevenHand straightFlush = new DeuceToSevenHand("2c3c4c5c6c");
    DeuceToSevenHand flush = new DeuceToSevenHand("Ks7s9sTs3s");

    assertEquals(Rank.SIX, straightFlush.getStraightFlush());
    assertTrue(flush.isFlush());

    assertIsBetterThan(flush, straightFlush);
  }

  @Test
  public void flushesAreBad() {
    DeuceToSevenHand number1 = new DeuceToSevenHand("2d3d4s5c7d");
    DeuceToSevenHand flush = new DeuceToSevenHand("2h3h4h5h7h");

    assertIsBetterThan(number1, flush);
  }

  @Test
  public void straightVsFlush() {
    DeuceToSevenHand straight = new DeuceToSevenHand("3c4c5c6c7h");
    DeuceToSevenHand flush = new DeuceToSevenHand("2d3d4d5d7d");

    assertEquals(Rank.SEVEN, straight.getStraight());
    assertTrue(flush.isFlush());

    assertIsBetterThan(straight, flush);
  }

  @Test
  public void straightVsStraight() {
    DeuceToSevenHand sevenHigh = new DeuceToSevenHand("3c4c5c6c7d");
    DeuceToSevenHand sevenHigh2 = new DeuceToSevenHand("3d4d5d6d7h");
    DeuceToSevenHand kingHigh = new DeuceToSevenHand("9hThJhQhKs");
    DeuceToSevenHand aceHigh = new DeuceToSevenHand("TsJsQsKcAc");

    assertEquals(Rank.SEVEN, sevenHigh.getStraight());
    assertEquals(Rank.SEVEN, sevenHigh2.getStraight());
    assertEquals(Rank.KING, kingHigh.getStraight());
    assertEquals(Rank.ACE, aceHigh.getStraight());

    assertHandEquals(sevenHigh, sevenHigh2);
    assertIsBetterThan(sevenHigh, kingHigh);
    assertIsBetterThan(sevenHigh, aceHigh);
    assertIsBetterThan(kingHigh, aceHigh);
  }

  @Test
  public void A2345NotStraight() {
    DeuceToSevenHand aTo5 = new DeuceToSevenHand("Ac2d3d4d5d");
    assertFalse(aTo5.isStraight());
  }

  @Test
  public void AKQJTIsStraight() {
    DeuceToSevenHand broadway = new DeuceToSevenHand("AsKdQhJcTc");
    assertTrue(broadway.isStraight());
  }

  @Test
  public void tripsVsStraight() {
    DeuceToSevenHand straight = new DeuceToSevenHand("2c3d4d5d6d");
    DeuceToSevenHand trips = new DeuceToSevenHand("KcKhKs7d8d");

    assertTrue(straight.isStraight());
    assertNotNull(trips.getTrips());

    assertIsBetterThan(trips, straight);
  }

  @Test
  public void tripsVsTrips() {
    DeuceToSevenHand tripA = new DeuceToSevenHand("AcAdAhKsQs");
    DeuceToSevenHand tripK = new DeuceToSevenHand("KcKdKh4sQd");
    DeuceToSevenHand trip3 = new DeuceToSevenHand("3d3h3sAsQh");
    DeuceToSevenHand trip2 = new DeuceToSevenHand("2c2s2h3cJc");

    assertNotNull(tripA.getTrips());
    assertNotNull(tripK.getTrips());
    assertNotNull(trip3.getTrips());
    assertNotNull(trip2.getTrips());

    assertIsBetterThan(tripK, tripA);
    assertIsBetterThan(trip3, tripK);
    assertIsBetterThan(trip2, trip3);

    assertIsBetterThan(trip3, tripA);
    assertIsBetterThan(trip2, tripA);

    assertIsBetterThan(trip2, tripK);
  }

  @Test
  public void twoPairVsTrips() {
    DeuceToSevenHand trip2 = new DeuceToSevenHand("2c2s2h3cJc");
    DeuceToSevenHand twoPair = new DeuceToSevenHand("AdAhKsKc2d");

    assertNotNull(trip2.getTrips());
    assertNotNull(twoPair.getTwoPair());

    assertIsBetterThan(twoPair, trip2);
  }

  @Test
  public void twoPairVsSameTwoPair() {
    DeuceToSevenHand aceKicker = new DeuceToSevenHand("5h5s6c6dAs");
    DeuceToSevenHand aceKicker2 = new DeuceToSevenHand("5c5d6h6sAd");
    DeuceToSevenHand lowKicker = new DeuceToSevenHand("5h5s6c6d7d");

    assertNotNull(aceKicker.getTwoPair());
    assertNotNull(aceKicker2.getTwoPair());
    assertNotNull(lowKicker.getTwoPair());

    assertHandEquals(aceKicker, aceKicker2);
    assertIsBetterThan(lowKicker, aceKicker);
    assertIsBetterThan(lowKicker, aceKicker2);
  }

  @Test
  public void twoPairVsTwoPair() {
    DeuceToSevenHand acesOverTreys = new DeuceToSevenHand("AcAd3c3dKh");
    DeuceToSevenHand acesOverDeuces = new DeuceToSevenHand("AcAd2c2dKh");
    DeuceToSevenHand kingsOverTreys = new DeuceToSevenHand("KcKd3c3dAh");
    DeuceToSevenHand sevensOverSixes = new DeuceToSevenHand("7c7d6d6h5s");

    assertNotNull(acesOverTreys.getTwoPair());
    assertNotNull(acesOverDeuces.getTwoPair());
    assertNotNull(kingsOverTreys.getTwoPair());
    assertNotNull(sevensOverSixes.getTwoPair());

    assertIsBetterThan(acesOverDeuces, acesOverTreys);
    assertIsBetterThan(kingsOverTreys, acesOverDeuces);
    assertIsBetterThan(sevensOverSixes, kingsOverTreys);

    assertIsBetterThan(sevensOverSixes, acesOverDeuces);
    assertIsBetterThan(sevensOverSixes, acesOverTreys);

    assertIsBetterThan(kingsOverTreys, acesOverTreys);
  }

  // TODO: One pair and high card tests

  private void assertIsBetterThan(DeuceToSevenHand first, DeuceToSevenHand second) {
    assertTrue(first.compareTo(second) < 0);
    assertTrue(second.compareTo(first) > 0);
    assertNotEquals(first, second);
  }

  // Assert that two hands have the same 2-7 rank
  private void assertHandEquals(DeuceToSevenHand first, DeuceToSevenHand second) {
    assertEquals(0, first.compareTo(second));
    assertEquals(0, second.compareTo(first));
  }

  @Ignore
  public void generateFile() throws Exception {
    DeuceToSevenHand.generateHandRankFile();
  }
}