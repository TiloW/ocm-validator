package proof.data.reader;

import static org.junit.Assert.fail;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import proof.ResourceBasedTest;
import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.SegmentIndex;
import proof.exception.InvalidGraphException;
import proof.exception.InvalidPathException;

import java.util.HashSet;
import java.util.Set;

/**
 * Tests for {@link PathReader}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class PathReaderTest extends ResourceBasedTest {
  private Graph graph;
  private PathReader reader;
  private HashSet<CrossingIndex> crossings;

  public PathReaderTest() {
    super("path-reader");
  }

  /**
   * Called before each test method is executed. Resets all crossings and initializes a complete
   * graph on 10 vertices.
   */
  @Before
  public void init() {
    crossings = new HashSet<CrossingIndex>();
    graph = createCompleteGraph(10);
    reader = new PathReader(graph, crossings);
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_empty() throws InvalidPathException, JSONException {
    reader.read(new JSONArray());
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_disconnected() throws InvalidPathException, JSONException {
    reader.read(loadJson("disconnected").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_missingEdge() throws InvalidPathException, JSONException {
    reader.read(loadJson("missing-edge").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_wrongDirection() throws InvalidPathException, JSONException {
    JSONArray path = loadJson("multi-edges").getJSONArray("path");
    JSONObject section = path.getJSONObject(0);
    section.put("keepDirection", !section.getBoolean("keepDirection"));

    reader.read(path);
  }

  @Test
  public void testValidate_simple() throws InvalidPathException, JSONException {
    reader.read(loadJson("simple").getJSONArray("path"));
  }

  @Test
  public void testValidate_simpleWithVariables() throws InvalidPathException, JSONException {
    crossings.add(new CrossingIndex(new SegmentIndex(2, 3), new SegmentIndex(4, 5)));
    crossings.add(new CrossingIndex(new SegmentIndex(2, 10), new SegmentIndex(3, 5)));
    crossings.add(new CrossingIndex(new SegmentIndex(7, 3), new SegmentIndex(5, 10)));

    reader.read(loadJson("simple").getJSONArray("path"));
  }

  @Test
  public void testValidate_multi() throws InvalidPathException, JSONException {
    reader.read(loadJson("multi-edges").getJSONArray("path"));
  }

  @Test
  public void testValidate_crossing() throws InvalidPathException, JSONException,
  InvalidGraphException {
    JSONArray path = loadJson("crossing").getJSONArray("path");

    assertInvalid(path);

    crossings.add(new CrossingIndex(new SegmentIndex(graph.getEdgeId(0, 3), 3), new SegmentIndex(
        graph.getEdgeId(1, 4), 5)));

    reader.read(path);
  }

  @Test
  public void testValidate_crossingExtended() throws InvalidPathException, JSONException,
      InvalidGraphException {
    Set<CrossingIndex> crossings = new HashSet<CrossingIndex>();
    JSONArray path = loadJson("crossing-2").getJSONArray("path");

    JSONObject edge = path.getJSONObject(0).getJSONObject("edge");
    int firstEdge = graph.getEdgeId(edge.getInt("source"), edge.getInt("target"));

    assertInvalid(path);

    crossings.add(new CrossingIndex(new SegmentIndex(firstEdge, path.getJSONObject(0).getInt(
        "start")), new SegmentIndex(5, 5)));

    assertInvalid(path);

    edge = path.getJSONObject(path.length() - 1).getJSONObject("edge");
    int lastEdge = graph.getEdgeId(edge.getInt("source"), edge.getInt("target"));

    crossings.clear();
    crossings.add(new CrossingIndex(new SegmentIndex(firstEdge, path.getJSONObject(0).getInt(
        "start")),
        new SegmentIndex(lastEdge, path.getJSONObject(path.length() - 1).getInt("start"))));

    Graph graph = createCompleteGraph(10);
    PathReader reader = new PathReader(graph, crossings);

    reader.read(path);
  }

  private void assertInvalid(JSONArray path) {
    try {
      reader.read(path);
      fail("Should detect missing variable");
    } catch (InvalidPathException expected) {
    }
  }
}
