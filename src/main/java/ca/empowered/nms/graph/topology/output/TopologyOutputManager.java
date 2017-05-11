package ca.empowered.nms.graph.topology.output;

import java.io.FileNotFoundException;

public class TopologyOutputManager {

	//private static final Logger log = LogManager.getLogger(TopologyOutputManager.class.getName());
	
	private TopologyOutput topologyOutput;
	
	public TopologyOutputManager(TopologyOutput topologyOutput) {
		this.topologyOutput = topologyOutput;
	}
	
	public boolean process() throws FileNotFoundException {
		return this.topologyOutput.write();
	}

	public TopologyOutput getTopologyOutput() {
		return this.topologyOutput;
	}

	public void setTopologyOutput(TopologyOutput topologyOutput) {
		this.topologyOutput = topologyOutput;
	}
	
}
