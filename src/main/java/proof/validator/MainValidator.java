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
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class MainValidator implements Validator<JSONObject> {
  private static final GraphReader graphReader = new GraphReader();

  /**
   * Validates a whole log file as provided by the OCM logger.
   */
  @Override
  public void validate(JSONObject object) throws InvalidProofException {
    Graph graph = graphReader.read(object.getJSONObject("graph"));

    Config.get().logger
        .print("trying to validate a lower bound of " + graph.getClaimedLowerBound());

    // is the proof trivial in terms of a known lower bound formula?
    if (object.getJSONObject("solution").getBoolean("trivial")) {
      Config.get().logger.reset(1);
      Config.get().logger.print("lower bound is claimed to be trivial");

      boolean valid = false;

      if (graph.getClaimedLowerBound() <= 1) {
        valid = true;
        Config.get().logger.progress("bound is less than 2");
      } else if (graph.getClaimedLowerBound() == 6 + graph.getNumberOfEdges() - 3
          * graph.getNumberOfNodes()) {
        valid = true;
        Config.get().logger.progress("bound follows from Euler's polyhedron formula");
      } else if (graph.getClaimedLowerBound() == Math.ceil(Math.pow(graph.getNumberOfEdges(), 3)
          / (33.75 * Math.pow(graph.getNumberOfNodes(), 2)) - 3 * graph.getNumberOfNodes())) {
        valid = true;
        Config.get().logger.progress("bound follows from the formula by Pach & Todt");
      }

      if (!valid) {
        throw new InvalidProofException("The claimed lower bound of "
            + graph.getClaimedLowerBound() + " is non-trivial.");
      }
    } else {
      JSONArray leaves = object.getJSONObject("solution").getJSONArray("leaves");
      int numberOfConstraints = 0;

      for (int i = 0; i < leaves.length(); i++) {
        numberOfConstraints += leaves.getJSONObject(i).getJSONArray("constraints").length();
      }

      Config.get().logger.reset(numberOfConstraints);
      BranchCoverageValidator coverageValidator = new BranchCoverageValidator(graph);

      Config.get().logger.print("branch coverage");
      coverageValidator.validate(leaves);

      for (int i = 0; i < leaves.length(); i++) {
        Config.get().logger.print("branch " + i + " of " + leaves.length());
        LeafValidator leafValidator = new LeafValidator(graph);

        try {
          leafValidator.validate(leaves.getJSONObject(i));
        } catch (InvalidProofException e) {
          throw ExceptionHelper
              .wrap(e, new InvalidProofException("Could not validate branch " + i));
        }
      }
    }
  }
}
