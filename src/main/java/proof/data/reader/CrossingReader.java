package proof.data.reader;

import org.json.JSONArray;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.SegmentIndex;
import proof.exception.ReaderException;

/**
 * Used for reading a single {@link CrossingIndex}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class CrossingReader implements Reader<JSONArray> {
  private final SegmentReader segmentReader;
  private final Graph graph;

  /**
   * Creates a new crossing reader.
   *
   * @param graph The underlying {@link Graph}.
   */
  public CrossingReader(Graph graph) {
    this.graph = graph;
    segmentReader = new SegmentReader(graph);
  }

  /**
   * Reads a single {@link CrossingIndex}. A crossing is denoted by two {@link SegmentIndex segment
   * indices}.
   *
   * @throws ReaderException if the crossing is infeasible
   */
  @Override
  public CrossingIndex read(JSONArray input) throws ReaderException {
    SegmentIndex seg1 = segmentReader.read(input.getJSONObject(0));
    SegmentIndex seg2 = segmentReader.read(input.getJSONObject(1));

    CrossingIndex result = new CrossingIndex(seg1, seg2);

    int source1 = graph.getEdgeSource(seg1.edge);
    int target1 = graph.getEdgeTarget(seg1.edge);

    int source2 = graph.getEdgeSource(seg2.edge);
    int target2 = graph.getEdgeTarget(seg2.edge);

    if (source1 == source2 || source1 == target2 || target1 == source2 || target1 == target2) {
      throw new ReaderException("Adjacent edges never cross: " + result);
    }

    return result;
  }
}
