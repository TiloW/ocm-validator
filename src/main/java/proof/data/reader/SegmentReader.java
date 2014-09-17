package proof.data.reader;

import org.json.JSONObject;

import proof.data.Graph;
import proof.data.SegmentIndex;
import proof.data.reader.base.ObjectReader;

public class SegmentReader implements ObjectReader {

  private final Graph graph;

  public SegmentReader(Graph graph) {
    this.graph = graph;
  }

  @Override
  public SegmentIndex read(JSONObject input) {
    JSONObject edge = input.getJSONObject("edge");
    int source = edge.getInt("source");
    int target = edge.getInt("target");
    int edgeId = graph.getEdgeId(source, target);

    return new SegmentIndex(edgeId, input.getInt("segment"));
  }
}
