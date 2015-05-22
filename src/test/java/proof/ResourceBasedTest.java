package proof;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

/**
 * Base class for tests that require JSON resources.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
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
  protected JSONObject loadJSON(String filename) {
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
