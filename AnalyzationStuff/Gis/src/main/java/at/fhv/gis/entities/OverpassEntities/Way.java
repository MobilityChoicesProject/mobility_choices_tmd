package at.fhv.gis.entities.OverpassEntities;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johannes on 09.04.2017.
 */
public class Way {

  private long id;
  private Relation relation;
  private HashMap<String,String> tags = new HashMap<>();
  private List<Node> subnodes = new ArrayList<>();

  private List<Relation> relations = new ArrayList<>();


  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public void setTags(HashMap<String, String> tags) {
    this.tags = tags;
  }

  public void setRelation(Relation relation) {
    this.relation = relation;
  }

  public Relation getRelation() {
    return relation;
  }

  public HashMap<String, String> getTags() {
    return tags;
  }

  public List<Node> getSubnodes() {
    return subnodes;
  }

  public void addTag(String key, String value) {
    tags.put(key,value);
  }

  public List<Relation> getRelations() {
    return relations;

  }
  public void addRelation(Relation relation){
    relations.add(relation);
  }

  public void addNode(Node node) {
    getSubnodes().add(node);
    node.getWays().add(this);
  }
}
