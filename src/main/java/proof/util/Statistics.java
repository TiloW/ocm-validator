package proof.util;

import proof.solver.LinearProgramGenerator;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

/**
 * Class for collecting integer values. Used by {@link LinearProgramGenerator} to print information
 * about the generated program. All values contained in the map can be returned in a well readable
 * format (see @link {@link #format()}.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class Statistics extends HashMap<String, Integer> {
  int maxTitleLength;
  int maxValue;

  /**
   * Initializes an empty statistic.
   */
  public Statistics() {
    clear();
  }

  /**
   * Increases the value of some name by one. The empty value is assumed to be zero.
   *
   * @param name identifier of the variable to be increased
   */
  public void increase(String name) {
    if (!containsKey(name)) {
      put(name, 0);
    }

    put(name, get(name) + 1);
  }

  @Override
  public Integer put(String name, Integer value) {
    maxValue = Math.max(maxValue, value);
    maxTitleLength = Math.max(maxTitleLength, name.length());

    return super.put(name, value);
  }

  @Override
  public void clear() {
    maxTitleLength = 0;
    maxValue = 0;
  }

  /**
   * Returns all collected information in a well readable format.
   *
   * @return An alphanumerically sorted list of formatted lines
   */
  public List<String> format() {
    List<String> result = new LinkedList<>();

    List<String> names = new LinkedList<>(keySet());
    Collections.sort(names);

    for (String name : names) {
      result.add(formatNumber(name, get(name)));
    }

    return result;
  }

  /**
   * Formats a number according to the current statistics.
   *
   * @param title description of the value
   * @param number integer value
   * @return the formatted number
   */
  private String formatNumber(String title, int number) {
    return String.format(
        "# %-" + (maxTitleLength + 1) + "s %"
            + ((int) Math.log10(Math.abs(maxValue)) + 2) + "d", title + ":", number);
  }
}
