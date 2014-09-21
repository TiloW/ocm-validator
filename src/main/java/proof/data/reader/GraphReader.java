package proof.data.reader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import proof.data.Graph;
import proof.data.reader.base.ObjectReader;
import proof.exception.InvalidGraphException;

public class GraphReader implements ObjectReader {

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
