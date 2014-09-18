package proof.validator;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import proof.ResourceBasedTest;
import proof.exception.InvalidCoverageException;

public class BranchCoverageValidatorTest extends ResourceBasedTest {

  private JSONObject simpleResource;
  private JSONObject singleLeafResource;

  public BranchCoverageValidatorTest() {
    super("branch-coverage");
  }

  @Before
  public void init() {
    simpleResource = loadJSON("simple");
  }

  private final BranchCoverageValidator coverageValidator = new BranchCoverageValidator(
      createCompleteGraph(100));

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_empty() throws InvalidCoverageException, IOException {
    JSONObject empty = new JSONObject();
    empty.put("leaves", new JSONArray());
    coverageValidator.validate(empty);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_insufficientCoverage() throws InvalidCoverageException, IOException {
    simpleResource.getJSONArray("leaves").remove(2);
    coverageValidator.validate(simpleResource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidMismatchedValue() throws InvalidCoverageException, IOException {
    JSONObject var =
        simpleResource.getJSONArray("leaves").getJSONObject(0).getJSONArray("fixedVariables")
            .getJSONObject(0);

    var.put("value", var.getInt("value") == 1 ? 0 : 1);

    coverageValidator.validate(simpleResource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidMultipleVariables() throws InvalidCoverageException, IOException {
    simpleResource.getJSONArray("leaves").getJSONObject(0).getJSONArray("fixedVariables")
        .getJSONObject(0).getJSONArray("segments").getJSONObject(0).getJSONObject("edge")
        .put("source", 123);

    coverageValidator.validate(simpleResource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidSingleLeaf() throws InvalidCoverageException, IOException {
    JSONObject resource = loadJSON("single-leaf");
    JSONObject var =
        simpleResource.getJSONArray("leaves").getJSONObject(0).getJSONArray("fixedVariables")
            .getJSONObject(0);
    resource.getJSONArray("leaves").getJSONObject(0).getJSONArray("fixedVariables").put(var);

    coverageValidator.validate(resource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_overlappingCoverage() throws InvalidCoverageException, IOException {
    JSONArray leaves = simpleResource.getJSONArray("leaves");
    leaves.put(leaves.get(0));
    coverageValidator.validate(simpleResource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_unmergeable() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("unmergeable"));
  }

  @Test
  public void testValidate_validCoverage() throws InvalidCoverageException, IOException {
    coverageValidator.validate(simpleResource);
  }

  @Test
  public void testValidate_validSingleLeaf() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("single-leaf"));
  }

}
