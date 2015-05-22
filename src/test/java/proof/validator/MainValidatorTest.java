package proof.validator;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import proof.ValidatorTest;
import proof.exception.InvalidConfigurationException;
import proof.exception.InvalidProofException;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;

/**
 * Tests of valid proofs for the {@link MainValidator}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
@RunWith(Parameterized.class)
public class MainValidatorTest extends ValidatorTest {
  private final File proofFile;
  static final String DIR = "log";

  public MainValidatorTest(File proofFile) throws InvalidConfigurationException {
    super(DIR);
    this.proofFile = proofFile;
  }

  /**
   * Returns a collection of all JSON resource files to be tested.
   *
   * @return An array of JSON files
   */
  @Parameterized.Parameters(name = "{0}")
  public static File[] getFiles() {
    File dir = new File("build/resources/test/" + DIR);

    return dir.listFiles(new FileFilter() {
      @Override
      public boolean accept(File file) {
        return file.isFile();
      }
    });
  }

  @Test
  public void testValidate() throws InvalidProofException, IOException {
    new MainValidator().validate(loadJson(proofFile.getName()));
  }
}
