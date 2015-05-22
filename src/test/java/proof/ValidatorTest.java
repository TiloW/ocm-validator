package proof;

import java.io.OutputStream;
import java.io.PrintStream;

import proof.exception.InvalidConfigurationException;
import proof.util.Config;

/**
 * Base class for all validator tests. In contrast to {@link ResourceBasedTest} this class relies on
 * the {@link Config} to be instanciated.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class ValidatorTest extends ResourceBasedTest {
  private final static String[] configArgs = {"-f", "src/test/resources/log/invalid/empty.json"};

  public ValidatorTest(String directory) throws InvalidConfigurationException {
    super(directory);

    try {
      Config.get();
    } catch (RuntimeException e) {
      // config does not exists yet
      Config.Create(configArgs, new PrintStream(new OutputStream() {
        @Override
        public void write(int b) {}
      }));
    }
  }
}
