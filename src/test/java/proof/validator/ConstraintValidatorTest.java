package proof.validator;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.json.JSONArray;
import org.junit.Test;

import proof.ResourceBasedTest;
import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.SegmentIndex;
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

  /**
   * Returns a new {@link ConstraintValidator}.
   *
   * @see #createValidator
   */
  private ConstraintValidator getValidator(boolean validatePaths) {
    return createValidator(new HashMap<CrossingIndex, Boolean>(), 42, validatePaths);
  }

  /**
   * Returns a basic validator that can be used for testing.
   *
   * Should not be used for testing problem specific behaviour since the underlying graph is not
   * valid. No branching variables are set. The mocked {@link PathValidator} will never fail if
   * {@code validatePaths} is set to false.
   *
   * @param numberOfSegments The crossing number
   */
  private ConstraintValidator createValidator(Map<CrossingIndex, Boolean> vars,
      int numberOfSegments, boolean validatePaths) {
    ConstraintValidator result = null;

    if (validatePaths) {
      // create K100
      int counter = 0;
      Graph graph = new Graph(100, (100 * 99) / 2);
      for (int i = 0; i < 100; i++)
        for (int ii = i + 1; ii < 100; ii++) {
          graph.addEdge(counter++, i, ii, 1);
        }
      result = new ConstraintValidator(vars, numberOfSegments, graph);
    } else {
      result = new ConstraintValidator(vars, numberOfSegments, new Graph(100, 100)) {
        @Override
        protected PathValidator createPathValidator(Set<CrossingIndex> vars, int numberOfSegments,
            Graph graph) {
          return new PathValidator(vars, numberOfSegments, graph) {
            @Override
            public void validate(JSONArray path) {}
          };
        }
      };
    }

    return result;
  }

  @Override
  protected String getResourceSubdir() {
    return "constraint";
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_emptyPaths() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/empty-paths"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_tooFewPaths() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/too-few-paths"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_tooManyPaths() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/too-many-paths"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidPath() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k33-invalid-path"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidNodesK33() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k33-invalid-nodes"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_missingPathK33() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k33-missing-path"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_missingPathK5() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k5-missing-path"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_selfLoopK5() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/k5-self-loop"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidNodesK5() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k5-invalid-nodes"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_missingCrossing() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k33-missing-crossing"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidColoring() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k33-invalid-coloring"));
  }

  @Test
  public void testValidate_crossingBranchingVariable() throws InvalidConstraintException {
    Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>();
    vars.put(new CrossingIndex(new SegmentIndex(99, 8), new SegmentIndex(198, 16)), true);
    createValidator(vars, 42, true).validate(loadJSON("invalid/k33-missing-crossing"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_crossingExcludedBranchingVariable() throws InvalidConstraintException {
    Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>();
    vars.put(new CrossingIndex(new SegmentIndex(99, 8), new SegmentIndex(198, 16)), false);
    createValidator(vars, 42, true).validate(loadJSON("invalid/k33-missing-crossing"));
  }

  @Test
  public void testValidate_requiredCrossing() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("valid/k33-required-crossing"));
  }

  @Test
  public void testValidate_simpleK5() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("valid/simple-k5"));
  }

  @Test
  public void testValidate_simpleK33() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("valid/simple-k33"));
  }
}
