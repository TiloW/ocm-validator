package proof.data.reader;

import org.json.JSONObject;

import proof.data.SegmentIndex;

public class SegmentReader implements ObjectReader {

  @Override
  public SegmentIndex read(JSONObject input) {
    return new SegmentIndex(input.getInt("edge"), input.getInt("segment"));
  }
}
