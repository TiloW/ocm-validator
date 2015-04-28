package proof.solver;


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
