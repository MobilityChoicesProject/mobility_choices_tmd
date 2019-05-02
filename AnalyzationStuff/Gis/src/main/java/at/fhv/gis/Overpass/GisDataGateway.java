package at.fhv.gis.Overpass;

import at.fhv.gis.entities.OverpassEntities.Node;
import at.fhv.gis.entities.OverpassEntities.Relation;
import at.fhv.gis.entities.OverpassEntities.Way;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.SAXException;

/**
 * Created by Johannes on 09.04.2017.
 */
public class GisDataGateway {


  SAXParser saxParser;

  protected SAXParser getSaxParser() throws ParserConfigurationException, SAXException {
    if(saxParser == null){
      SAXParserFactory saxParserFactory = SAXParserFactory.newInstance();
      saxParser =  saxParserFactory.newSAXParser();
    }
    return saxParser;
  }


  protected OverpassReturnValue createOverpassReturnValue(OverpassXmlHandler handler){
    Collection<Node> nodes = handler.getNodes();
    Collection<Relation> relations = handler.getRelations();
    Collection<Way> ways= handler.getWays();
    OverpassReturnValue returnValue = new OverpassReturnValue(nodes,relations,ways);
    return returnValue;
  }


  public OverpassReturnValue importData(InputStream inputStream ) throws GisDataException {

    try {

      SAXParser saxParser = getSaxParser();
      OverpassXmlHandler handler = new OverpassXmlHandler();
      saxParser.parse(inputStream,handler);
      return createOverpassReturnValue(handler);

    } catch (ParserConfigurationException | SAXException | IOException e) {
      throw new GisDataException(e);
    }


  }





}
