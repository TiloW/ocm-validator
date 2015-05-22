package proof;

import static org.junit.Assert.fail;

import proof.exception.InvalidConfigurationException;
import proof.util.Config;

import java.io.OutputStream;
import java.io.PrintStream;

/**
 * Base class for all validator tests. In contrast to {@link ResourceBasedTest} this class relies on
 * the {@link Config} to be instanciated.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class ValidatorTest extends ResourceBasedTest {
  private static final String[] configArgs = {"-f", "src/test/resources/log/invalid/empty.json"};

  /**
   * Initializes a new validator test.
   *
   * @param directory A directory containing required JSON resources
   */
  public ValidatorTest(String directory) {
    super(directory);

    try {
      Config.get();
    } catch (RuntimeException runtimeException) {
      // config does not exists yet
      try {
        Config.create(configArgs, new PrintStream(new OutputStream() {
          @Override
          public void write(int data) {
            // don't write anything
          }
        }));
      } catch (InvalidConfigurationException invalidConfigurationException) {
        fail("Unable to initialize missing configuration");
      }
    }
  }
}
