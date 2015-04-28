package proof.solver;

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
