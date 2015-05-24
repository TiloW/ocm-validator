package proof;

import static org.junit.Assert.fail;

import proof.data.Graph;
import proof.exception.InvalidGraphException;

/**
 * Base class for tests that require a graph.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public abstract class GraphBasedTest {

  /**
   * Creates a complete graph with a claimed lower bound of 42.
   *
   * @param numberOfNodes The number of vertices
   * @return The complete graph
   */
  protected Graph createCompleteGraph(int numberOfNodes) {
    int n = numberOfNodes;
    Graph result = new Graph(n, (n * (n - 1)) / 2, 42);
    int counter = 0;

    try {
      for (int i = 0; i < n; i++) {
        for (int ii = i + 1; ii < n; ii++) {
          result.addEdge(counter++, i, ii, 1);
        }
      }

      result.makeImmutable();
    } catch (InvalidGraphException e) {
      fail("Graph could not be created.");
    }

    return result;
  }
}
