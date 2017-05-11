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

public abstract class TopologySource implements TopologySourceParser {

	private static final Logger log = LogManager.getLogger(TopologySource.class.getName());
	
	protected File configurationFile;
	protected ArrayList<Node> nodes = new ArrayList<>();
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
