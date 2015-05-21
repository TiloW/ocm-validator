package proof.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import proof.GraphBasedTest;
import proof.exception.InvalidPathException;

/**
 * Tests for {@link Path}.
 *
 * @author Tilo Wiedera
 */
public class PathTest extends GraphBasedTest {
  private Path path;
  private Graph graph;

  @Before
  public void setUp() {
    graph = createCompleteGraph(21);
    path = new Path(graph, new HashSet<CrossingIndex>());
  }

  @Test
  public void testGetSource() {
    path.addSection(1, 2, -1, 42, true);

    assertEquals(1, path.getSource());
    assertEquals(2, path.getTarget());
  }

  @Test
  public void testGetSource_direction() {
    path.addSection(1, 2, -1, 42, false);

    assertEquals(1, path.getTarget());
    assertEquals(2, path.getSource());
  }

  @Test
  public void testAddSection() {
    path.addSection(1, 2, -1, 42, false);
    path.addSection(1, 5, -1, 42, true);
    path.addSection(4, 5, -1, 42, false);

    assertEquals(2, path.getSource());
    assertEquals(4, path.getTarget());
  }

  @Test
  public void testAddSection_crossing() {
    Set<CrossingIndex> crossings = new HashSet<>();
    crossings.add(new CrossingIndex(graph.getEdgeId(0, 1), 5, graph.getEdgeId(2, 3), 7));
    path = new Path(graph, crossings);

    path.addSection(0, 1, -1, 5, true);
    path.addSection(2, 3, 7, 42, true);

    assertEquals(0, path.getSource());
    assertEquals(3, path.getTarget());
  }

  @Test(expected = InvalidPathException.class)
  public void testAddSection_invalidCrossing() {
    path.addSection(1, 2, -1, 5, true);
  }

  @Test(expected = InvalidPathException.class)
  public void testAddSection_disconnected() {
    path.addSection(1, 2, -1, 5, true);
    path.addSection(3, 1, -1, 5, true);
  }

  @Test
  public void testIsDisjointTo() {
    path.addSection(1, 10, -1, 42, true);
    path.addSection(2, 10, -1, 42, false);

    Path path2 = new Path(graph, new HashSet<CrossingIndex>());
    path2.addSection(1, 5, -1, 42, true);

    assertTrue(path.isDisjointTo(path2));
    assertTrue(path2.isDisjointTo(path));

    path2.addSection(5, 7, -1, 42, true);

    assertTrue(path.isDisjointTo(path2));
    assertTrue(path2.isDisjointTo(path));

    path2.addSection(7, 10, -1, 42, true);

    assertFalse(path.isDisjointTo(path2));
    assertFalse(path2.isDisjointTo(path));
  }

  @Test
  public void testIsAdjacentTo() {
    path.addSection(1, 2, -1, 42, true);

    Path path2 = new Path(graph, new HashSet<CrossingIndex>());
    path2.addSection(1, 5, -1, 42, true);
    path2.addSection(5, 20, -1, 42, true);

    assertTrue(path.isAdjacentTo(path2));
    assertTrue(path2.isAdjacentTo(path));
    assertTrue(path.isAdjacentTo(path));
    assertTrue(path2.isAdjacentTo(path2));

    path2 = new Path(graph, new HashSet<CrossingIndex>());
    path2.addSection(3, 5, -1, 42, true);
    path2.addSection(5, 20, -1, 42, true);

    assertFalse(path.isAdjacentTo(path2));
    assertFalse(path2.isAdjacentTo(path));
    assertTrue(path2.isAdjacentTo(path2));
  }
}
