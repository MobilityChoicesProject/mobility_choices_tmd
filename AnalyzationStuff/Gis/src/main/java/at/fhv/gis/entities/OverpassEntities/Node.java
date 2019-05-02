package at.fhv.gis.entities.OverpassEntities;

import at.fhv.tmd.common.ICoordinate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by Johannes on 09.04.2017.
 */

public class Node {

  private long id;
  private ICoordinate location;
  private List<Way> ways = new ArrayList<>();
  private List<Relation> relations = new ArrayList<>();
  private HashMap<String,String> tags = new HashMap<>();

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public ICoordinate getLocation() {
    return location;
  }

  public void setLocation(ICoordinate location) {
    this.location = location;
  }

  public List<Way> getWays() {
    return ways;
  }

  public List<Relation> getRelations() {
    return relations;
  }

  public void addWay(Way way){
    ways.add(way);
  }
  public void addRelation(Relation relation){
    relations.add(relation);
  }

  public HashMap<String, String> getTags() {
    return tags;
  }

  public void addTag(String key, String value){
    tags.put(key,value);
  }

}
