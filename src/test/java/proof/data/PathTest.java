package proof.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import proof.GraphBasedTest;
import proof.exception.InvalidGraphException;
import proof.exception.InvalidPathException;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests for {@link Path}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
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
  public void testGetSource() throws InvalidPathException {
    path.addSection(1, 2, -1, 42, true);

    assertEquals(1, path.getSource());
    assertEquals(2, path.getTarget());
  }

  @Test
  public void testGetSource_direction() throws InvalidPathException {
    path.addSection(1, 2, -1, 42, false);

    assertEquals(1, path.getTarget());
    assertEquals(2, path.getSource());
  }

  @Test
  public void testAddSection() throws InvalidPathException {
    path.addSection(1, 2, -1, 42, false);
    path.addSection(1, 5, -1, 42, true);
    path.addSection(4, 5, -1, 42, false);

    assertEquals(2, path.getSource());
    assertEquals(4, path.getTarget());
  }

  @Test
  public void testAddSection_crossing() throws InvalidPathException, InvalidGraphException {
    Set<CrossingIndex> crossings = new HashSet<>();
    crossings.add(new CrossingIndex(graph.getEdgeId(0, 1), 5, graph.getEdgeId(2, 3), 7));
    path = new Path(graph, crossings);

    path.addSection(0, 1, -1, 5, true);
    path.addSection(2, 3, 7, 42, true);

    assertEquals(0, path.getSource());
    assertEquals(3, path.getTarget());
  }

  @Test(expected = InvalidPathException.class)
  public void testAddSection_invalidCrossing() throws InvalidPathException {
    path.addSection(1, 2, -1, 5, true);
  }

  @Test(expected = InvalidPathException.class)
  public void testAddSection_disconnected() throws InvalidPathException {
    path.addSection(1, 2, -1, 5, true);
    path.addSection(3, 1, -1, 5, true);
  }

  @Test
  public void testIsDisjointTo() throws InvalidPathException {
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

  @Test(expected = IllegalArgumentException.class)
  public void testIsDisjoint_differentGraphs() throws InvalidPathException, InvalidGraphException {
    Path path2 = new Path(createCompleteGraph(21), new HashSet<CrossingIndex>());

    path.isDisjointTo(path2);
  }

  @Test(expected = IllegalArgumentException.class)
  public void testIsDisjoint_differentCrossings() throws InvalidPathException,
      InvalidGraphException {
    Set<CrossingIndex> crossings = new HashSet<>();
    crossings.add(new CrossingIndex(1, 0, 2, 0));
    Path path2 = new Path(graph, crossings);

    path.isDisjointTo(path2);
  }

  @Test
  public void testIsDisjoint_overlappingSegments() throws InvalidPathException,
      InvalidGraphException {
    Set<CrossingIndex> crossings = new HashSet<>();
    crossings.add(new CrossingIndex(graph.getEdgeId(1, 10), 5, graph.getEdgeId(3, 4), 0));
    crossings.add(new CrossingIndex(graph.getEdgeId(1, 10), 10, graph.getEdgeId(6, 7), 0));

    path = new Path(graph, crossings);
    path.addSection(1, 10, -1, 42, true);
    path.addSection(2, 10, -1, 42, false);

    Path path2 = new Path(graph, crossings);
    path2.addSection(3, 4, -1, 0, true);
    path2.addSection(1, 10, 5, 10, true);
    path2.addSection(6, 7, -1, 0, false);

    assertFalse(path.isDisjointTo(path2));
  }

  @Test
  public void testIsDisjoint_crossing() throws InvalidPathException, InvalidGraphException {
    Set<CrossingIndex> crossings = new HashSet<>();
    crossings.add(new CrossingIndex(graph.getEdgeId(1, 10), 8, graph.getEdgeId(2, 3), 16));

    path = new Path(graph, crossings);
    path.addSection(1, 10, -1, 42, true);

    Path path2 = new Path(graph, crossings);
    path2.addSection(2, 3, -1, 42, true);

    assertFalse(path.isDisjointTo(path2));
  }

  @Test
  public void testIsAdjacentTo() throws InvalidPathException {
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
