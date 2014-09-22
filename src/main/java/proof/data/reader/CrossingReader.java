package proof.data.reader;

import org.json.JSONArray;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.SegmentIndex;
import proof.data.reader.base.ArrayReader;

/**
 * Used for reading a single {@link CrossingIndex}.
 *
 * @author Tilo Wiedera
 *
 */
public class CrossingReader implements ArrayReader {

  private final SegmentReader segmentReader;

  /**
   * Creates a new crossing reader.
   *
   * @param graph The underlying {@link Graph}.
   */
  public CrossingReader(Graph graph) {
    segmentReader = new SegmentReader(graph);
  }

  /**
   * Reads a single {@link CrossingIndex}. Such an index is an array of {@link SegmentIndex}
   * containing exactly two elements.
   */
  @Override
  public CrossingIndex read(JSONArray input) {
    return new CrossingIndex(segmentReader.read(input.getJSONObject(0)), segmentReader.read(input
        .getJSONObject(1)));
  }
}
