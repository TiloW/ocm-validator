package proof;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.json.JSONObject;

import proof.data.Graph;

public abstract class ResourceBasedTest {

  protected Graph createCompleteGraph(int n) {
    Graph result = new Graph(n, (n * (n - 1)) / 2);
    int counter = 0;
    for (int i = 0; i < n; i++) {
      for (int ii = i + 1; ii < n; ii++) {
        result.addEdge(counter++, i, ii, 1);
      }
    }
    result.makeImmutable();

    return result;
  }

  protected JSONObject loadJSON(String filename) {
    StringBuilder result = new StringBuilder();

    try {
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

  protected abstract String getResourceSubdir();
}
