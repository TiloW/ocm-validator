package proof.validator;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.reader.CrossingReader;
import proof.exception.InvalidConstraintException;
import proof.validator.base.ObjectValidator;

/**
 * Validates a single Kuratwoski Constraint.
 *
 * The only type of constraints included in the log are Kuratwoski constraints. These constraints
 * will be validated by proving the respective paths do form a Kuratowski-subdivision. Valid
 * Kuratowski subdivisions in this context are the K3,3 and the K5.
 *
 * @author Tilo Wiedera
 *
 */
public class ConstraintValidator implements ObjectValidator {

  private final static CrossingReader CROSSING_READER = new CrossingReader();

  private final Map<CrossingIndex, Boolean> fixedVariables;

  public ConstraintValidator(Map<CrossingIndex, Boolean> fixedVariables) {
    this.fixedVariables = fixedVariables;
  }

  @Override
  public void validate(JSONObject object) throws InvalidConstraintException {
    JSONArray crossings = object.getJSONArray("requiredCrossings");

    final Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>(fixedVariables);

    // realize required variables
    // override branching variables if required (should never happen but still)
    for (int i = 0; i < crossings.length(); i++) {
      vars.put(CROSSING_READER.read(crossings.getJSONArray(i)), true);
    }

    JSONArray paths = object.getJSONArray("paths");

    validatePaths(vars, paths);

    if (paths.length() == 9) {
      validateK33(vars, paths);
    } else if (paths.length() == 10) {
      validateK5(vars, paths);
    } else {
      throw new InvalidConstraintException(
          "Invalid number of Kuratowski-Paths for single Constraint: " + paths.length());
    }
  }

  /**
   * For each path, we have to validate the required crossings are realized.
   *
   * @param vars
   * @param paths
   */
  private void validatePaths(Map<CrossingIndex, Boolean> vars, JSONArray paths) {
    for (int i = 0; i < paths.length(); i++) {
      JSONArray path = paths.getJSONArray(i);


    }
  }

  /**
   * Asserts that all five nodes are connected to one another.
   *
   * @param vars
   * @param paths
   */
  private void validateK5(Map<CrossingIndex, Boolean> vars, JSONArray paths) {
    // TODO
  }

  /**
   * Asserts a valid K33, i.e. a bipartit Graph with 3 nodes in each of the two subsets.
   *
   * @param vars
   * @param paths
   */
  private void validateK33(Map<CrossingIndex, Boolean> vars, JSONArray paths) {
    // TODO
  }
}
