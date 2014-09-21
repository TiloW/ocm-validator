package proof.data;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import proof.exception.InvalidPathException;

public class Path {

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

  public Path(Graph graph, Set<CrossingIndex> crossings) {
    this.crossings = crossings;
    this.graph = graph;
    sections = new LinkedList<Section>();
  }

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
  }

  public Object getSource() {
    return getEndpoint(0, true);
  }

  public Object getTarget() {
    return getEndpoint(sections.size() - 1, false);
  }

  private Object getEndpoint(int pos, boolean getSource) {
    Object result = null;
    Section section = sections.get(pos);

    int node = section.source;
    int index = section.start;
    if ((!getSource && section.keepDirection) || (getSource && !section.keepDirection)) {
      node = section.target;
      index = section.end;
    }

    if (index >= 0) {
      result = findCrossing(section.edge, index);
    }

    if (result == null) {
      result = new Integer(node);
    }

    return result;
  }

  private CrossingIndex findCrossing(int edge, int index) {
    SegmentIndex seg = new SegmentIndex(edge, index);
    CrossingIndex result = null;

    for (Iterator<CrossingIndex> it = crossings.iterator(); it.hasNext();) {
      CrossingIndex cr = it.next();

      if (cr.segments[0].equals(seg) || cr.segments[1].equals(seg)) {
        result = cr;
      }
    }

    return result;
  }
}
