package sfh.badugi;

import com.google.common.collect.Collections2;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import org.junit.Test;

import java.util.List;

import static junit.framework.TestCase.assertEquals;
import static org.junit.Assert.assertTrue;

public class BadugiRangeTest {
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
  public void allBadugisIsRightSize() {
    BadugiRange allBadugis = new BadugiRange("KQJT+");
    // (13 choose 4) * 24
    assertEquals(17160, allBadugis.size());
    // Worst 4 card hand, 3 card...
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
