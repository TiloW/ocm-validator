package proof.data.reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import proof.data.Graph;
import proof.data.reader.base.ObjectReader;
import proof.exception.InvalidGraphException;

/**
 * Reader for reading graph objects.
 *
 * @author Tilo Wiedera
 *
 */
public class GraphReader implements ObjectReader {

  /**
   * Constructs a graph with the specified number of nodes and edges. Nodes and edges must be
   * indexed continuously from 1 to the corresponding number.
   *
   * A Graph is stored as a list of edges. Each edge has an associated cost which defaults to 1 if
   * not present.
   */
  @Override
  public Graph read(JSONObject input) {
    try {
      int numberOfNodes = input.getInt("numberOfNodes");

      JSONArray edges = input.getJSONArray("edges");

      Graph result = new Graph(numberOfNodes, edges.length());

      for (int i = 0; i < edges.length(); i++) {
        JSONObject edge = edges.getJSONObject(i);

        double cost = edge.has("cost") ? edge.getDouble("cost") : 1;

        result.addEdge(edge.getInt("id"), edge.getInt("source"), edge.getInt("target"), cost);
      }

      result.makeImmutable();

      return result;

    } catch (IllegalArgumentException | JSONException e) {
      InvalidGraphException exception = new InvalidGraphException("Could not parse JSON");
      exception.initCause(e);
      throw exception;
    }
  }
}