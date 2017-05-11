package ca.empowered.nms.graph.topology.source;

import java.io.FileNotFoundException;

import org.apache.commons.collections4.MultiValuedMap;

import ca.empowered.nms.graph.topology.element.Node;

public class TopologySourceManager {

	//private static final Logger log = LogManager.getLogger(TopologySourceManager.class.getName());
	
	private TopologySource topologySource;
	
	public TopologySourceManager(TopologySource topologySource) {
		this.topologySource = topologySource;
	}
	
	public MultiValuedMap<Node, Node> process() throws FileNotFoundException {
		this.topologySource.parseConfigurationFile();
		this.topologySource.processConfigurationFile();
		return this.topologySource.getNetworkMap();
	}

	public TopologySource getTopologySource() {
		return this.topologySource;
	}

	public void setTopologySource(TopologySource topologySource) {
		this.topologySource = topologySource;
	}
}
