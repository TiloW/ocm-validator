package proof;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

public abstract class ResourceBasedTest {

  protected JSONObject loadJSON(String filename) {
    StringBuilder result = new StringBuilder();

    try {
      System.out.println(getClass().getResource(filename));

      BufferedReader reader =
          new BufferedReader(new InputStreamReader(getClass().getResourceAsStream(
              "/" + getResourceSubdir() + "/" + filename + ".json")));
      String line;
      while ((line = reader.readLine()) != null) {
        result.append(line);
      }
    } catch (IOException | NullPointerException e) {
      fail("Could not read ressource: " + filename);
    }

    return new JSONObject(result.toString());
  }

  protected String getResourceSubdir() {
    return "";
  }
}
