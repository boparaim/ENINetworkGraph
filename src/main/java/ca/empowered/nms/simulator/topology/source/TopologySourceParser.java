package ca.empowered.nms.simulator.topology.source;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;

import ca.empowered.nms.simulator.topology.element.Node;

public interface TopologySourceParser {

	/**
	 * This method will parse the given configuration file.
	 * For -
	 * i. Json file with templates - it will read those templates in memory.
	 * ii. Text file with nodes and edges - it will read that information in memory.
	 * iii. Serialized object - get file path and name.
	 * iv. DB - get db, table name and credentials.
	 * 
	 * @param configurationFile
	 */
	public void parseConfigurationFile(File configurationFile);
	public void parseConfigurationFile() throws FileNotFoundException;
	
	/**
	 * This method will, if required, read the extra information from source.
	 * For -
	 * i. Json file - nothing.
	 * ii. Text file with nodes and edges - nothing.
	 * iii. Serialized object - read the object file in memory.
	 * iv. DB - read the records in memory.
	 */
	public void processConfigurationFile();
	
	/**
	 * Returns a list of node to node bindings.
	 * Eg -
	 * Node1 - Node2,Node3
	 * Node3 - Node4
	 * 
	 * @return
	 */
	public MultiValuedMap<Node, Node> getNetworkMap();
	
}
