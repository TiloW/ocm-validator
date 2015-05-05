package proof.validator;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.Graph;
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
      JSONArray leaves = object.getJSONObject("solution").getJSONArray("leaves");
      BranchCoverageValidator coverageValidator = new BranchCoverageValidator(graph);

      Config.get().logger.print("branch coverage");
      coverageValidator.validate(leaves);
      Config.get().logger.println("  ..OK");

      for (int i = 0; i < leaves.length(); i++) {
        Config.get().logger.println("branch " + (i + 1) + " of " + leaves.length());

        LeafValidator leafValidator = new LeafValidator(graph);
        leafValidator.validate(leaves.getJSONObject(i));

        Config.get().logger.println("branch " + (i + 1) + " validated");
      }
    }
  }
}
