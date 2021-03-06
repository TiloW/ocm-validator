package proof.validator;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import proof.ValidatorTest;
import proof.exception.InvalidConfigurationException;
import proof.exception.InvalidCoverageException;

import java.io.IOException;

/**
 * Tests for the {@link BranchCoverageValidator}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class BranchCoverageValidatorTest extends ValidatorTest {
  private JSONArray simpleResource;

  public BranchCoverageValidatorTest() throws InvalidConfigurationException {
    super("branch-coverage");
  }

  @Before
  public void init() {
    simpleResource = loadJson("simple").getJSONArray("leaves");
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
    JSONArray resource = loadJson("single-leaf").getJSONArray("leaves");
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
    coverageValidator.validate(loadJson("unmergeable").getJSONArray("leaves"));
  }

  @Test
  public void testValidate_validCoverage() throws InvalidCoverageException, IOException {
    coverageValidator.validate(simpleResource);
  }

  @Test
  public void testValidate_validSingleLeaf() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJson("single-leaf").getJSONArray("leaves"));
  }

}
