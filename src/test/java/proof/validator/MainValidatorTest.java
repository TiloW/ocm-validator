package proof.validator;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;

import org.json.JSONObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import proof.exception.InvalidProofException;

@RunWith(Parameterized.class)
public class MainValidatorTest {

  private final File logFile;

  public MainValidatorTest(File logFile) {
    this.logFile = logFile;
  }

  @Parameterized.Parameters
  public static File[] getFiles() throws URISyntaxException {
    File dir = new File("build/resources/test/log");

    return dir.listFiles();
  }

  @Test
  public void testValidate() throws InvalidProofException, IOException {
    String input = new String(Files.readAllBytes(logFile.toPath()));
    new MainValidator().validate(new JSONObject(input));
  }
}
