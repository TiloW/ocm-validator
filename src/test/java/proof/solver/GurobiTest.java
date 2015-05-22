package proof.solver;

/**
 * Tests for the {@link Gurobi} linear program solver.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class GurobiTest extends SolverTest {
  public GurobiTest() {
    super(new Initializer() {
      @Override
      public Solver initialize() {
        return new Gurobi();
      }
    });
  }
}
