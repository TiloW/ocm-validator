package proof.data;

/**
 * Represents a single segment on a single edge.
 * 
 * @author Tilo Wiedera
 *
 */
public class SegmentIndex {

  public SegmentIndex(int edge, int segment) {
    if (edge < 0) {
      throw new IllegalArgumentException("Invalid edge index");
    }
    if (segment < 0) {
      throw new IllegalArgumentException("Invalid segment index");
    }

    this.edge = edge;
    this.segment = segment;
  }

  public final int edge;
  public final int segment;

  @Override
  public boolean equals(Object other) {
    boolean result = other != null;

    if (result) {
      result &= getClass().equals(other.getClass());
      if (result) {
        SegmentIndex otherSI = (SegmentIndex) other;
        result = edge == otherSI.edge && segment == otherSI.segment;
      }
    }

    return result;
  }

  @Override
  public int hashCode() {
    return edge + segment;
  }

  @Override
  public String toString() {
    return "(" + edge + "," + segment + ")";
  }
}
