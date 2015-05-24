package proof.data.reader;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.exception.ReaderException;

import java.util.HashMap;
import java.util.Map;

/**
 * Reads all variables fixed on a single branch.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class VariablesReader implements Reader<JSONArray> {
  private final Graph graph;

  public VariablesReader(Graph graph) {
    this.graph = graph;
  }

  @Override
  public Map<CrossingIndex, Boolean> read(JSONArray fixedVariables) throws ReaderException {
    Map<CrossingIndex, Boolean> result = new HashMap<CrossingIndex, Boolean>();
    CrossingReader crossingReader = new CrossingReader(graph);

    for (int j = 0; j < fixedVariables.length(); j++) {
      JSONObject variable = fixedVariables.getJSONObject(j);

      CrossingIndex crossing = crossingReader.read(variable.getJSONArray("crossing"));
      boolean crossingRealized = variable.getInt("value") == 1;

      for (CrossingIndex cross : result.keySet()) {
        if (crossingRealized && result.get(cross) && crossing.conflicting(cross)) {
          throw new ReaderException("Conflicting fixed variables: " + cross + " and " + crossing
              + ".");
        }
      }

      result.put(crossing, crossingRealized);
    }

    return result;
  }

}
