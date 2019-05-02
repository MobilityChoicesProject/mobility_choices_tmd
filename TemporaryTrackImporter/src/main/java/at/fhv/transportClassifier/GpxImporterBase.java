package at.fhv.transportClassifier;

import at.fhv.transportClassifier.scheffknechtgpx.XmlHandler;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

/**
 * Created by Johannes on 19.05.2017.
 */
public class GpxImporterBase {

  protected  XMLReader xmlReader;

  public void init(){
    SAXParserFactory spf = SAXParserFactory.newInstance();
    spf.setNamespaceAware(true);
    SAXParser saxParser = null;
    try {
      saxParser = spf.newSAXParser();

      xmlReader = saxParser.getXMLReader();
    } catch (ParserConfigurationException e) {
      e.printStackTrace();
    } catch (SAXException e) {
      e.printStackTrace();
    }
  }

  protected XmlHandler parse(File file) throws IOException, SAXException {
    XmlHandler xmlHandler = new XmlHandler();
    xmlReader.setContentHandler(xmlHandler);
    String absolutePath = file.getAbsolutePath();

    FileInputStream fileInputStream = new FileInputStream(absolutePath);



    InputSource inputSource = new InputSource(fileInputStream );
    xmlReader.parse(inputSource);
    return xmlHandler;
  }

}
