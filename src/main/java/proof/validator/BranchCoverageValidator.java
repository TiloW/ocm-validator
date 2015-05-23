package proof.validator;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.Graph;
import proof.data.reader.VariablesReader;
import proof.exception.ExceptionHelper;
import proof.exception.InvalidCoverageException;
import proof.exception.ReaderException;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Validates the fixed variables of all leaves. The set of all leaves must completely cover the
 * problem at hand. This means, every possible configuration of variables must be met. The
 * {@code BranchCoverage}-Validator will ensure that no variable configuration is missing and that
 * there are no ambiguities caused by multiple leaves reporting the same or overlapping
 * configurations.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class BranchCoverageValidator implements Validator<JSONArray> {
  private final VariablesReader variablesReader;

  /**
   * Used to sort the leaves in descending number of branching variables.
   */
  static final Comparator<Map<CrossingIndex, Boolean>> LEAF_COMPARATOR =
      new Comparator<Map<CrossingIndex, Boolean>>() {
    @Override
    public int compare(Map<CrossingIndex, Boolean> vars1, Map<CrossingIndex, Boolean> vars2) {
      return vars2.size() - vars1.size();
    }
  };

  /**
   * Creates a new coverage validator.
   *
   * @param graph underlying non-expanded graph
   */
  public BranchCoverageValidator(Graph graph) {
    variablesReader = new VariablesReader(graph);
  }

  /**
   * Validates the array of leaves. Inspects the fixed variables within each leaf.
   */
  @Override
  public void validate(JSONArray leaves) throws InvalidCoverageException {
    List<Map<CrossingIndex, Boolean>> parsedVariables =
        new LinkedList<Map<CrossingIndex, Boolean>>();

    // collect the fixed variables of each leaf
    for (int i = 0; i < leaves.length(); i++) {
      JSONObject leaf = leaves.getJSONObject(i);

      JSONArray jsonVars = leaf.getJSONArray("fixedVariables");
      try {
        Map<CrossingIndex, Boolean> variablesOfLeaf = variablesReader.read(jsonVars);
        parsedVariables.add(variablesOfLeaf);
      } catch (ReaderException e) {
        throw ExceptionHelper.wrap(e, new InvalidCoverageException(
            "Encountered invalid variable indices."));
      }
    }

    if (parsedVariables.isEmpty()) {
      throw new InvalidCoverageException("Could not find any leaves.");
    }

    mergeAllLeaves(parsedVariables);
  }

  /**
   * Tries to merge matching leaves until there is only one leaf with no fixed variables left. If
   * this can not be achieved, the leaves are either overlapping or not all of the variables are
   * covered.
   *
   * @param parsedVariables fixed variables of each branch
   */
  private void mergeAllLeaves(List<Map<CrossingIndex, Boolean>> parsedVariables)
      throws InvalidCoverageException {
    // sort the leaves by number of variables
    Collections.sort(parsedVariables, LEAF_COMPARATOR);

    // merge all leaves that differ by the value of a single variable
    while (parsedVariables.size() > 1) {
      int size = parsedVariables.get(0).size();
      boolean merged = false;

      for (int i = 1; !merged && i < parsedVariables.size(); i++) {
        if (parsedVariables.get(i).size() == size) {
          merged = mergeIfPossible(parsedVariables, 0, i);
        }
      }

      if (!merged) {
        throw new InvalidCoverageException("Could not merge all leaves.");
      }
    }

    // check there is exactly one leaf remaining, covering all variables
    if (!parsedVariables.get(0).isEmpty()) {
      throw new InvalidCoverageException(
          "Some variables remain uncovered after merging all leaves.");
    }
  }

  /**
   * Tries to merge the leaves contained at {@code i} and {@code j}. The leaves will be merged if
   * they differ by the assignment of a single variable. Will remove one of the merged leaves and
   * alter the other one upon success.
   *
   * @param leaves list of all leaves
   * @param firstLeaf first leaf index
   * @param secondLeaf second leaf index
   * @return {@code true} iff the leaves were successfully merged
   */
  private boolean mergeIfPossible(List<Map<CrossingIndex, Boolean>> leaves, int firstLeaf,
      int secondLeaf) {
    boolean result = true;
    Map<CrossingIndex, Boolean> varsA = leaves.get(firstLeaf);
    Map<CrossingIndex, Boolean> varsB = leaves.get(secondLeaf);
    CrossingIndex branchVariable = null;
    Iterator<CrossingIndex> it = varsA.keySet().iterator();

    while (result && it.hasNext()) {
      CrossingIndex ci = it.next();
      result &= varsB.containsKey(ci);

      if (varsA.get(ci) != varsB.get(ci)) {
        result = branchVariable == null;
        branchVariable = ci;
      }
    }

    if (result) {
      varsA.remove(branchVariable);
      leaves.remove(varsB);
      // shift varsA to the right
      Collections.sort(leaves, LEAF_COMPARATOR);
    }

    return result;
  }
}
