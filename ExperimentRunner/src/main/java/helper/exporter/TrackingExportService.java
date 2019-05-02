package helper.exporter;

import at.fhv.tmd.common.Tuple;
import at.fhv.transportdetector.trackingtypes.Constants;
import at.fhv.transportdetector.trackingtypes.Tracking;
import at.fhv.transportdetector.trackingtypes.TrackingSegmentBag;
import at.fhv.transportdetector.trackingtypes.TransportType;
import at.fhv.transportdetector.trackingtypes.segmenttypes.TrackingSegment;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class TrackingExportService extends TrajectoryExporterService {

  public void write(Tracking tracking, OutputStream outputStream) throws TrackingExportException {

    List<ExportSegmentWithTransportType> segmentWithTransportTypes = new ArrayList<>();
    TrackingSegmentBag latestTrackingSegmentBag = tracking.getLatestTrackingSegmentBag();
    for (TrackingSegment trackingSegment : latestTrackingSegmentBag.getSegments()) {
      ExportSegmentWithTransportType exportSegmentWithTransportType = new ExportSegmentWithTransportType();
      exportSegmentWithTransportType.setTransportType(trackingSegment.getTransportType());
      exportSegmentWithTransportType.setGpsPoints(trackingSegment.getGpsPoints());
      segmentWithTransportTypes.add(exportSegmentWithTransportType);
    }


    List<Tuple<String,String>> attributes = new ArrayList<>();
    String fileName = tracking.getTrackingInfo(Constants.FILENAME);
    String origin = tracking.getTrackingInfo(Constants.ORIGIN);
    attributes.add(new Tuple<>("Filename",fileName));
    attributes.add(new Tuple<>("origin",origin));
    super.write(segmentWithTransportTypes,outputStream,attributes);

  }

  @Override
  protected  void writeSegmentStart(XMLStreamWriter xmlStreamWriter, ExportSegment exportSegment ) throws XMLStreamException {
    xmlStreamWriter.writeStartElement("trkseg");
    ExportSegmentWithTransportType segmentWithTransportType = (ExportSegmentWithTransportType)exportSegment;
    xmlStreamWriter.writeAttribute("TransportType", segmentWithTransportType.getTransportType().name());
  }


  protected static class ExportSegmentWithTransportType extends ExportSegment{

    private TransportType transportType;

    public TransportType getTransportType() {
      return transportType;
    }

    public void setTransportType(TransportType transportType) {
      this.transportType = transportType;
    }
  }


  }




