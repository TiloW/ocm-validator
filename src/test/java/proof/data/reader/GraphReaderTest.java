package proof.data.reader;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import proof.ResourceBasedTest;
import proof.data.Graph;
import proof.exception.InvalidGraphException;

import java.io.IOException;

/**
 * Tests for {@link GraphReader}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class GraphReaderTest extends ResourceBasedTest {
  private JSONObject resource;

  public GraphReaderTest() {
    super("graph-reader");
  }

  private final GraphReader graphReader = new GraphReader();

  @Before
  public void init() {
    resource = loadJson("simple");
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_empty() throws IOException, InvalidGraphException {
    graphReader.read(new JSONObject());
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_ambiguous() throws IOException, InvalidGraphException {
    JSONObject additionalEdge = new JSONObject();
    additionalEdge.put("id", 1);
    additionalEdge.put("source", 5);
    additionalEdge.put("target", 6);
    additionalEdge.put("cost", 1);

    resource.getJSONArray("edges").put(additionalEdge);

    graphReader.read(resource);
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_nodeOutOfRange() throws IOException, InvalidGraphException {
    JSONObject additionalEdge = new JSONObject();
    additionalEdge.put("id", 1);
    additionalEdge.put("source", 42);
    additionalEdge.put("target", 43);
    additionalEdge.put("cost", 1);

    resource.getJSONArray("edges").put(additionalEdge);

    graphReader.read(resource);
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_multiEdge() throws IOException, InvalidGraphException {
    JSONArray edges = resource.getJSONArray("edges");

    JSONObject edgeCopy = new JSONObject(edges.get(0));
    edgeCopy.put("id", edges.length());
    edges.put(edgeCopy);

    graphReader.read(resource);
  }

  @Test(expected = InvalidGraphException.class)
  public void testRead_multiEdgeDirected() throws IOException, InvalidGraphException {
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
  public void testRead_edgeOutOfRange() throws IOException, InvalidGraphException {
    JSONArray edges = resource.getJSONArray("edges");

    JSONObject edgeCopy = new JSONObject();
    edgeCopy.put("source", 5);
    edgeCopy.put("target", 6);
    edgeCopy.put("id", edges.length() + 1);
    edges.put(edgeCopy);

    graphReader.read(resource);
  }

  @Test
  public void testRead_simple() throws IOException, InvalidGraphException {
    Graph graph = graphReader.read(resource);

    assertFalse(graph.edgeExists(0, 3));
    assertFalse(graph.edgeExists(1, 3));
    assertFalse(graph.edgeExists(2, 3));

    assertTrue(graph.edgeExists(0, 1));
    assertTrue(graph.edgeExists(0, 2));
    assertTrue(graph.edgeExists(1, 2));

    assertEquals(1, graph.getEdgeCost(0, 1));
    assertEquals(1, graph.getEdgeCost(0, 2));
    assertEquals(42, graph.getEdgeCost(1, 2));

    try {
      graph.getEdgeCost(0, 3);
      fail("getEdgeCost for invalid edge should throw an exception");
    } catch (InvalidGraphException expected) {
    }

    try {
      graph.addEdge(0, 0, 1, 1);
      fail("Graph should be immutable");
    } catch (InvalidGraphException expected) {

    }
  }
}
