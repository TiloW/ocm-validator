package proof.validator;

import static org.junit.Assert.fail;

import java.util.HashSet;
import java.util.Set;

import org.json.JSONArray;
import org.json.JSONException;
import org.junit.Test;

import proof.ResourceBasedTest;
import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.SegmentIndex;
import proof.exception.InvalidPathException;

public class PathValidatorTest extends ResourceBasedTest {

  public PathValidatorTest() {
    super("path");
  }

  /**
   * Returns a simple graph to be used for testing.
   */
  private Graph getSimpleGraph() {
    Graph result = new Graph(9, 14);

    // create K5 subgraph
    int counter = 0;
    for (int i = 0; i < 5; i++) {
      for (int ii = i + 1; ii < 5; ii++) {
        result.addEdge(counter++, i, ii, 42 * i);
      }
    }

    // connect remaining nodes
    for (int i = 5; i < 9; i++) {
      result.addEdge(counter++, i, i - 5, 123);
    }

    result.makeImmutable();

    return result;
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_empty() throws InvalidPathException, JSONException {
    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "invalid/empty").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_loop() throws InvalidPathException, JSONException {
    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "invalid/loop").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_missingSource() throws InvalidPathException, JSONException {
    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "invalid/missing-source").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_missingTarget() throws InvalidPathException, JSONException {
    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "invalid/missing-target").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_disconnected() throws InvalidPathException, JSONException {
    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "invalid/disconnected").getJSONArray("path"));
  }

  @Test(expected = InvalidPathException.class)
  public void testValidate_missingEdge() throws InvalidPathException, JSONException {
    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "invalid/missing-edge").getJSONArray("path"));
  }

  @Test
  public void testValidate_simple() throws InvalidPathException, JSONException {
    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "valid/simple").getJSONArray("path"));
  }

  @Test
  public void testValidate_simpleWithVariables() throws InvalidPathException, JSONException {
    Set<CrossingIndex> crossings = new HashSet<CrossingIndex>();

    crossings.add(new CrossingIndex(new SegmentIndex(2, 3), new SegmentIndex(4, 5)));
    crossings.add(new CrossingIndex(new SegmentIndex(2, 10), new SegmentIndex(3, 5)));
    crossings.add(new CrossingIndex(new SegmentIndex(7, 3), new SegmentIndex(5, 10)));

    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "valid/simple").getJSONArray("path"));
  }

  @Test
  public void testValidate_multi() throws InvalidPathException, JSONException {
    new PathValidator(new HashSet<CrossingIndex>(), 42, getSimpleGraph()).validate(loadJSON(
        "valid/multi").getJSONArray("path"));
  }

  @Test
  public void testValidate_crossing() throws InvalidPathException, JSONException {
    Set<CrossingIndex> crossings = new HashSet<CrossingIndex>();

    JSONArray path = loadJSON("valid/crossing").getJSONArray("path");
    PathValidator validator = new PathValidator(crossings, 42, getSimpleGraph());

    try {
      validator.validate(path);
      fail("Should detect missing variable");
    } catch (InvalidPathException expected) {
    }

    crossings.add(new CrossingIndex(new SegmentIndex(2, 3), new SegmentIndex(6, 5)));

    validator.validate(path);
  }
}
