package proof.solver;

/**
 * Tests for the {@link Cplex} linear program solver.
 *
 * @author Tilo Wiedera <tilo@wiedera.de>
 */
public class CplexTest extends SolverTest {
  public CplexTest() {
    super(new Initializer() {
      @Override
      public Solver initialize() {
        return new Cplex();
      }
    });
  }
}
