package proof.data.reader.base;

import org.json.JSONObject;

/**
 * Reader for reading from a JSON object.
 *
 * @author Tilo Wiedera
 *
 */
public interface ObjectReader extends Reader {

  /**
   * Parses the JSON array.
   *
   * @param input The JSON to be parsed
   * @return The constructed object
   */
  public Object read(JSONObject input);
}
