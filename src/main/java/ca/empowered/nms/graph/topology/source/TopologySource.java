package ca.empowered.nms.graph.topology.source;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

import ca.empowered.nms.graph.topology.element.Node;

/**
 * Input source can be anything - file/DB/WebService
 * 
 * Use this class for all source extensions.
 * Extend and implement the source parsing logic in parseConfigurationFile(File)
 * and map creation logic in processConfigurationFile()
 * 
 * @author mboparai
 *
 */
public abstract class TopologySource implements TopologySourceParser {

	private static final Logger log = LogManager.getLogger(TopologySource.class.getName());
	
	/**
	 * Configuration file for this source.
	 */
	protected File configurationFile;
	/**
	 * Nodes in the network.
	 */
	protected ArrayList<Node> nodes = new ArrayList<>();
	/**
	 * Network map.
	 * A -> B,C
	 * B -> E
	 */
	protected MultiValuedMap<Node, Node> networkMap = new ArrayListValuedHashMap<>();

	protected TopologySource(Resource configurationFileResource) throws IOException {
		this.configurationFile = configurationFileResource.getFile();
	}
	
	protected TopologySource(File configurationFile) {
		this.configurationFile = configurationFile;
	}
	
	@Override
	public void parseConfigurationFile() throws FileNotFoundException {
		if (this.configurationFile == null) {
			log.error("topology source requires a configuration file.");
			throw new FileNotFoundException();
		}
		parseConfigurationFile(this.configurationFile);
	}

	public File getConfigurationFile() {
		return this.configurationFile;
	}
	public void setConfigurationFile(File configurationFile) {
		this.configurationFile = configurationFile;
	}
	public ArrayList<Node> getNodes() {
		return nodes;
	}
	public void setNodes(ArrayList<Node> nodes) {
		this.nodes = nodes;
	}
	public MultiValuedMap<Node, Node> getNetworkMap() {
		return networkMap;
	}
	public void setNetworkMap(MultiValuedMap<Node, Node> networkMap) {
		this.networkMap = networkMap;
	}	
	
}
