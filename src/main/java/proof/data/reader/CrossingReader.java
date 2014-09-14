package proof.data.reader;

import org.json.JSONArray;

import proof.data.CrossingIndex;
import proof.data.reader.base.ArrayReader;

public class CrossingReader implements ArrayReader {
  private final static SegmentReader SEGMENT_READER = new SegmentReader();

  @Override
  public CrossingIndex read(JSONArray input) {
    return new CrossingIndex(SEGMENT_READER.read(input.getJSONObject(0)), SEGMENT_READER.read(input
        .getJSONObject(1)));
  }
}
