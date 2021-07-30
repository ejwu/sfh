package sfh.badugi;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

public class BadugiRangeTest {
  @Test
  public void rejectInvalidConstructionStrings() {
    try {
      new BadugiRange("");
      fail("Should have failed with empty string");
    } catch (IllegalArgumentException expected) {
      assertEquals("[] doesn't match regex", expected.getMessage());
    }
    try {
      new BadugiRange("23456+");
      fail("Should have failed with more than 5 characters");
    } catch (IllegalArgumentException expected) {
      assertEquals("[23456+] doesn't match regex", expected.getMessage());
    }
    try {
      new BadugiRange("AKQJT");
      fail("Should have failed with 5 characters and no '+'");
    } catch (IllegalArgumentException expected) {
      assertEquals("[AKQJT] doesn't match regex", expected.getMessage());
    }
    try {
      new BadugiRange("+KQJT");
      fail("Should have failed with '+' not at end of string");
    } catch (IllegalArgumentException expected) {
      assertEquals("[+KQJT] doesn't match regex", expected.getMessage());
    }
    try {
      new BadugiRange("A23d");
      fail("Should have failed with invalid character");
    } catch (IllegalArgumentException expected) {
      assertEquals("[A23d] doesn't match regex", expected.getMessage());
    }
    try {
      new BadugiRange("A223");
      fail("Should have failed with repeated characters");
    } catch (IllegalArgumentException expected) {
      assertEquals("[A223] can't have repeated characters", expected.getMessage());
    }
  }

  @Test
  public void createSingleFourCardHand() {
    // TODO: It's pretty lame to create hand and range with two different strings here
    BadugiHand hand = new BadugiHand("Qc7d5h2s");
    BadugiRange range = new BadugiRange("Q752");
    assertEquals(24, range.size());
    for (BadugiHand handFromRange : range) {
      assertEquals(0, handFromRange.compareTo(hand));
    }
  }

  @Test
  public void wheelPlus() {
    BadugiRange range = new BadugiRange("A234+");
    assertEquals(24, range.size());
  }

  @Test
  public void number2Plus() {
    BadugiRange range = new BadugiRange("A235+");
    assertEquals(48, range.size());
  }

  @Test
  public void allHandsIsRightSize() {
    BadugiRange allHands = new BadugiRange("K+,KQ+,KQJ+,KQJT+");
    // 52 choose 4
    assertEquals(270725, allHands.size());
  }

  @Test
  public void allBadugisIsRightSize() {
    BadugiRange allBadugis = new BadugiRange("KQJT+");
    // (13 choose 4) * 24
    assertEquals(17160, allBadugis.size());
    // TODO: Worst 4 card hand, 3 card...
  }

  @Test
  public void all3CardsIsRightSize() {
    BadugiRange all3Cards = new BadugiRange("KQJ+");
    assertEquals(154440, all3Cards.size());
  }

  @Test
  public void all2CardsIsRightSize() {
    BadugiRange all2Cards = new BadugiRange("KQ+");
    assertEquals(96252, all2Cards.size());
  }

  @Test
  public void all1CardsIsRightSize() {
    BadugiRange all1Cards = new BadugiRange("K+");
    assertEquals(2873, all1Cards.size());
  }

  @Test
  public void fourCardRangeCreationStringIsOrderIndependent() {
    assertAllRangesFromPermutationsEqual(Lists.newArrayList('3', '9', 'T', 'K'), false);
  }

  @Test
  public void fourCardRangePlusCreationStringIsOrderIndependent() {
    assertAllRangesFromPermutationsEqual(Lists.newArrayList('A', '3', '4', '6'), true);
  }

  @Test
  public void single3CardHand() {
    // Two ways of making 678
    BadugiHand hand1 = new BadugiHand("6c7d8h9h");
    BadugiHand hand2 = new BadugiHand("6s7h8c8d");
    BadugiRange range = new BadugiRange("678");

    // This looks like the right number based on rankedHands.csv
    assertEquals(540, range.size());
    for (BadugiHand handInRange : range) {
      assertTrue(handInRange.compareTo(hand1) == 0);
      assertTrue(handInRange.compareTo(hand2) == 0);
    }
  }

  @Test
  public void a23() {
    BadugiHand hand1 = new BadugiHand("Ad2s3c4d");
    BadugiHand hand2 = new BadugiHand("Ah2c3d3s");
    BadugiRange nut3Card = new BadugiRange("A23");

    BadugiRange nut3CardPlus = new BadugiRange("A23+");

    assertEquals(900, nut3Card.size());
    assertEquals(Sets.newHashSet(nut3Card), Sets.newHashSet(nut3CardPlus));
    for (BadugiHand handInRange : nut3Card) {
      assertTrue(handInRange.compareTo(hand1) == 0);
      assertTrue(handInRange.compareTo(hand2) == 0);
    }
    for (BadugiHand handInRange : nut3CardPlus) {
      assertTrue(handInRange.compareTo(hand1) == 0);
      assertTrue(handInRange.compareTo(hand2) == 0);
    }
  }

  @Test
  public void a24() {
    BadugiHand hand1 = new BadugiHand("Ad2s4cKd");
    BadugiHand hand2 = new BadugiHand("Ah2c4d4s");
    BadugiRange a24 = new BadugiRange("A24");
    BadugiRange a24Plus = new BadugiRange("A24+");

    assertEquals(876, a24.size());
    assertEquals(1776, a24Plus.size());

    for (BadugiHand handInRange : a24) {
      assertTrue(handInRange.compareTo(hand1) == 0);
      assertTrue(handInRange.compareTo(hand2) == 0);
    }
    for (BadugiHand handInRange : a24Plus) {
      assertTrue(handInRange.compareTo(hand1) == 0 ^ handInRange.isBetterThan(hand1));
      assertTrue(handInRange.compareTo(hand2) == 0 ^ handInRange.isBetterThan(hand2));
    }
  }

  private void assertAllRangesFromPermutationsEqual(List<Character> rangeList, boolean usePlus) {
    BadugiRange previous = null;
    for (List<Character> list : Collections2.orderedPermutations(rangeList)) {
      StringBuilder rangeSb = new StringBuilder();
      for (Character c : list) {
        rangeSb.append(c);
      }
      if (usePlus) {
        rangeSb.append('+');
      }

      BadugiRange range = new BadugiRange(rangeSb.toString());
      if (previous != null) {
        assertEquals(Sets.newHashSet(previous), Sets.newHashSet(range));
      }

      previous = range;
    }
  }

}
