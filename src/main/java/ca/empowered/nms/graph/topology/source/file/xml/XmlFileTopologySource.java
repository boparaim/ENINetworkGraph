package ca.empowered.nms.graph.topology.source.file.xml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;
import org.xml.sax.helpers.DefaultHandler;

import ca.empowered.nms.graph.topology.element.Node;
import ca.empowered.nms.graph.topology.source.TopologySource;
import ca.empowered.nms.graph.utils.Constants.STATE;

public class XmlFileTopologySource extends TopologySource {

	private static final Logger log = LogManager.getLogger(XmlFileTopologySource.class.getName());
	
	private SAXParser saxParser;
	private DefaultHandler handler;
	
	private XMLEventReader eventReader;
	
	private HashMap<Integer, Device> devices = new HashMap<Integer, Device>();
	private HashMap<Integer, Node> theseNodes = new HashMap<Integer, Node>();
	private ArrayList<Relationship> relationships = new ArrayList<>();
	
	/**
	 * Read the file and create nodes and edges.
	 * 
	 * @param file
	 * @throws IOException 
	 */
	public XmlFileTopologySource(Resource configurationFileResource) throws IOException {
		super(configurationFileResource);
		init();
	}
	
	public XmlFileTopologySource(File configurationFile) {
		super(configurationFile);
		init();
	}
	
	public void init() {
		try {
			SAXParserFactory factory = SAXParserFactory.newInstance();
			saxParser = factory.newSAXParser();
			
			handler = new DefaultHandler() {
				
			};
			
			XMLInputFactory inputFactory = XMLInputFactory.newInstance();
            InputStream inputStream = new FileInputStream(this.configurationFile);
            eventReader = inputFactory.createXMLEventReader(inputStream);
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	@Override
	public void parseConfigurationFile(File configurationFile) {
		try {
			// SAX
			//saxParser.parse(configurationFile, handler);
			
			// STAX
			Device device = null;
			Relationship relationship = null;
			while ( eventReader.hasNext() ) {
				XMLEvent event = eventReader.nextEvent();
                if (event.isStartElement()
                		&& event.asStartElement().getName().getLocalPart().equals("device")) {
                	device = new Device();
                	//log.debug("DEVICE");
                }
                /*
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DEVICE
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceID 22021
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceIPDotted 172.18.149.184
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceMAC 49:07:DE:F8:2D:F6
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceModel DeviceModel336
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceName DeviceName336
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceType DeviceType336
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceVendor DeviceVendor336
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceVersion DeviceVersion0.0
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - DeviceViewerUri /netmri/results/DeviceViewer/index.tdf?DeviceID=22021
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - NetworkDeviceInd 1
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - NetworkName Network01
2017-06-20 13:05:35 DEBUG XmlFileTopologySource:92 - /DEVICE
                 */
                else if (event.isStartElement()
                		&& device != null) {
                	StartElement startElement = event.asStartElement();
                	String attrName = startElement.getName().getLocalPart().toLowerCase();
                	
                	event = eventReader.nextEvent();
                	String attrValue = "";
                	if (event.isCharacters()) {
	                	Characters characters = event.asCharacters();
	                	attrValue = characters.getData();
	                	//log.debug(attrName+" "+attrValue);
                	}
                	
                	switch (attrName) {
						case "deviceid":
							device.id = Integer.parseInt(attrValue);
							break;
						case "deviceipdotted":
							device.ip = attrValue;
							break;
						case "devicemac":
							device.mac = attrValue;
							break;
						case "devicemodel":
							device.model = attrValue;
							break;
						case "devicename":
							device.name = attrValue;
							break;
						case "devicetype":
							device.type = attrValue;
							break;
						case "devicevendor":
							device.vendor = attrValue;
							break;
						case "deviceversion":
							device.version = attrValue;
							break;
						case "devicevieweruri":
							device.uri = attrValue;
							break;
						case "networkdeviceind":
							device.NetworkDeviceIndex = Integer.parseInt(attrValue);
							break;
						case "networkname":
							device.networkName = attrValue;
							break;
	
						default:
							break;
					}
                }
                else if (event.isEndElement()
                		&& event.asEndElement().getName().getLocalPart().equals("device")) {
                    if (device != null)
                    	devices.put(device.id, device);
                    device = null;
                	//log.debug("/DEVICE");
                }
                
                if (event.isStartElement()
                		&& event.asStartElement().getName().getLocalPart().equals("relationship")) {
                	relationship = new Relationship();
                	//log.debug("RELATIONSHIP");
                }
                else if (event.isStartElement()
                		&& event.asStartElement().getName().getLocalPart().equals("source_device")
                		&& relationship != null) {
                	while (true) {
            			event = eventReader.nextEvent();
            			if (event.isStartElement()
            					&& event.asStartElement().getName().getLocalPart().toLowerCase().equals("deviceid")) {
            				event = eventReader.nextEvent();
                        	String attrValue = "";
                        	if (event.isCharacters()) {
        	                	Characters characters = event.asCharacters();
        	                	attrValue = characters.getData();
        	                	//log.debug("Source device id: "+attrValue);
        	                	relationship.sourceDevice = Integer.parseInt(attrValue);
                        	}
            			}
            			if (event.isEndElement() 
            				&& event.asEndElement().getName().getLocalPart().equals("source_device")) {
            				break;
            			}
            		}
                }
                else if (event.isStartElement()
                		&& event.asStartElement().getName().getLocalPart().equals("target_device")
                		&& relationship != null) {
            		while (true) {
            			event = eventReader.nextEvent();
            			if (event.isStartElement()
            					&& event.asStartElement().getName().getLocalPart().toLowerCase().equals("deviceid")) {
            				event = eventReader.nextEvent();
                        	String attrValue = "";
                        	if (event.isCharacters()) {
        	                	Characters characters = event.asCharacters();
        	                	attrValue = characters.getData();
        	                	//log.debug("Target device id: "+attrValue);
        	                	relationship.targetDevice = Integer.parseInt(attrValue);
                        	}
            			}
            			else if (event.isEndElement() 
            				&& event.asEndElement().getName().getLocalPart().equals("target_device")) {
            				break;
            			}
            		}
                }
                else if (event.isEndElement()
                		&& event.asEndElement().getName().getLocalPart().equals("relationship")) {
                    if (relationship != null)
                    	relationships.add(relationship);
                	relationship = null;
                	//log.debug("/RELATIONSHIP");
                }
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}

	@Override
	public void processConfigurationFile() {
		nodes.clear();
		networkMap.clear();
		
		for (Device device : devices.values()) {
			Node node = new Node();
			node.setClassName("Host");
			node.setName(device.name);
			node.setDescription(device.toString());
			node.setInitialState(STATE.UNKNOWN);
			node.setRank(100);
			nodes.add(node);
			
			theseNodes.put(device.id, node);
		}
		
		for (Relationship rel : relationships) {
			log.debug("source - target : "+rel.sourceDevice+" "+rel.targetDevice);
			Device sourceDevice = devices.get(rel.sourceDevice);
			Device targetDevice = devices.get(rel.targetDevice);
			if (sourceDevice == null
					|| targetDevice == null)
				continue;
			
			log.debug("source device: "+sourceDevice.toString());
			log.debug("target device: "+targetDevice.toString());
			
			Node sourceNode = theseNodes.get(rel.sourceDevice);
			Node targetNode = theseNodes.get(rel.targetDevice);
			if (sourceNode == null
					|| targetNode == null)
				continue;
			
			networkMap.put(sourceNode, targetNode);
		}
		
		log.debug("devices: "+devices.size());
		log.debug("relationships: "+relationships.size());
	}
	

}
