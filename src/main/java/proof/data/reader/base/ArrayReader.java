package proof.data.reader.base;

import org.json.JSONArray;

public interface ArrayReader extends Reader {

  public Object read(JSONArray input);
}
