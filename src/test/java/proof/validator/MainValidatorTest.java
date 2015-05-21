package proof.validator;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.net.URISyntaxException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import proof.ValidatorTest;
import proof.exception.InvalidConfigurationException;
import proof.exception.InvalidProofException;

@RunWith(Parameterized.class)
public class MainValidatorTest extends ValidatorTest {
  private final File proofFile;
  static final String DIR = "log";

  public MainValidatorTest(File proofFile) throws InvalidConfigurationException {
    super(DIR);
    this.proofFile = proofFile;
  }

  @Parameterized.Parameters(name = "{0}")
  public static File[] getFiles() throws URISyntaxException {
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
    new MainValidator().validate(loadJSON(proofFile.getName()));
  }
}
