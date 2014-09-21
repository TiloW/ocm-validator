package proof.validator;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import proof.ResourceBasedTest;
import proof.exception.InvalidCoverageException;

public class BranchCoverageValidatorTest extends ResourceBasedTest {

  private JSONArray simpleResource;

  public BranchCoverageValidatorTest() {
    super("branch-coverage");
  }

  @Before
  public void init() {
    simpleResource = loadJSON("simple").getJSONArray("leaves");
  }

  private final BranchCoverageValidator coverageValidator = new BranchCoverageValidator(
      createCompleteGraph(100));

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_empty() throws InvalidCoverageException, IOException {
    coverageValidator.validate(new JSONArray());
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_insufficientCoverage() throws InvalidCoverageException, IOException {
    simpleResource.remove(2);
    coverageValidator.validate(simpleResource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidMismatchedValue() throws InvalidCoverageException, IOException {
    JSONObject var =
        simpleResource.getJSONObject(0).getJSONArray("fixedVariables").getJSONObject(0);

    var.put("value", var.getInt("value") == 1 ? 0 : 1);

    coverageValidator.validate(simpleResource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidMultipleVariables() throws InvalidCoverageException, IOException {
    simpleResource.getJSONObject(0).getJSONArray("fixedVariables").getJSONObject(0)
    .getJSONArray("crossing").getJSONObject(0).getJSONObject("edge").put("source", 123);

    coverageValidator.validate(simpleResource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidSingleLeaf() throws InvalidCoverageException, IOException {
    JSONArray resource = loadJSON("single-leaf").getJSONArray("leaves");
    JSONObject var =
        simpleResource.getJSONObject(0).getJSONArray("fixedVariables").getJSONObject(0);
    resource.getJSONObject(0).getJSONArray("fixedVariables").put(var);

    coverageValidator.validate(resource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_overlappingCoverage() throws InvalidCoverageException, IOException {
    simpleResource.put(simpleResource.get(0));
    coverageValidator.validate(simpleResource);
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_unmergeable() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("unmergeable").getJSONArray("leaves"));
  }

  @Test
  public void testValidate_validCoverage() throws InvalidCoverageException, IOException {
    coverageValidator.validate(simpleResource);
  }

  @Test
  public void testValidate_validSingleLeaf() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("single-leaf").getJSONArray("leaves"));
  }

}
