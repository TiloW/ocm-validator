package proof.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

import proof.exception.InfeasibleLinearProgramException;
import proof.exception.InvalidLinearProgramException;

/**
 * Simple wrapper for executing the SCIP optimization suite linear program solver. This requires the
 * scip command to be available on the console.
 * 
 * @author Tilo Wiedera
 */
public class Scip implements Solver {
  @Override
  public double solve(String filename) throws InvalidLinearProgramException,
      InfeasibleLinearProgramException {
    Double result = null;
    Process process = null;

    try {
      process = Runtime.getRuntime().exec("scip -f " + filename);
      BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
      BufferedReader errorReader =
          new BufferedReader(new InputStreamReader(process.getErrorStream()));
      boolean isFeasible = false;

      for (String line = reader.readLine(); result == null && line != null; line =
          reader.readLine()) {
        if (errorReader.ready()) {
          throw new InvalidLinearProgramException(filename, errorReader.readLine());
        } else if (line.contains("problem is solved [optimal solution found]")) {
          isFeasible = true;
        } else if (line.contains("problem is solved [infeasible]")) {
          throw new InfeasibleLinearProgramException(filename);
        } else if (isFeasible && line.contains("objective value:")) {
          StringTokenizer st = new StringTokenizer(line);
          String value = null;

          while (st.hasMoreTokens()) {
            value = st.nextToken();
          }

          result = Double.parseDouble(value);
        }
      }
    } catch (IOException e) {
      Exception exception = new InvalidLinearProgramException(filename);
      exception.initCause(e);
    } finally {
      if (process != null) {
        process.destroy();
      }
    }

    if (result == null) {
      // this will never happen unless SCIP goes bonkers
      throw new InvalidLinearProgramException(filename, "SCIP output is missing some information.");
    }

    return result;
  }
}
