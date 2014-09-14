package proof.data.reader;

import org.json.JSONObject;

import proof.data.SegmentIndex;
import proof.data.reader.base.ObjectReader;

public class SegmentReader implements ObjectReader {

  @Override
  public SegmentIndex read(JSONObject input) {
    return new SegmentIndex(input.getInt("edge"), input.getInt("segment"));
  }
}
