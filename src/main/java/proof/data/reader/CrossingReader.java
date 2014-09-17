package proof.data.reader;

import org.json.JSONArray;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.reader.base.ArrayReader;

public class CrossingReader implements ArrayReader {

  private final SegmentReader segmentReader;

  public CrossingReader(Graph graph) {
    segmentReader = new SegmentReader(graph);
  }

  @Override
  public CrossingIndex read(JSONArray input) {
    return new CrossingIndex(segmentReader.read(input.getJSONObject(0)), segmentReader.read(input
        .getJSONObject(1)));
  }
}
