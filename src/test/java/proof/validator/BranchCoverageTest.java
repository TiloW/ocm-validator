package proof.validator;

import java.io.IOException;

import org.junit.Test;

import proof.exception.InvalidCoverageException;

public class BranchCoverageTest extends ResourceBasedTest {

  @Override
  protected String getResourceSubdir() {
    return "branch-coverage";
  }

  private final BranchCoverage coverageValidator = new BranchCoverage();

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_empty() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("invalid/empty"));
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_insufficientCoverage() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("invalid/insufficient"));
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidMissmatchedIndices() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("invalid/missmatch-index"));
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidMissmatchedValues() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("invalid/missmatch-value"));
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_overlappingCoverage() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("invalid/overlapping"));
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_selfCrossing() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("invalid/self-crossing"));
  }

  @Test(expected = InvalidCoverageException.class)
  public void testValidate_invalidSingleLeaf() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("invalid/single-leaf"));
  }

  @Test
  public void testValidate_validCoverage() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("valid/simple"));
  }

  @Test
  public void testValidate_validSingleLeaf() throws InvalidCoverageException, IOException {
    coverageValidator.validate(loadJSON("valid/single-leaf"));
  }

}
