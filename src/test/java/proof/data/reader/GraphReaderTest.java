package proof.data.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
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

  private JSONObject resource;

  public GraphReaderTest() {
    super("graph-reader");
  }

  private final GraphReader graphReader = new GraphReader();

  @Before
  public void init() {
    resource = loadJSON("simple");
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_empty() throws IOException {
    graphReader.read(new JSONObject());
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_ambiguous() throws IOException {
    JSONObject additionalEdge = new JSONObject();
    additionalEdge.put("id", 1);
    additionalEdge.put("source", 5);
    additionalEdge.put("target", 6);
    additionalEdge.put("cost", 1);

    resource.getJSONArray("edges").put(additionalEdge);

    graphReader.read(resource);
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_nodeOutOfRange() throws IOException {
    JSONObject additionalEdge = new JSONObject();
    additionalEdge.put("id", 1);
    additionalEdge.put("source", 42);
    additionalEdge.put("target", 43);
    additionalEdge.put("cost", 1);

    resource.getJSONArray("edges").put(additionalEdge);

    graphReader.read(resource);
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_multiEdge() throws IOException {
    JSONArray edges = resource.getJSONArray("edges");

    JSONObject edgeCopy = new JSONObject(edges.get(0));
    edgeCopy.put("id", edges.length());
    edges.put(edgeCopy);

    graphReader.read(resource);
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_multiEdgeDirected() throws IOException {
    JSONArray edges = resource.getJSONArray("edges");

    JSONObject edgeOrig = edges.getJSONObject(0);
    JSONObject edgeCopy = new JSONObject();
    edgeCopy.put("source", edgeOrig.getInt("target"));
    edgeCopy.put("target", edgeOrig.getInt("source"));
    edgeCopy.put("id", edges.length());
    edges.put(edgeCopy);

    graphReader.read(resource);
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_edgeOutOfRange() throws IOException {
    JSONArray edges = resource.getJSONArray("edges");

    JSONObject edgeCopy = new JSONObject();
    edgeCopy.put("source", 5);
    edgeCopy.put("target", 6);
    edgeCopy.put("id", edges.length() + 1);
    edges.put(edgeCopy);

    graphReader.read(resource);
  }

  @Test
  public void testRead_simple() throws IOException {
    Graph graph = graphReader.read(resource);

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
