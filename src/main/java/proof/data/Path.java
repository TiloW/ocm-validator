package proof.data;

import proof.exception.ExceptionHelper;
import proof.exception.InvalidGraphException;
import proof.exception.InvalidPathException;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

/**
 * Represents a Kuratowski path.
 *
 * @author <a href="mailto:tilo@wiedera.de">Tilo Wiedera</a>
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
   * @param graph underlying non-expanded graph
   * @param crossings currently realized crossings
   */
  public Path(Graph graph, Set<CrossingIndex> crossings) {
    this.crossings = crossings;
    this.graph = graph;
    sections = new LinkedList<Section>();
  }

  /**
   * Adds a new section to this path and ensures the path is valid.
   *
   * @param source source of the edge
   * @param target target of the edge
   * @param start start index on this edge
   * @param end end index on this edge
   * @param keepDirection whether to traverse the edge in order
   * @throws InvalidPathException if the edge does not exist, the path is disconnected or visits any
   *         node twice
   */
  public void addSection(int source, int target, int start, int end, boolean keepDirection)
      throws InvalidPathException {
    Section section = new Section();
    section.source = source;
    section.target = target;
    section.start = start;
    section.end = end;
    section.keepDirection = keepDirection;

    try {
      section.edge = graph.getEdgeId(source, target);
    } catch (InvalidGraphException e) {
      throw ExceptionHelper.wrap(e, new InvalidPathException("Edge does not exist."));
    }

    if (!sections.isEmpty()) {
      sections.add(0, section);
      Object sourceObj = getSource();
      Object targetObj = getTarget();
      boolean connected = sourceObj.equals(targetObj);
      sections.remove(0);

      if (!connected) {
        throw new InvalidPathException("Path is disconnected: " + sourceObj + " != " + targetObj
            + ".");
      }
    }

    sections.add(section);

    // validate all (dummy) vertices
    getDummyNode(0, true);
    getDummyNode(sections.size() - 1, false);

    if (collectNodes().size() != sections.size() - 1) {
      throw new InvalidPathException("Path contains duplicate nodes.");
    }
  }

  /**
   * Returns the source of this path. This can either be an Integer (node index) or a
   * {@link CrossingIndex}.
   *
   * @return the first (dummy) node of this path
   */
  public Object getSource() {
    try {
      return getDummyNode(0, true);
    } catch (InvalidPathException e) {
      // this should never happen since each path is validated upon construction
      throw ExceptionHelper.wrap(e, new RuntimeException());
    }
  }

  /**
   * Returns the target of this path. This can either be an Integer (node index) or a
   * {@link CrossingIndex}.
   *
   * @return the last (dummy) node of this path
   */
  public Object getTarget() {
    try {
      return getDummyNode(sections.size() - 1, false);
    } catch (InvalidPathException e) {
      // this should never happen since each path is validated upon construction
      throw ExceptionHelper.wrap(e, new RuntimeException());
    }
  }

  /**
   * Will test whether the two paths are disjoint (except for their source and target).
   *
   * @param path path to be tested against this path
   * @return {@code true} iff the paths are disjoint
   */
  public boolean isDisjointTo(Path path) {
    try {
      Set<Object> nodes = collectNodes();
      nodes.add(getSource());
      nodes.add(getTarget());
      nodes.addAll(path.collectNodes());

      int expectedSize = sections.size() + path.sections.size();
      boolean result = nodes.size() == expectedSize;

      if (result) {
        nodes = collectNodes();
        nodes.add(path.getSource());
        nodes.add(path.getTarget());
        nodes.addAll(path.collectNodes());

        result = nodes.size() == expectedSize;
      }

      return result;
    } catch (InvalidPathException e) {
      // this should never happen since each path is validated upon construction
      throw ExceptionHelper.wrap(e, new RuntimeException());
    }
  }

  /**
   * Returns a set of all (dummy) nodes incident to segments along this path except for the source
   * and target of this path.
   *
   * @return the set of nodes
   * @throws InvalidPathException if any required crossing is not realized
   */
  private Set<Object> collectNodes() throws InvalidPathException {
    Set<Object> result = new HashSet<>();

    for (int i = 1; i < sections.size(); i++) {
      result.add(getDummyNode(i, true));
    }

    return result;
  }

  /**
   * Returns whether this path shares a common start or end with the other one.
   *
   * @param other the other path
   * @return {@code true} iff the paths are adjacent to each other
   */
  public boolean isAdjacentTo(Path other) {
    return getSource().equals(other.getSource()) || getSource().equals(other.getTarget())
        || getTarget().equals(other.getSource()) || getTarget().equals(other.getTarget());
  }

  /**
   * Returns the node or crossing at the end (or start) of the given segment range.
   *
   * @param pos position of the segment range in this path
   * @param getSource whether to return the source instead of the target
   * @return the node or crossing
   * @throws InvalidPathException if any required crossing is not realized
   */
  private Object getDummyNode(int pos, boolean getSource) throws InvalidPathException {
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
