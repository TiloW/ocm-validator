package proof.validator;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.reader.CrossingReader;
import proof.data.reader.GraphReader;
import proof.exception.InvalidProofException;
import proof.util.Config;

/**
 * The main validator for validating a complete log file.
 *
 * @author Tilo Wiedera
 *
 */
public class MainValidator implements Validator<JSONObject> {

  private final static GraphReader graphReader = new GraphReader();

  /**
   * Called after completing the validation of a leaf.
   *
   * @param progress The progess (on a range from 0 to 1)
   */
  protected void onProgress(double progress) {}

  /**
   * Validates a whole log file as provided by the OCM logger.
   */
  @Override
  public void validate(JSONObject object) throws InvalidProofException {
    Graph graph = graphReader.read(object.getJSONObject("graph"));

    if (object.getJSONObject("solution").getBoolean("trivial")) {
      Config.get().logger.print("validating supposedly trivial lower bound");
      if (graph.getClaimedLowerBound() > 1) {
        throw new InvalidProofException("The claimed lower bound of "
            + graph.getClaimedLowerBound() + " is non-trivial.");
      }
      Config.get().logger.println("  ..OK");
    } else {

      BranchCoverageValidator coverageValidator = new BranchCoverageValidator(graph);

      JSONArray leaves = object.getJSONObject("solution").getJSONArray("leaves");

      Config.get().logger.print("branch coverage");
      coverageValidator.validate(leaves);
      Config.get().logger.println("  ..OK");

      for (int i = 0; i < leaves.length(); i++) {
        Config.get().logger.println("branch " + (i + 1) + " of " + leaves.length());
        JSONObject leaf = leaves.getJSONObject(i);
        JSONArray variables = leaf.getJSONArray("fixedVariables");

        CrossingReader crossingReader = new CrossingReader(graph);
        Map<CrossingIndex, Boolean> vars = new HashMap<CrossingIndex, Boolean>();

        for (int j = 0; j < variables.length(); j++) {
          JSONObject variable = variables.getJSONObject(j);

          CrossingIndex cross = crossingReader.read(variable.getJSONArray("crossing"));

          vars.put(cross, variable.getInt("value") == 1);
        }

        ConstraintValidator constraintValidator = new ConstraintValidator(graph, vars);
        JSONArray constraints = leaf.getJSONArray("constraints");

        for (int j = 0; j < constraints.length(); j++) {
          Config.get().logger.println("  Kuratowski constraint " + (j + 1) + " of "
              + constraints.length());
          constraintValidator.validate(constraints.getJSONObject(j));
        }

        onProgress((i + 1) / (double) leaves.length());
      }
    }
  }
}
