package proof.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * Tests for {@link CrossingIndex}.
 * 
 * @author Tilo Wiedera
 *
 */
public class CrossingIndexTest {

  @Test
  public void testConstructor_valid() {
    new CrossingIndex(new SegmentIndex(1, 2), new SegmentIndex(2, 1));
    new CrossingIndex(new SegmentIndex(1, 2), new SegmentIndex(3, 2));
    new CrossingIndex(new SegmentIndex(0, 2), new SegmentIndex(1, 2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_invalidSelfCrossing() {
    new CrossingIndex(new SegmentIndex(2, 2), new SegmentIndex(2, 1));
  }

  @Test
  public void testEquality() {
    CrossingIndex ci = new CrossingIndex(new SegmentIndex(1, 2), new SegmentIndex(3, 2));

    assertTrue(ci.equals(ci));
    assertTrue(ci.equals(new CrossingIndex(new SegmentIndex(1, 2), new SegmentIndex(3, 2))));
    assertEquals(ci.hashCode(),
        new CrossingIndex(new SegmentIndex(1, 2), new SegmentIndex(3, 2)).hashCode());
    assertFalse(ci.equals(new CrossingIndex(new SegmentIndex(1, 3), new SegmentIndex(3, 2))));
    assertFalse(ci.equals(new CrossingIndex(new SegmentIndex(0, 2), new SegmentIndex(3, 2))));
    assertFalse(ci.equals(null));
  }
}
