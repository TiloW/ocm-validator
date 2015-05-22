package proof.data.reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import proof.data.Graph;
import proof.exception.ExceptionHelper;
import proof.exception.InvalidGraphException;

/**
 * Reader for reading graph objects.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class GraphReader implements Reader<JSONObject> {

  /**
   * Constructs a graph with the specified number of nodes and edges. Nodes and edges must be
   * indexed continuously from 1 to the corresponding number. A Graph is stored as a list of edges.
   * Each edge has an associated cost which defaults to 1 if not present.
   */
  @Override
  public Graph read(JSONObject input) {
    try {
      int numberOfNodes = input.getInt("numberOfNodes");

      JSONArray edges = input.getJSONArray("edges");

      Graph result = new Graph(numberOfNodes, edges.length(), input.getInt("claimedLowerBound"));

      for (int i = 0; i < edges.length(); i++) {
        JSONObject edge = edges.getJSONObject(i);

        int cost = edge.has("cost") ? edge.getInt("cost") : 1;

        result.addEdge(edge.getInt("id"), edge.getInt("source"), edge.getInt("target"), cost);
      }

      result.makeImmutable();

      return result;

    } catch (IllegalArgumentException | JSONException e) {
      throw ExceptionHelper.wrap(e, new InvalidGraphException("Could not parse JSON"));
    }
  }
}
