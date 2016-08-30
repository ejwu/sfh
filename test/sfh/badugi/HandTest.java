package sfh.badugi;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

@RunWith(JUnit4.class)
public class HandTest {
  @Test
  public void constructHand() {
    Hand hand = new Hand(new Card("Ac"), new Card("2d"), new Card("3h"), new Card("4s"));
    assertEquals("Ac2d3h4s", hand.toString());
  }

  @Test
  public void handWithDuplicateCardsShouldThrow() {
    try {
      new Hand(new Card("Ac"), new Card("2d"), new Card("3h"), new Card("Ac"));
      fail("Shouldn't be able to construct hand with duplicate cards");
    } catch (IllegalArgumentException expected) {
    }
  }
}
