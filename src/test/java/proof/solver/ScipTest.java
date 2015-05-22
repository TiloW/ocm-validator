package proof.solver;

/**
 * Tests class for the {@link Scip} linear program solver.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class ScipTest extends SolverTest {

  /**
   * Initializes a new Scip test.
   */
  public ScipTest() {
    super(new Initializer() {
      @Override
      public Solver initialize() {
        return new Scip();
      }
    });
  }
}
