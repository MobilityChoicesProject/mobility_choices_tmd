package at.fhv.gis.entities.OverpassEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johannes on 09.04.2017.
 */
public class Relation {

  private long id;
  private HashMap<String,String> tags = new HashMap<>();
  private List<Relation> relations =new ArrayList<>();

  private List<Way> subWays = new ArrayList<>();
  private List<Relation> subRelations = new ArrayList<>();
  private List<Node> subnodes = new ArrayList<>();
  public HashMap<String, String> getTags() {
    return tags;
  }


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public List<Way> getSubWays() {
    return subWays;
  }

  public List<Relation> getSubRelations() {
    return subRelations;
  }

  public List<Node> getSubnodes() {
    return subnodes;
  }

  public void addNode(Node node) {
    getSubnodes().add(node);
    node.addRelation(this);
  }

  public void addWay(Way way) {
    getSubWays().add(way);
    way.addRelation(this);

  }

  public void addTag(String key, String value) {
    tags.put(key,value);
  }

  public void addSubRelation(Relation relation1) {
    getSubRelations().add(relation1);
    relation1.relations.add(this);
  }
}
