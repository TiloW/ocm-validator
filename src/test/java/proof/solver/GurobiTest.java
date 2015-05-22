package proof.solver;

/**
 * Tests for the {@link Gurobi} linear program solver.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class GurobiTest extends SolverTest {

  /**
   * Initializes a new Gurobi test.
   */
  public GurobiTest() {
    super(new Initializer() {
      @Override
      public Solver initialize() {
        return new Gurobi();
      }
    });
  }
}
