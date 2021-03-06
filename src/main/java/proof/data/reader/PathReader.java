package proof.data.reader;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.Path;
import proof.exception.InvalidPathException;

import java.util.Set;

/**
 * Used for reading Kuratowski paths.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class PathReader implements Reader<JSONArray> {
  private final Graph graph;
  private final Set<CrossingIndex> crossings;

  /**
   * Constructs a new {@link Path} reader.
   *
   * @param graph underlying non-expanded graph
   * @param crossings currently realized crossings
   */
  public PathReader(Graph graph, Set<CrossingIndex> crossings) {
    this.crossings = crossings;
    this.graph = graph;
  }

  /**
   * Reads the Kuratowski {@link Path}. A path is a list of directed segment ranges.
   *
   * @throws InvalidPathException if the sequence of segments does not constitute a path.
   */
  @Override
  public Path read(JSONArray input) throws InvalidPathException {
    Path result = new Path(graph, crossings);

    if (input.length() == 0) {
      throw new InvalidPathException("Path is empty.");
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
