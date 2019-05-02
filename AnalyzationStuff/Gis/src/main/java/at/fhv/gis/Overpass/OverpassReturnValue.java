package at.fhv.gis.Overpass;

import at.fhv.gis.entities.OverpassEntities.Node;
import at.fhv.gis.entities.OverpassEntities.Relation;
import at.fhv.gis.entities.OverpassEntities.Way;
import java.util.Collection;

/**
 * Created by Johannes on 08.05.2017.
 */
public class OverpassReturnValue {

 private Collection<Node> nodes;
 private Collection<Relation> relations;
 private Collection<Way> ways;

  public OverpassReturnValue(Collection<Node> nodes,
      Collection<Relation> relations, Collection<Way> ways) {
    this.nodes = nodes;
    this.relations = relations;
    this.ways = ways;
  }

  public Collection<Node> getNodes() {
    return nodes;
  }

  public Collection<Relation> getRelations() {
    return relations;
  }

  public Collection<Way> getWays() {
    return ways;
  }
}
