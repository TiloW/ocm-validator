package proof;

import static org.junit.Assert.fail;

import org.json.JSONObject;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

/**
 * Base class for tests that require JSON resources.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public abstract class ResourceBasedTest extends GraphBasedTest {

  private final String directory;

  /**
   * Initializes a new resource based test.
   *
   * @param directory The base directory containing all resource files to be loaded by this test.
   */
  public ResourceBasedTest(String directory) {
    this.directory = directory;
  }

  /**
   *
   * @param filename The name of the file to be loaded (relative to the base {@link #directory}).
   * @return The parsed JSON object
   */
  protected JSONObject loadJson(String filename) {
    String result = null;

    if (!filename.substring(filename.length() - 5).equals(".json")) {
      filename += ".json";
    }

    try {
      result =
          new String(Files.readAllBytes(Paths.get("build/resources/test/" + directory + "/"
              + filename)));
    } catch (IOException | NullPointerException e) {
      fail("Could not read ressource: " + filename);
    }

    return new JSONObject(result);
  }
}
