package proof.data;

import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import proof.exception.InvalidPathException;

/**
 * Represents a Kuratowski path.
 *
 * @author Tilo Wiedera
 */
public class Path {

  /**
   * A section (or segment range) constitutes some portion of a directed edge.
   */
  class Section {
    int edge;
    int source;
    int target;
    int start;
    int end;
    boolean keepDirection;
  }

  private final Set<CrossingIndex> crossings;
  private final Graph graph;
  private final List<Section> sections;

  /**
   * Initializes a new Kuratowski path.
   *
   * @param graph The underlying {@link Graph}
   * @param crossings The currently realized crossings
   */
  public Path(Graph graph, Set<CrossingIndex> crossings) {
    this.crossings = crossings;
    this.graph = graph;
    sections = new LinkedList<Section>();
  }

  /**
   * Adds a new section to this path and ensures the path is valid.
   *
   * @param source The source of the edge
   * @param target The target of the edge
   * @param start The start index on this edge
   * @param end The end index on this edge
   * @param keepDirection Whether to traverse the edge in order
   */
  public void addSection(int source, int target, int start, int end, boolean keepDirection) {
    Section section = new Section();
    section.source = source;
    section.target = target;
    section.start = start;
    section.end = end;
    section.keepDirection = keepDirection;

    if (!graph.edgeExists(source, target)) {
      throw new InvalidPathException("Edge does not exist");
    }

    section.edge = graph.getEdgeId(source, target);

    if (sections.size() > 0) {
      sections.add(0, section);
      Object sourceObj = getSource();
      Object targetObj = getTarget();
      boolean connected = sourceObj.equals(targetObj);
      sections.remove(0);

      if (!connected) {
        throw new InvalidPathException("Path is disconnected: " + sourceObj + " != " + targetObj);
      }
    }

    sections.add(section);

    // validate the current source and target
    getSource();
    getTarget();
  }

  /**
   * Returns the source of this path.
   *
   * This can either be an Integer (node index) or a {@link CrossingIndex}.
   *
   * @return The source
   */
  public Object getSource() {
    return getEndpoint(0, true);
  }

  /**
   * Returns the target of this path.
   *
   * This can either be an Integer (node index) or a {@link CrossingIndex}.
   *
   * @return The target
   */
  public Object getTarget() {
    return getEndpoint(sections.size() - 1, false);
  }

  /**
   * Will test whether the two paths are disjoint (except for their source and target).
   *
   * @param path The path to be tested against this path
   * @return True iff the paths are disjoint
   */
  public boolean isDisjointTo(Path path) {
    boolean result = true;

    for (int i = 0; result && i <= sections.size(); i++) {
      for (int j = 0; result && j <= path.sections.size(); j++) {
        if ((i > 0 && i < sections.size()) || (j > 0 && j < path.sections.size())) {
          Object p1;
          if (i < sections.size()) {
            p1 = getEndpoint(i, true);
          } else {
            p1 = getEndpoint(i - 1, false);
          }

          Object p2;
          if (j < path.sections.size()) {
            p2 = path.getEndpoint(j, true);
          } else {
            p2 = path.getEndpoint(j - 1, false);
          }

          result = !p1.equals(p2);
        }
      }
    }

    return result;
  }

  /**
   * Returns whether this path shares a common start or end with the other one.
   *
   * @param other The other path
   * @return true iff the paths are ajacent to each other
   */
  public boolean isAdjacentTo(Path other) {
    return getSource().equals(other.getSource()) || getSource().equals(other.getTarget())
        || getTarget().equals(other.getSource()) || getTarget().equals(other.getTarget());
  }

  /**
   * Returns the node or crossing at the end (or start) of the given segment range.
   *
   * @param pos The position of the segment range in this path
   * @param getSource Whether to return the source instead of the target
   * @return The node or crossing
   */
  private Object getEndpoint(int pos, boolean getSource) {
    Object result = null;
    Section section = sections.get(pos);

    int node = section.source;
    int index = section.start;
    if ((!getSource && section.keepDirection) || (getSource && !section.keepDirection)) {
      node = section.target;
      index = section.end;
    }

    if (index == -1 || index == graph.getClaimedLowerBound()) {
      result = new Integer(node);
    } else {
      result = findCrossing(section.edge, index);
    }

    if (result == null) {
      throw new InvalidPathException(
          "Path is supposed to end at a realized crossing but there is none: "
              + new SegmentIndex(section.edge, index));
    }

    return result;
  }

  private CrossingIndex findCrossing(int edge, int index) {
    SegmentIndex seg = new SegmentIndex(edge, index);
    CrossingIndex result = null;

    for (CrossingIndex cr : crossings) {
      if (cr.segments[0].equals(seg) || cr.segments[1].equals(seg)) {
        result = cr;
      }
    }

    return result;
  }
}
