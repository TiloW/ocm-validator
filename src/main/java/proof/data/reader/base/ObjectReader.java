package proof.data.reader.base;

import org.json.JSONObject;

public interface ObjectReader extends Reader {

  public Object read(JSONObject input);
}
