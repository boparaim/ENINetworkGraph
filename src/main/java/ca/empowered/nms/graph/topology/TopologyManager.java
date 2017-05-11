package ca.empowered.nms.graph.topology;

import java.io.IOException;

import org.apache.commons.collections4.MultiValuedMap;

import ca.empowered.nms.graph.api.cli.GraphViz;
import ca.empowered.nms.graph.topology.element.Node;
import ca.empowered.nms.graph.topology.output.TopologyOutputManager;
import ca.empowered.nms.graph.topology.source.TopologySourceManager;

public class TopologyManager {

	TopologySourceManager topologySourceManager;
	TopologyOutputManager topologyOutputManager;
	GraphViz graphviz;
	
	public TopologyManager(TopologySourceManager topologySourceManager, TopologyOutputManager topologyOutputManager, GraphViz graphviz) {
		this.topologySourceManager = topologySourceManager;
		this.topologyOutputManager = topologyOutputManager;
		this.graphviz = graphviz;
	}
	
	public boolean run() throws IOException {
		boolean mapWithLayoutWrittenSuccessfully = false;
		
		MultiValuedMap<Node, Node> networkMap = topologySourceManager.process();
		topologyOutputManager.getTopologyOutput().setNetworkMap(networkMap);
		boolean mapWithoutLayoutWrittenSuccessfully = topologyOutputManager.process();
		
		if (mapWithoutLayoutWrittenSuccessfully) {
			mapWithLayoutWrittenSuccessfully = graphviz.run();
		}
		
		return mapWithLayoutWrittenSuccessfully;
	}
	
}
