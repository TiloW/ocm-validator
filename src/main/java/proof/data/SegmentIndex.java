package proof.data;

/**
 * Represents a single segment on a single edge.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class SegmentIndex {
  public final int edge;
  public final int segment;

  /**
   * Creates a new segment index.
   *
   * @param edge The edge index
   * @param segment The segment index
   */
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

  @Override
  public boolean equals(Object other) {
    boolean result = other != null;

    if (result) {
      result &= getClass().equals(other.getClass());
      if (result) {
        SegmentIndex otherSegment = (SegmentIndex) other;
        result = edge == otherSegment.edge && segment == otherSegment.segment;
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
