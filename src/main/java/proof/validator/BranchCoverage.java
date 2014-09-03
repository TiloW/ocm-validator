package proof.validator;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import proof.data.CrossingIndex;
import proof.data.SegmentIndex;
import proof.exception.InvalidCoverageException;

/**
 * Validates the fixed variables of all leaves.
 * 
 * The set of all leaves must completely cover the problem at hand. This means, every possible
 * configuration of variables must be met. The {@code BranchCoverage}-Validator will ensure that no
 * variable configuration is missing and that there are no ambiguities caused by multiple leaves
 * reporting the same or overlapping configurations.
 * 
 * @author Tilo Wiedera
 *
 */
public class BranchCoverage implements Validator {

  /**
   * Used to sort the leaves in descending number of branching variables.
   */
  final static Comparator<Map<CrossingIndex, Boolean>> LEAF_COMPARATOR =
      new Comparator<Map<CrossingIndex, Boolean>>() {
        @Override
        public int compare(Map<CrossingIndex, Boolean> vars1, Map<CrossingIndex, Boolean> vars2) {
          return vars2.size() - vars1.size();
        }
      };

  @Override
  public void validate(JSONObject object) throws InvalidCoverageException {
    JSONArray leaves = object.getJSONArray("leaves");

    List<Map<CrossingIndex, Boolean>> parsedVariables =
        new LinkedList<Map<CrossingIndex, Boolean>>();

    // collect the fixed variables of each leaf
    for (int i = 0; i < leaves.length(); i++) {
      JSONObject leaf = leaves.getJSONObject(i);
      JSONArray fixedVariables = leaf.getJSONArray("fixedVariables");

      Map<CrossingIndex, Boolean> variablesOfLeaf = new HashMap<CrossingIndex, Boolean>();
      for (int j = 0; j < fixedVariables.length(); j++) {
        JSONObject variable = fixedVariables.getJSONObject(j);

        JSONArray segments = variable.getJSONArray("segments");
        JSONObject s1 = segments.getJSONObject(0);
        JSONObject s2 = segments.getJSONObject(1);

        try {
          variablesOfLeaf.put(
              new CrossingIndex(new SegmentIndex(s1.getInt("edge"), s1.getInt("segment")),
                  new SegmentIndex(s2.getInt("edge"), s2.getInt("segment"))), variable
                  .getInt("value") == 1);
        } catch (IllegalArgumentException e) {
          throw new InvalidCoverageException("Encountered invalid variable indices.");
        }
      }
      parsedVariables.add(variablesOfLeaf);
    }

    // sort the leaves by number of variables
    Collections.sort(parsedVariables, LEAF_COMPARATOR);

    if (parsedVariables.size() == 0) {
      throw new InvalidCoverageException("Could not find any leaves.");
    }

    // merge all leaves that differ by the value of a single variable
    while (parsedVariables.size() > 1) {
      int size = parsedVariables.get(0).size();

      boolean merged = false;
      for (int i = 1; !merged && i < parsedVariables.size()
          && parsedVariables.get(i).size() == size; i++) {
        merged = mergeIfPossible(parsedVariables, 0, i);
      }
      if (!merged) {
        throw new InvalidCoverageException("Could not merge all leaves.");
      }
    }

    // check there is exactly one leaf remaining, covering all variables
    if (parsedVariables.get(0).size() > 0) {
      throw new InvalidCoverageException(
          "Some variables remain uncovered after merging all leaves.");
    }
  }

  /**
   * Tries to merge the leaves contained at {@code i} and {@code j}. The leaves will be merged if
   * they differ by the assignment of a single variable.
   * 
   * @param leaves The list of all leaves
   * @param i The first leaf index
   * @param j The second leaf index
   * @return True iff the leaves were successfully merged
   */
  private boolean mergeIfPossible(List<Map<CrossingIndex, Boolean>> leaves, int i, int j) {
    boolean result = true;

    Map<CrossingIndex, Boolean> varsA = leaves.get(i);
    Map<CrossingIndex, Boolean> varsB = leaves.get(j);

    CrossingIndex branchVariable = null;

    Iterator<CrossingIndex> it = varsA.keySet().iterator();
    while (result && it.hasNext()) {
      CrossingIndex ci = it.next();
      result &= varsB.containsKey(ci);
      if (varsA.get(ci) != varsB.get(ci)) {
        if (branchVariable == null) {
          branchVariable = ci;
        } else {
          result = false;
        }
      }
    }

    if (result) {
      varsA.remove(branchVariable);
      leaves.remove(varsB);
      Collections.sort(leaves, LEAF_COMPARATOR); // shift varsA to the right
    }

    return result;
  }
}
