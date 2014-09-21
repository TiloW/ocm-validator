package proof.data.reader;

import java.util.Set;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.Path;
import proof.data.reader.base.ArrayReader;
import proof.exception.InvalidPathException;

public class PathReader implements ArrayReader {

  private final Graph graph;
  private final Set<CrossingIndex> crossings;

  public PathReader(Graph graph, Set<CrossingIndex> crossings) {
    this.crossings = crossings;
    this.graph = graph;
  }

  @Override
  public Path read(JSONArray input) {
    Path result = new Path(graph, crossings);

    if (input.length() == 0) {
      throw new InvalidPathException("Path is empty");
    }

    for (int i = 0; i < input.length(); i++) {
      JSONObject section = input.getJSONObject(i);
      JSONObject edge = section.getJSONObject("edge");

      result.addSection(edge.getInt("source"), edge.getInt("target"), section.getInt("start"),
          section.getInt("end"), section.getBoolean("keepDirection"));
    }

    return result;
  }
}
