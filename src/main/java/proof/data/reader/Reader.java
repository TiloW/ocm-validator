package proof.data.reader;

import proof.exception.ReaderException;

/**
 * Common interface for all data readers.
 *
 * @param <T> The type of object this reader can parse
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public interface Reader<T> {

  /**
   * Parses the JSON structure.
   *
   * @param input The JSON to be parsed
   * @return The constructed object
   * @throws ReaderException iff the read data is inconsistent
   */
  public Object read(T input) throws ReaderException;
}
