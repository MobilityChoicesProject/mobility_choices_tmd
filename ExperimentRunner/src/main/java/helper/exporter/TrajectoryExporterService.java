package helper.exporter;

import at.fhv.tmd.common.ICoordinate;
import at.fhv.tmd.common.IGpsPoint;
import at.fhv.tmd.common.Tuple;
import at.fhv.tmd.processFlow.TmdSegment;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.IExtendedGpsPoint;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import com.sun.xml.txw2.output.IndentingXMLStreamWriter;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.TimeZone;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

/**
 * Created by Johannes on 26.08.2017.
 */
public class TrajectoryExporterService {

  public void write(List<? extends ExportSegment> segments, OutputStream outputStream,List<Tuple<String,String>> attributes) throws TrackingExportException {
    try{

    XMLStreamWriter xmlStreamWriter = XMLOutputFactory.newInstance().createXMLStreamWriter(
        new OutputStreamWriter(outputStream, "utf-8"));
    xmlStreamWriter = new IndentingXMLStreamWriter(xmlStreamWriter);

    xmlStreamWriter.writeStartDocument("UTF-8", "1.0");
    xmlStreamWriter.writeStartElement("gpx");
    xmlStreamWriter.writeAttribute("xmlns", "http://www.topografix.com/GPX/1/1");
    xmlStreamWriter.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
    xmlStreamWriter.writeAttribute("version", "1.1");
    xmlStreamWriter.writeAttribute("creator", "Johannes Neubauer");
    xmlStreamWriter.writeAttribute("xsi:schemaLocation",
        "http://www.topografix.com/GPX/1/1 http://www.topografix.com/GPX/1/1/gpx.xsd");

      for (Tuple<String, String> attribute : attributes) {
        xmlStreamWriter.writeAttribute(attribute.getItem1(),attribute.getItem2());
      }
    //tracking start
    xmlStreamWriter.writeStartElement("trk");

    //tracking segment

    for (ExportSegment segment : segments) {

      writeSegmentStart(xmlStreamWriter, segment);
      for (IGpsPoint gpsPoint : segment.getGpsPoints()) {
        writeGpsPoint(xmlStreamWriter, gpsPoint);
      }

      // trkseg end - segment end
      xmlStreamWriter.writeEndElement();

    }

    //tracking end
    xmlStreamWriter.writeEndElement();
    xmlStreamWriter.writeEndDocument();
    xmlStreamWriter.close();


    } catch (XMLStreamException | UnsupportedEncodingException e) {
      throw new TrackingExportException("Failed to export tracking",e);
    }

  }

  protected void writeGpsPoint(XMLStreamWriter xmlStreamWriter, IGpsPoint gpsPoint)
      throws XMLStreamException {
    Double accuracy = gpsPoint.getAccuracy();
    LocalDateTime mostAccurateTime = gpsPoint.getTime();
    Double latitude = gpsPoint.getLatitude();
    Double longitude = gpsPoint.getLongitude();

    String lat = latitude + "";
    String lng = longitude + "";

    DateTimeFormatter df = DateTimeFormatter.ISO_DATE_TIME; // Quoted "Z" to indicate UTC, no timezone offset

    String nowAsISO = df.format(mostAccurateTime);

    xmlStreamWriter.writeStartElement("trkpt");
    xmlStreamWriter.writeAttribute("lat", lat);
    xmlStreamWriter.writeAttribute("lon", lng);

    xmlStreamWriter.writeStartElement("time");
    xmlStreamWriter.writeCharacters(nowAsISO);
    xmlStreamWriter.writeEndElement();

    xmlStreamWriter.writeStartElement("accuracy");
    xmlStreamWriter.writeCharacters(accuracy + "");
    xmlStreamWriter.writeEndElement();

    // trkpt end
    xmlStreamWriter.writeEndElement();
  }

  protected void writeSegmentStart(XMLStreamWriter xmlStreamWriter, ExportSegment segment ) throws XMLStreamException {
    xmlStreamWriter.writeStartElement("trkseg");
  }


}
