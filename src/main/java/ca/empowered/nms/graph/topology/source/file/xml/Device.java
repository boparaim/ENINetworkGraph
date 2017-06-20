package ca.empowered.nms.graph.topology.source.file.xml;

//@XmlRootElement(name = "device")
public class Device {

	String groups;
	int id;
	String ip;
	String mac;
	String model;
	String name;
	String type;
	String vendor;
	String version;
	String uri;
	int endpointCount;
	int NetworkDeviceIndex;
	String networkIDs;
	String networkName;
	int numberOfVirtualChildren;
	int parentDeviceID;
	String samLicensedIndex;
	String virtualIndex;
	String changeData;
	String issueData;
	String policyData;
	
	public String toString() {
		return ""
				+ "\nID: "+id
				+ "\nIP: "+ip
				+ "\nMAC: "+mac
				+ "\nModel: "+model
				+ "\nName: "+name
				+ "\nType: "+type
				+ "\nVendor: "+vendor
				+ "\nVersion: "+version
				+ "\nURI: "+uri
				+ "\nNetwork Device Index: "+NetworkDeviceIndex
				+ "\nNetwork Name: "+networkName;
	}
}
