package ca.empowered.nms.graph.topology;

import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.collections4.MultiValuedMap;

import ca.empowered.nms.graph.api.cli.GraphViz;
import ca.empowered.nms.graph.topology.element.Node;
import ca.empowered.nms.graph.topology.output.TopologyOutputManager;
import ca.empowered.nms.graph.topology.source.TopologySourceManager;

/**
 * This is the highest level manager which controls input source, intermediate layout engine and output target.
 * 
 * @author mboparai
 *
 */
public class TopologyManager {

	TopologySourceManager topologySourceManager;
	TopologyOutputManager topologyOutputManager;
	GraphViz graphviz;
	
	public TopologyManager(TopologySourceManager topologySourceManager, TopologyOutputManager topologyOutputManager, GraphViz graphviz) {
		this.topologySourceManager = topologySourceManager;
		this.topologyOutputManager = topologyOutputManager;
		this.graphviz = graphviz;
	}
	
	public boolean process() throws IOException, FileNotFoundException, SecurityException {
		boolean mapWithLayoutWrittenSuccessfully = false;
		
		MultiValuedMap<Node, Node> networkMap = topologySourceManager.process();
		topologyOutputManager.getTopologyOutput().setNetworkMap(networkMap);
		boolean mapWithoutLayoutWrittenSuccessfully = topologyOutputManager.process();
		
		if (mapWithoutLayoutWrittenSuccessfully) {
			mapWithLayoutWrittenSuccessfully = graphviz.process();
			if (mapWithLayoutWrittenSuccessfully)
				graphviz.postProcessing();
		}
		
		return mapWithLayoutWrittenSuccessfully;
	}

	public TopologySourceManager getTopologySourceManager() {
		return topologySourceManager;
	}

	public void setTopologySourceManager(TopologySourceManager topologySourceManager) {
		this.topologySourceManager = topologySourceManager;
	}

	public TopologyOutputManager getTopologyOutputManager() {
		return topologyOutputManager;
	}

	public void setTopologyOutputManager(TopologyOutputManager topologyOutputManager) {
		this.topologyOutputManager = topologyOutputManager;
	}

	public GraphViz getGraphviz() {
		return graphviz;
	}

	public void setGraphviz(GraphViz graphviz) {
		this.graphviz = graphviz;
	}
	
}
