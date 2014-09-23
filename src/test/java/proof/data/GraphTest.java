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
 * @author Tilo Wiedera
 *
 */
public class GraphTest {

  @Test
  public void testAddEdge_single() {
    Graph graph = new Graph(10, 20);

    graph.addEdge(0, 0, 1, 20.1);

    assertTrue(graph.edgeExists(0, 1));
    assertFalse(graph.edgeExists(1, 0));

    assertFalse(graph.edgeExists(0, 2));
    assertFalse(graph.edgeExists(1, 3));

    assertEquals(20, (int) graph.getEdgeCost(0, 1));
  }

  @Test
  public void testAddEdge_many() {
    Graph graph = new Graph(4, 3);

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
  public void testAddEdge_redundant() {
    Graph graph = new Graph(4, 3);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 0, 100);
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testMakeImmutable_valid() {
    Graph graph = new Graph(4, 3);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);
    graph.addEdge(2, 2, 0, 1000);

    graph.makeImmutable();

    graph.addEdge(0, 0, 1, 10);
  }

  @Test(expected = InvalidGraphException.class)
  public void testMakeImmutable_invalid() {
    Graph graph = new Graph(4, 3);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);

    graph.makeImmutable();
  }

  @Test
  public void testEdgeExists() {
    Graph graph = new Graph(4, 3);

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
  public void testEdgeExists_bounds() {
    Graph graph = new Graph(4, 3);

    graph.addEdge(0, 0, 1, 10);
    graph.addEdge(1, 1, 2, 100);

    assertFalse(graph.edgeExists(4, 0));
    assertFalse(graph.edgeExists(-1, 0));
    assertFalse(graph.edgeExists(0, 42));
    assertFalse(graph.edgeExists(0, -666));
  }
}
