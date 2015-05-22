package proof.data.reader;

/**
 * Common interface for all data readers.
 *
 * @param <T> The type of object this reader can parse
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public interface Reader<T> {

  /**
   * Parses the JSON structure.
   *
   * @param input The JSON to be parsed
   * @return The constructed object
   */
  public Object read(T input);
}
