package ca.empowered.nms.graph.topology.output;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class TopologyOutputManager {

	private static final Logger log = LogManager.getLogger(TopologyOutputManager.class.getName());
	
	private TopologyOutput topologyOutput;
	
	public TopologyOutputManager(TopologyOutput topologyOutput) {
		this.topologyOutput = topologyOutput;
	}
	
	public boolean process() {
		try {
			return this.topologyOutput.write();
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return false;
	}

	public TopologyOutput getTopologyOutput() {
		return this.topologyOutput;
	}

	public void setTopologyOutput(TopologyOutput topologyOutput) {
		this.topologyOutput = topologyOutput;
	}
	
}
