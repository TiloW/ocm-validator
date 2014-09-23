package proof.data.reader.base;

import org.json.JSONArray;

/**
 * Reader for reading from a JSON array.
 *
 * @author Tilo Wiedera
 *
 */
public interface ArrayReader extends Reader {

  /**
   * Parses the JSON array.
   *
   * @param input The JSON to be parsed
   * @return The constructed object
   */
  public Object read(JSONArray input);
}
