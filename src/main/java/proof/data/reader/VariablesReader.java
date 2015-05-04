package proof.data.reader;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.reader.base.ArrayReader;

/**
 * Reads all variables fixed on a single branch.
 *
 * @author Tilo Wiedera
 */
public class VariablesReader implements ArrayReader {

  private Graph graph;

  public VariablesReader(Graph graph) {
    this.graph = graph;
  }

  @Override
  public Map<CrossingIndex, Boolean> read(JSONArray fixedVariables) {
    Map<CrossingIndex, Boolean> result = new HashMap<CrossingIndex, Boolean>();
    CrossingReader crossingReader = new CrossingReader(graph);

    for (int j = 0; j < fixedVariables.length(); j++) {
      JSONObject variable = fixedVariables.getJSONObject(j);

      CrossingIndex crossing = crossingReader.read(variable.getJSONArray("crossing"));
      boolean value = variable.getInt("value") == 1;

      if (value) {
        for (CrossingIndex cross : result.keySet()) {
          if (result.get(cross) && crossing.conflicting(cross)) {
            throw new RuntimeException("Conflicting fixed variables: " + cross + " VS " + crossing);
          }
        }
      }

      result.put(crossing, value);
    }

    return result;
  }

}
