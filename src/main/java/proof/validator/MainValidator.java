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
import proof.validator.base.ObjectValidator;

public class MainValidator implements ObjectValidator {

  private final static GraphReader graphReader = new GraphReader();

  @Override
  public void validate(JSONObject object) throws InvalidProofException {
    Graph graph = graphReader.read(object.getJSONObject("graph"));

    BranchCoverageValidator coverageValidator = new BranchCoverageValidator(graph);

    JSONArray leaves = object.getJSONObject("solution").getJSONArray("leaves");

    coverageValidator.validate(leaves);

    for (int i = 0; i < leaves.length(); i++) {
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
        constraintValidator.validate(constraints.getJSONObject(j));
      }

      System.out.println(i * 100 / (double) leaves.length());
    }
  }
}
