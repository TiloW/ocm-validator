package proof.data.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.junit.Test;

import proof.ResourceBasedTest;
import proof.data.Graph;
import proof.exception.InvalidGraphException;

/**
 * Tests for {@link GraphReader}.
 * 
 * @author Tilo Wiedera
 *
 */
public class GraphReaderTest extends ResourceBasedTest {

  @Override
  protected String getResourceSubdir() {
    return "graph-reader";
  }

  private final GraphReader graphReader = new GraphReader();

  @Test(expected = InvalidGraphException.class)
  public void testRead_empty() throws IOException {
    graphReader.read(loadJSON("invalid/empty"));
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_ambiguous() throws IOException {
    graphReader.read(loadJSON("invalid/ambiguous-edge-ids"));
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_nodeOutOfRange() throws IOException {
    graphReader.read(loadJSON("invalid/node-out-of-range"));
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_multiEdge() throws IOException {
    graphReader.read(loadJSON("invalid/multi-edge"));
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_multiEdgeDirected() throws IOException {
    graphReader.read(loadJSON("invalid/multi-edge-directed"));
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_edgeOutOfRange() throws IOException {
    graphReader.read(loadJSON("invalid/edge-out-of-range"));
  }

  @Test
  public void testRead_simple() throws IOException {
    Graph graph = graphReader.read(loadJSON("valid/simple"));

    assertFalse(graph.edgeExists(0, 3));
    assertFalse(graph.edgeExists(1, 3));
    assertFalse(graph.edgeExists(2, 3));

    assertTrue(graph.edgeExists(0, 1));
    assertTrue(graph.edgeExists(0, 2));
    assertTrue(graph.edgeExists(1, 2));

    assertEquals(30, (int) graph.getEdgeCost(0, 2));
    assertEquals(30, (int) graph.getEdgeCost(0, 2));
    assertEquals(78, (int) graph.getEdgeCost(1, 2));

    try {
      graph.getEdgeCost(0, 3);
      fail("getEdgeCost for invalid edge should throw an exception");
    } catch (IllegalArgumentException expected) {
    }

    try {
      graph.addEdge(0, 0, 1, 1);
      fail("Graph should be immutable");
    } catch (UnsupportedOperationException expected) {

    }
  }
}
