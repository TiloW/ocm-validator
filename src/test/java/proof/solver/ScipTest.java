package proof.solver;

/**
 * Tests class for the {@link Scip} linear program solver.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class ScipTest extends SolverTest {
  public ScipTest() {
    super(new Initializer() {
      @Override
      public Solver initialize() {
        return new Scip();
      }
    });
  }
}
