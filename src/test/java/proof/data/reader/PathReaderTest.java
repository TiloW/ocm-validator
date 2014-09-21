package proof.data.reader;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Test;

import proof.ResourceBasedTest;
import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.SegmentIndex;
import proof.exception.InvalidPathException;

public class PathReaderTest extends ResourceBasedTest {

  public PathReaderTest() {
    super("path-reader");
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_empty() throws InvalidPathException, JSONException {
    new PathReader(createCompleteGraph(10), new HashSet<CrossingIndex>()).read(new JSONArray());
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_disconnected() throws InvalidPathException, JSONException {
    new PathReader(createCompleteGraph(10), new HashSet<CrossingIndex>()).read(loadJSON(
        "disconnected").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_missingEdge() throws InvalidPathException, JSONException {
    new PathReader(createCompleteGraph(10), new HashSet<CrossingIndex>()).read(loadJSON(
        "missing-edge").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_wrongDirection() throws InvalidPathException, JSONException {
    JSONArray path = loadJSON("multi-edges").getJSONArray("path");
    JSONObject section = path.getJSONObject(0);
    section.put("keepDirection", !section.getBoolean("keepDirection"));
    new PathReader(createCompleteGraph(10), new HashSet<CrossingIndex>()).read(path);
  }

  @Test
  public void testValidate_simple() throws InvalidPathException, JSONException {
    new PathReader(createCompleteGraph(10), new HashSet<CrossingIndex>()).read(loadJSON("simple")
        .getJSONArray("path"));
  }

  @Test
  public void testValidate_simpleWithVariables() throws InvalidPathException, JSONException {
    Set<CrossingIndex> crossings = new HashSet<CrossingIndex>();

    crossings.add(new CrossingIndex(new SegmentIndex(2, 3), new SegmentIndex(4, 5)));
    crossings.add(new CrossingIndex(new SegmentIndex(2, 10), new SegmentIndex(3, 5)));
    crossings.add(new CrossingIndex(new SegmentIndex(7, 3), new SegmentIndex(5, 10)));

    new PathReader(createCompleteGraph(10), crossings)
        .read(loadJSON("simple").getJSONArray("path"));
  }

  @Test
  public void testValidate_multi() throws InvalidPathException, JSONException {
    new PathReader(createCompleteGraph(10), new HashSet<CrossingIndex>()).read(loadJSON(
        "multi-edges").getJSONArray("path"));
  }

  @Test
  public void testValidate_crossing() throws InvalidPathException, JSONException {
    Set<CrossingIndex> crossings = new HashSet<CrossingIndex>();

    JSONArray path = loadJSON("crossing").getJSONArray("path");
    Graph graph = createCompleteGraph(10);
    PathReader reader = new PathReader(graph, crossings);

    try {
      reader.read(path);
      fail("Should detect missing variable");
    } catch (InvalidPathException expected) {
    }

    crossings.add(new CrossingIndex(new SegmentIndex(graph.getEdgeId(0, 3), 3), new SegmentIndex(
        graph.getEdgeId(1, 4), 5)));

    reader.read(path);
  }

  @Test
  public void testValidate_crossingsAsEndpoints() throws InvalidPathException, JSONException {
    Set<CrossingIndex> crossings = new HashSet<CrossingIndex>();
    JSONArray path = loadJSON("crossings-as-endpoints").getJSONArray("path");

    Graph graph = createCompleteGraph(10);
    PathReader reader = new PathReader(graph, crossings);

    JSONObject edge = path.getJSONObject(0).getJSONObject("edge");
    int firstEdge = graph.getEdgeId(edge.getInt("source"), edge.getInt("target"));

    edge = path.getJSONObject(path.length() - 1).getJSONObject("edge");
    int lastEdge = graph.getEdgeId(edge.getInt("source"), edge.getInt("target"));

    try {
      reader.read(path);
      fail("Should detect missing variable");
    } catch (InvalidPathException expected) {
    }

    crossings.add(new CrossingIndex(new SegmentIndex(firstEdge, path.getJSONObject(0).getInt(
        "start")), new SegmentIndex(5, 5)));

    try {
      reader.read(path);
      fail("Should detect missing variable");
    } catch (InvalidPathException expected) {
    }

    crossings.clear();
    crossings.add(new CrossingIndex(new SegmentIndex(firstEdge, path.getJSONObject(0).getInt(
        "start")),
        new SegmentIndex(lastEdge, path.getJSONObject(path.length() - 1).getInt("start"))));

    reader.read(path);
  }
}
