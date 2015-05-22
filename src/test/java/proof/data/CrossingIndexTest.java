package proof.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;


/**
 * Tests for {@link CrossingIndex}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class CrossingIndexTest {

  @Test
  public void testConstructorxvalid() {
    new CrossingIndex(1, 2, 2, 1);
    new CrossingIndex(1, 2, 3, 2);
    new CrossingIndex(0, 2, 1, 2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testConstructorxinvalidSelfCrossing() {
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
  public void testFirstSegmentIsConflicting() {
    CrossingIndex c10x21 = new CrossingIndex(1, 0, 2, 1);
    CrossingIndex c10x32 = new CrossingIndex(1, 0, 3, 2);

    assertFalse(c10x21.conflicting(c10x32));
    assertFalse(c10x32.conflicting(c10x21));
  }

  @Test
  public void testIsConflicting() {
    CrossingIndex c12x21 = new CrossingIndex(1, 2, 2, 1);
    assertFalse(c12x21.conflicting(c12x21));

    CrossingIndex c12x32 = new CrossingIndex(1, 2, 3, 2);
    assertTrue(c12x21.conflicting(c12x32));

    CrossingIndex c11x31 = new CrossingIndex(1, 1, 3, 1);
    assertFalse(c12x21.conflicting(c11x31));

    CrossingIndex c11x32 = new CrossingIndex(1, 1, 3, 2);
    assertFalse(c12x21.conflicting(c11x32));

    assertTrue(c12x32.conflicting(c12x21));
    assertFalse(c12x32.conflicting(c12x32));
    assertFalse(c12x32.conflicting(c11x31));
    assertTrue(c12x32.conflicting(c11x32));

    assertFalse(c11x31.conflicting(c12x21));
    assertFalse(c11x31.conflicting(c12x32));
    assertFalse(c11x31.conflicting(c11x31));
    assertTrue(c11x31.conflicting(c11x32));

    assertFalse(c11x32.conflicting(c12x21));
    assertTrue(c11x32.conflicting(c12x32));
    assertTrue(c11x32.conflicting(c11x31));
    assertFalse(c11x32.conflicting(c11x32));
  }
}
