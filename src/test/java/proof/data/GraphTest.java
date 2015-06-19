package proof.data;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.junit.Test;

import proof.exception.InvalidGraphException;

/**
 * Tests for {@link Graph}
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class GraphTest {

  @Test
  public void testAddEdge_single() throws InvalidGraphException {
    Graph graph = new Graph(10, 20, 5);

    graph.addEdge(0, 0, 1, 20);

    assertTrue(graph.edgeExists(0, 1));
    assertFalse(graph.edgeExists(1, 0));

    assertFalse(graph.edgeExists(0, 2));
    assertFalse(graph.edgeExists(1, 3));

    assertEquals(20, graph.getEdgeCost(0, 1));
  }

  @Test
  public void testAddEdge_many() throws InvalidGraphException {
    Graph graph = new Graph(4, 3, 0);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);
    graph.addEdge(2, 2, 0, 1000);

    try {
      graph.addEdge(3, 0, 3, 10000);
      fail("Invalid edge index was not detected");
    } catch (IllegalArgumentException expected) {
    }
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddEdge_redundant() throws InvalidGraphException {
    Graph graph = new Graph(4, 3, 0);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 0, 100);
  }

  @Test(expected = InvalidGraphException.class)
  public void testMakeImmutable_valid() throws InvalidGraphException {
    Graph graph = new Graph(4, 3, 0);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);
    graph.addEdge(2, 2, 0, 1000);

    graph.makeImmutable();

    graph.addEdge(0, 0, 1, 10);
  }

  @Test(expected = InvalidGraphException.class)
  public void testMakeImmutable_invalid() throws InvalidGraphException {
    Graph graph = new Graph(4, 3, 0);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);

    graph.makeImmutable();
  }

  @Test
  public void testEdgeExists() throws InvalidGraphException {
    Graph graph = new Graph(4, 3, 0);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);

    assertTrue(graph.edgeExists(0, 1));
    assertFalse(graph.edgeExists(1, 0));
    assertTrue(graph.edgeExists(1, 2));
    assertFalse(graph.edgeExists(2, 1));
    assertFalse(graph.edgeExists(0, 3));
    assertFalse(graph.edgeExists(1, 3));
    assertFalse(graph.edgeExists(2, 3));
    assertFalse(graph.edgeExists(3, 0));
    assertFalse(graph.edgeExists(3, 1));
    assertFalse(graph.edgeExists(3, 2));
  }

  @Test
  public void testEdgeExists_bounds() throws InvalidGraphException {
    Graph graph = new Graph(4, 3, 0);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);

    assertFalse(graph.edgeExists(4, 0));
    assertFalse(graph.edgeExists(-1, 0));
    assertFalse(graph.edgeExists(0, 42));
    assertFalse(graph.edgeExists(0, -666));
  }

  @Test
  public void testClaimedLowerBound() {
    Graph graph = new Graph(4, 3, 42);
    assertEquals(42, graph.getClaimedLowerBound());

    graph = new Graph(4, 3, 314159265);
    assertEquals(314159265, graph.getClaimedLowerBound());
  }

  @Test
  public void testGetNumberOfNodes() {
    Graph graph = new Graph(3, 4, 5);
    assertEquals(3, graph.getNumberOfNodes());

    graph = new Graph(42, 4, 5);
    assertEquals(42, graph.getNumberOfNodes());
  }

  @Test
  public void testGetNumberOfEdges() {
    Graph graph = new Graph(4, 3, 5);
    assertEquals(3, graph.getNumberOfEdges());

    graph = new Graph(4, 42, 5);
    assertEquals(42, graph.getNumberOfEdges());
  }

  @Test
  public void testGetEdgeCost() throws InvalidGraphException {
    Graph graph = new Graph(4, 3, 0);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);

    assertEquals(10, graph.getEdgeCost(0));
    assertEquals(10, graph.getEdgeCost(0, 1));
    assertEquals(100, graph.getEdgeCost(1));
    assertEquals(100, graph.getEdgeCost(1, 2));
  }

  @Test(expected = IllegalArgumentException.class)
  public void testAddEdge_null() throws InvalidGraphException {
    Graph graph = new Graph(2, 1, 0);

    graph.addEdge(0, 0, 1, 0);
  }

  @Test
  public void testGetEdgeTarget() throws InvalidGraphException {
    Graph graph = new Graph(100, 3, 0);

    graph.addEdge(0, 42, 1, 10);
    graph.addEdge(1, 1, 2, 100);

    assertEquals(42, graph.getEdgeSource(0));
    assertEquals(1, graph.getEdgeTarget(0));
    assertEquals(1, graph.getEdgeSource(1));
    assertEquals(2, graph.getEdgeTarget(1));
  }

  @Test
  public void testEdgesAreAdjacent() throws InvalidGraphException {
    Graph graph = new Graph(100, 3, 0);

    graph.addEdge(0, 42, 1, 10);
    graph.addEdge(1, 1, 2, 100);
    graph.addEdge(2, 45, 2, 100);

    assertTrue(graph.areEdgesAdjacent(0, 0));
    assertTrue(graph.areEdgesAdjacent(0, 1));
    assertFalse(graph.areEdgesAdjacent(0, 2));

    assertTrue(graph.areEdgesAdjacent(1, 0));
    assertTrue(graph.areEdgesAdjacent(1, 1));
    assertTrue(graph.areEdgesAdjacent(1, 2));

    assertFalse(graph.areEdgesAdjacent(2, 0));
    assertTrue(graph.areEdgesAdjacent(2, 1));
    assertTrue(graph.areEdgesAdjacent(2, 2));
  }
}
