package proof.validator;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import proof.ResourceBasedTest;
import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.exception.InvalidConstraintException;

/**
 * Tests for {@link ConstraintValidator}.
 *
 * Tests are run either on a mocked {@ConstraintValidator} that assumes each
 * path to be valid or on a K100 with a fully functional {@PathValidator}.
 *
 * @author Tilo Wiedera
 * 
 */
public class ConstraintValidatorTest extends ResourceBasedTest {

  private Graph graph;
  private Map<CrossingIndex, Boolean> variables;
  private ConstraintValidator validator;

  public ConstraintValidatorTest() {
    super("constraint");
  }

  @Before
  public void init() {
    graph = createCompleteGraph(100);
    variables = new HashMap<CrossingIndex, Boolean>();
    validator = new ConstraintValidator(createCompleteGraph(100), variables);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_missingNodeK33() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k33-simple");
    JSONObject edge =
        resource.getJSONArray("paths").getJSONArray(0).getJSONObject(0).getJSONObject("edge");

    edge.put("target", 101);

    validator.validate(resource);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_missingEdgeK33() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k33-simple");
    JSONObject edge =
        resource.getJSONArray("paths").getJSONArray(0).getJSONObject(0).getJSONObject("edge");

    int tmp = edge.getInt("source");
    edge.put("source", edge.get("target"));
    edge.put("target", tmp);

    validator.validate(resource);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_tooManyPathsK5() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k5-simple");
    JSONArray paths = resource.getJSONArray("paths");
    paths.put(paths.get(0));

    validator.validate(resource);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_tooFewPathsK3() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k33-simple");
    resource.getJSONArray("paths").remove(0);

    validator.validate(resource);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidType() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k5-simple");
    resource.put("type", "some-unknown-type");

    validator.validate(resource);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_tooManyNodesK5() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k5-simple");
    JSONObject edge =
        resource.getJSONArray("paths").getJSONArray(0).getJSONObject(0).getJSONObject("edge");
    edge.put("target", 99);

    validator.validate(resource);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_missingEdgeK5() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k5-simple");
    JSONArray paths = resource.getJSONArray("paths");
    JSONArray path = paths.getJSONArray(0);
    paths.remove(1);
    paths.put(path);

    validator.validate(resource);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_tooManyNodesK33() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k33-simple");
    JSONArray paths = resource.getJSONArray("paths");
    JSONArray path = paths.getJSONArray(8);
    for (int i = 0; i < 3; i++) {
      paths.remove(0);
      paths.put(path);
    }

    validator.validate(resource);
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidColoringK33() throws InvalidConstraintException {
    JSONObject resource = loadJSON("k33-simple");
    resource.getJSONArray("paths").getJSONArray(0).getJSONObject(0).getJSONObject("edge")
        .put("target", 1);

    validator.validate(resource);
  }

  @Test
  public void testValidate_requiredCrossingK33() throws InvalidConstraintException {
    validator.validate(loadJSON("k33-required-crossing"));
  }

  @Test
  public void testValidate_simpleK5() throws InvalidConstraintException {
    validator.validate(loadJSON("k5-simple"));
  }

  @Test
  public void testValidate_crossingAsNode() throws InvalidConstraintException {
    validator.validate(loadJSON("k5-crossing-as-node"));
  }

  @Test
  public void testValidate_simpleK33() throws InvalidConstraintException {
    validator.validate(loadJSON("k33-simple"));
  }
}
