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
    new CrossingIndex(1, 2, 2, 1);
    new CrossingIndex(1, 2, 3, 2);
    new CrossingIndex(0, 2, 1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructor_invalidSelfCrossing() {
    new CrossingIndex(2, 2, 2, 1);
  }

  @Test
  public void testEquality() {
    CrossingIndex ci = new CrossingIndex(1, 2, 3, 2);

    assertTrue(ci.equals(ci));
    assertTrue(ci.equals(new CrossingIndex(1, 2, 3, 2)));
    assertEquals(ci.hashCode(), new CrossingIndex(1, 2, 3, 2).hashCode());
    assertFalse(ci.equals(new CrossingIndex(1, 3, 3, 2)));
    assertFalse(ci.equals(new CrossingIndex(0, 2, 3, 2)));
    assertFalse(ci.equals(null));
  }
}
