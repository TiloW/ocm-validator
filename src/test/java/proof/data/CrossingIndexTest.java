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

  @Test
  public void testIsConflicting() {
    CrossingIndex c12_21 = new CrossingIndex(1, 2, 2, 1);
    CrossingIndex c12_32 = new CrossingIndex(1, 2, 3, 2);
    CrossingIndex c11_31 = new CrossingIndex(1, 1, 3, 1);
    CrossingIndex c11_32 = new CrossingIndex(1, 1, 3, 2);

    assertFalse(c12_21.conflicting(c12_21));
    assertTrue(c12_21.conflicting(c12_32));
    assertFalse(c12_21.conflicting(c11_31));
    assertFalse(c12_21.conflicting(c11_32));

    assertTrue(c12_32.conflicting(c12_21));
    assertFalse(c12_32.conflicting(c12_32));
    assertFalse(c12_32.conflicting(c11_31));
    assertTrue(c12_32.conflicting(c11_32));

    assertFalse(c11_31.conflicting(c12_21));
    assertFalse(c11_31.conflicting(c12_32));
    assertFalse(c11_31.conflicting(c11_31));
    assertTrue(c11_31.conflicting(c11_32));

    assertFalse(c11_32.conflicting(c12_21));
    assertTrue(c11_32.conflicting(c12_32));
    assertTrue(c11_32.conflicting(c11_31));
    assertFalse(c11_32.conflicting(c11_32));
  }
}
