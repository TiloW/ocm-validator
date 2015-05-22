package proof.solver;

/**
 * Tests for the {@link Cplex} linear program solver.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class CplexTest extends SolverTest {

  /**
   * Initializes a new CPLEX test.
   */
  public CplexTest() {
    super(new Initializer() {
      @Override
      public Solver initialize() {
        return new Cplex();
      }
    });
  }
}
