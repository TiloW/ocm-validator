package proof.exception;

/**
 * Thrown when the user hands illegal arguments to the program.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class InvalidConfigurationException extends Exception {
  public InvalidConfigurationException(String message) {
    super(message);
  }
}
