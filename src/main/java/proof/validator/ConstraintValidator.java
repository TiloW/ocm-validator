package proof.validator;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.reader.CrossingReader;
import proof.exception.InvalidConstraintException;
import proof.exception.InvalidProofException;

/**
 * Validates a single Kuratwoski Constraint.
 *
 * The only type of constraints included in the log are Kuratwoski constraints. These constrains
 * will be validated by proving the repective paths do form a Kuratowski-Subdivision. Valid
 * Kuratowski subdivisions are the K3,3 and the K5.
 *
 * @author Tilo Wiedera
 *
 */
public class ConstraintValidator implements Validator {

  private final static CrossingReader CROSSING_READER = new CrossingReader();

  private final Map<CrossingIndex, Boolean> fixedVariables;

  public ConstraintValidator(Map<CrossingIndex, Boolean> fixedVariables) {
    this.fixedVariables = fixedVariables;
  }

  @Override
  public void validate(JSONObject object) throws InvalidProofException {
    JSONArray crossings = object.getJSONArray("requiredCrossings");

    final Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>(fixedVariables);

    // realize required variables
    // override branching variables if required (should never happen but still)
    for (int i = 0; i < crossings.length(); i++) {
      vars.put(CROSSING_READER.read(crossings.getJSONArray(i)), true);
    }

    JSONArray paths = object.getJSONArray("paths");

    boolean isK33 = paths.length() == 9;
    boolean isK5 = paths.length() == 10;

    if (!isK33 && !isK5) {
      throw new InvalidConstraintException(
          "Invalid number of Kuratowski-Paths for single Constraint: " + paths.length());
    }

    for (int i = 0; i < paths.length(); i++) {

    }
  }
}
