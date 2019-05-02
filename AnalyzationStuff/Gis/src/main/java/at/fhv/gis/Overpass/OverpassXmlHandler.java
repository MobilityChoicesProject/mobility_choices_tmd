package at.fhv.gis.Overpass;

import static at.fhv.gis.Overpass.RelationInfo.RelationType.node;
import static at.fhv.gis.Overpass.RelationInfo.RelationType.relation;
import static at.fhv.gis.Overpass.RelationInfo.RelationType.way;

import at.fhv.gis.entities.OverpassEntities.Node;
import at.fhv.gis.entities.OverpassEntities.Relation;
import at.fhv.gis.entities.OverpassEntities.Way;
import at.fhv.tmd.common.Coordinate;
import at.fhv.tmd.common.Tuple;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 * Created by Johannes on 09.04.2017.
 */
public class OverpassXmlHandler extends DefaultHandler {

  private boolean nodeIsOpen=false;
  private boolean wayIsOpen=false;
  private boolean relationIsOpen=false;

  private TreeMap<Long,Node> nodes = new TreeMap<>();
  private TreeMap<Long,Way> ways = new TreeMap<>();
  private TreeMap<Long,Relation> relations = new TreeMap<>();


  public Collection<Relation> getRelations(){
    return relations.values();
  }
  public Collection<Way> getWays(){
    return ways.values();
  }
  public Collection<Node> getNodes(){
    return nodes.values();
  }

  private Node lastNode = null;
  private Way lastway= null;
  private Relation lastrelation= null;

  private List<Tuple<Relation,RelationInfo>> relationInfos = new ArrayList<>();

  public void startElement(String uri, String localName,String qName, Attributes atts) throws SAXException {

    localName = qName;
    if (localName.equals("node")) {

      String id = atts.getValue("id");
      String latitude = atts.getValue("lat");
      String longitude = atts.getValue("lon");
      nodeIsOpen = true;

      Node node = new Node();
      long idInt = Long.parseLong(id);
      double lat = Double.parseDouble(latitude);
      double lon = Double.parseDouble(longitude);

      node.setId(idInt);
      node.setLocation(new Coordinate(lat, lon));
      lastNode = node;
      nodes.put(idInt, node);
    }

    if (localName.equals("tag")) {
      String key = atts.getValue("k");
      String value = atts.getValue("v");

      if (nodeIsOpen) {
        lastNode.addTag(key, value);
      }else if(wayIsOpen ){
        lastway.addTag(key,value);
      }else if(relationIsOpen){
        lastrelation.addTag(key,value);
      }

    }

    if(localName.equals("nd")){
      String nodeRefStr = atts.getValue("ref");
      long id = Long.parseLong(nodeRefStr);
      Node node = nodes.get(id);
      if(wayIsOpen){
        lastway.addNode(node);

      }
    }


    if (localName.equals("way")) {
      String id = atts.getValue("id");
      wayIsOpen= true;
      long idInt = Long.parseLong(id);
      Way way = new Way();
      way.setId(idInt);
      ways.put(idInt,way);
      lastway = way;
    }

    if(localName.equals("relation")){
      relationIsOpen = true;
      String id = atts.getValue("id");
      Long idInt = Long.parseLong(id);

      Relation relation = new Relation();
      relation.setId(idInt);
      lastrelation = relation;
      relations.put(idInt,relation);
    }

    if(localName.equals("member")){
      String type = atts.getValue("type");
      String refIdStr = atts.getValue("ref");
      long refId = Long.parseLong(refIdStr);
      RelationInfo relationInfo;
      if(type.equals("node")){
      relationInfo = new RelationInfo(node,refId);
      }else if(type.equals("way")){
        relationInfo = new RelationInfo(way,refId);
      }else if(type.equals("relation")){
        relationInfo = new RelationInfo(relation,refId);
      }else{
        throw new SAXException("Unknown member type in relation: '"+type+"'");
      }
      relationInfos.add(new Tuple<Relation, RelationInfo>(lastrelation,relationInfo));
    }

  }


  @Override
  public void characters(char[] ch, int start, int length) throws SAXException {
    super.characters(ch, start, length);
    Character[] chars = new Character[length];
   for(int i = 0; i< length;i++){
     chars[i] = ch[i+start];
   }



  }

  @Override
  public void endElement(String uri, String localName, String qName) throws SAXException {
    super.endElement(uri, localName, qName);

    localName = qName;


    if(localName.equals("node")){

      nodeIsOpen = false;
      lastNode= null;
    }

    if (localName.equals("way")) {
        wayIsOpen = false;
      lastway = null;
    }
    if(localName.equals("relation")){
      relationIsOpen = false;
      lastrelation = null;
    }

    }

  @Override
  public void endDocument() throws SAXException {
    super.endDocument();

    for (Tuple<Relation, RelationInfo> relationInfoTupple : relationInfos) {
      Relation relation = relationInfoTupple.getItem1();
      RelationInfo relationInfo = relationInfoTupple.getItem2();
      switch(relationInfo.getRelationType()){
        case node:{
          Node node = nodes.get(relationInfo.getRef());
          relation.addNode(node);
          break;
        }
        case way:{
          Way way = ways.get(relationInfo.getRef());
          relation.addWay(way);
          break;
        }case relation:{
          Relation relation1 = relations.get(relationInfo.getRef());
          if(relation1 != null){
            relation.addSubRelation(relation1);

          }else{
            Object rel = relation1;
          }

          break;
        }
      }

    }

  }
}
