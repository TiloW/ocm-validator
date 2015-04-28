package proof.solver;


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
