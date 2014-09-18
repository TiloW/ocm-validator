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

  public ConstraintValidatorTest() {
    super("constraint");
  }

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
      Graph graph = createCompleteGraph(100);
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

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_emptyPathsK33() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/k33-empty-paths"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_tooFewPathsK33() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/k33-too-few-paths"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_tooManyPathsK33() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/k33-too-many-paths"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidPathK33() throws InvalidConstraintException {
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
  public void testValidate_invalidNodesK5() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k5-invalid-nodes"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_missingCrossingK33() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k33-missing-crossing"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidColoringK33() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("invalid/k33-invalid-coloring"));
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
  public void testValidate_tooManyPathsK5() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/k5-too-many-paths"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_invalidType() throws InvalidConstraintException {
    getValidator(false).validate(loadJSON("invalid/invalid-type"));
  }

  @Test(expected = InvalidConstraintException.class)
  public void testValidate_crossingExcludedBranchingVariableK33() throws InvalidConstraintException {
    Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>();
    vars.put(new CrossingIndex(new SegmentIndex(99, 8), new SegmentIndex(198, 16)), false);
    createValidator(vars, 42, true).validate(loadJSON("invalid/k33-missing-crossing"));
  }

  @Test
  public void testValidate_crossingBranchingVariableK33() throws InvalidConstraintException {
    Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>();
    vars.put(new CrossingIndex(new SegmentIndex(99, 8), new SegmentIndex(198, 16)), true);
    createValidator(vars, 42, true).validate(loadJSON("invalid/k33-missing-crossing"));
  }

  @Test
  public void testValidate_requiredCrossingK33() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("valid/k33-required-crossing"));
  }

  @Test
  public void testValidate_simpleK5() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("valid/k5-simple"));
  }

  @Test
  public void testValidate_simpleK33() throws InvalidConstraintException {
    getValidator(true).validate(loadJSON("valid/k33-simple"));
  }
}
