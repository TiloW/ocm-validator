package proof.data;

import java.util.Arrays;

/**
 * Represents a single crossing by two edge segments.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
 */
public class CrossingIndex {

  public final SegmentIndex[] segments = new SegmentIndex[2];

  /**
   * Creates a new crossing.
   *
   * @param edgeA the edge of the first segment
   * @param segmentA the index of the first segment
   * @param edgeB the the edge of the other segment
   * @param segmentB the index of the other segment
   */
  public CrossingIndex(int edgeA, int segmentA, int edgeB, int segmentB) {
    this(new SegmentIndex(edgeA, segmentA), new SegmentIndex(edgeB, segmentB));
  }

  /**
   * Creates a new crossing.
   *
   * @param segment The first segment
   * @param otherSegment The other segment
   */
  public CrossingIndex(SegmentIndex segment, SegmentIndex otherSegment) {
    if (segment.edge == otherSegment.edge) {
      throw new IllegalArgumentException("Edge is crossing itself");
    }

    if (segment.edge > otherSegment.edge) {
      SegmentIndex tmp = segment;
      segment = otherSegment;
      otherSegment = tmp;
    }

    segments[0] = segment;
    segments[1] = otherSegment;
  }

  /**
   * Returns true iff the other crossing can not exist with this crossing. (i.e. both crossings
   * share a single segment).
   *
   * @param other The possibly conflicting segment
   * @return True iff the segments do conflict
   */
  public boolean conflicting(CrossingIndex other) {
    return (other.segments[0].equals(segments[0]) || other.segments[0].equals(segments[1])
        || other.segments[1].equals(segments[0]) || other.segments[1].equals(segments[1]))
        && !equals(other)
        && (segments[0].segment * segments[1].segment * other.segments[0].segment
            * other.segments[1].segment != 0);
  }

  @Override
  public boolean equals(Object other) {
    boolean result = other != null;

    if (result) {
      result &= getClass().equals(other.getClass());
      if (result) {
        CrossingIndex otherCrossing = (CrossingIndex) other;
        result =
            segments[0].equals(otherCrossing.segments[0])
            && segments[1].equals(otherCrossing.segments[1]);
      }
    }

    return result;
  }

  @Override
  public int hashCode() {
    return segments[0].hashCode() + segments[1].hashCode();
  }

  @Override
  public String toString() {
    return Arrays.toString(segments);
  }
}
