package proof.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

/**
 * Tests for {@link SegmentIndex}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class SegmentIndexTest {

  @Test
  public void testConstructor_valid() {
    new SegmentIndex(1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_invalidEdge() {
    new SegmentIndex(-1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_invalidSegment() {
    new SegmentIndex(1, -2);
  }

  @Test
  public void testEquality() {
    SegmentIndex si = new SegmentIndex(2, 1);

    assertTrue(si.equals(si));
    assertTrue(si.equals(new SegmentIndex(2, 1)));
    assertEquals(si.hashCode(), new SegmentIndex(2, 1).hashCode());
    assertFalse(si.equals(new SegmentIndex(1, 1).hashCode()));
    assertFalse(si.equals(new SegmentIndex(2, 2).hashCode()));
    assertFalse(si.equals(null));
  }
}
