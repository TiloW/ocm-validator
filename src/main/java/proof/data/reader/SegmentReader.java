package proof.data.reader;

import org.json.JSONObject;

import proof.data.Graph;
import proof.data.SegmentIndex;
import proof.exception.ExceptionHelper;
import proof.exception.InvalidGraphException;
import proof.exception.ReaderException;

/**
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class SegmentReader implements Reader<JSONObject> {
  private final Graph graph;

  public SegmentReader(Graph graph) {
    this.graph = graph;
  }

  @Override
  public SegmentIndex read(JSONObject input) throws ReaderException {
    JSONObject edge = input.getJSONObject("edge");
    int source = edge.getInt("source");
    int target = edge.getInt("target");

    try {
      int edgeId = graph.getEdgeId(source, target);
      return new SegmentIndex(edgeId, input.getInt("segment"));
    } catch (InvalidGraphException e) {
      throw ExceptionHelper.wrap(e, new ReaderException("Required edge does not exist"));
    }
  }
}
