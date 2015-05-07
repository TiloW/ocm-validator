package proof.validator;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.Graph;
import proof.data.reader.GraphReader;
import proof.exception.ExceptionHelper;
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

      boolean valid = false;

      if (graph.getClaimedLowerBound() <= 1) {
        valid = true;
        Config.get().logger.println(" ..OK (bound is less than 2)");
      } else if (graph.getClaimedLowerBound() == 6 + graph.getNumberOfEdges() - 3
          * graph.getNumberOfNodes()) {
        valid = true;
        Config.get().logger.println(" ..OK (bound follows from Euler's polyhedron formula)");
      } else if (graph.getClaimedLowerBound() == Math.ceil(Math.pow(graph.getNumberOfEdges(), 3)
          / (33.75 * Math.pow(graph.getNumberOfNodes(), 2)) - 3 * graph.getNumberOfNodes())) {
        valid = true;
        Config.get().logger.println(" ..OK (bound follows from the formula by Pach & Todt)");
      }

      if (!valid) {
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

        try {
          leafValidator.validate(leaves.getJSONObject(i));
        } catch (InvalidProofException e) {
          throw ExceptionHelper.wrap(e, new InvalidProofException("Could not validate leaf #" + i));
        }

        Config.get().logger.println("branch " + (i + 1) + " validated");
      }
    }
  }
}
