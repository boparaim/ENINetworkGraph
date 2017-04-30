package ca.empowered.nms.simulator.topology.source;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.topology.element.Node;

public class TopologySourceManager {

	private static final Logger log = LogManager.getLogger(TopologySourceManager.class.getName());
	
	private TopologySource topologySource;
	
	public TopologySourceManager(TopologySource topologySource) {
		this.topologySource = topologySource;
	}
	
	public MultiValuedMap<Node, Node> process() {
		try {
			this.topologySource.parseConfigurationFile();
			this.topologySource.processConfigurationFile();
			return this.topologySource.getNetworkMap();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return null;
	}

	public TopologySource getTopologySource() {
		return this.topologySource;
	}

	public void setTopologySource(TopologySource topologySource) {
		this.topologySource = topologySource;
	}
}
