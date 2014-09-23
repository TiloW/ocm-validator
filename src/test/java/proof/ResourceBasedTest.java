package proof;

import static org.junit.Assert.fail;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.json.JSONObject;

import proof.data.Graph;

public abstract class ResourceBasedTest {

  private final String directory;

  public ResourceBasedTest(String directory) {
    this.directory = directory;
  }

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
    String result = null;

    try {
      String path = getClass().getResource("/" + directory + "/" + filename + ".json").getPath();
      result = new String(Files.readAllBytes(Paths.get(path)));
    } catch (IOException | NullPointerException e) {
      fail("Could not read ressource: " + filename);
    }

    return new JSONObject(result);
  }
}
