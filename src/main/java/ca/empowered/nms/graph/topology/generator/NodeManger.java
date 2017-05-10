package ca.empowered.nms.graph.topology.generator;

import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.graphstream.graph.Graph;

public class NodeManger {

	private Graph graph;
	
	public NodeManger(CustomGraph graph) {
		this.graph = graph;
	}
	
	public void createMap(ArrayListValuedHashMap networkMap) {
		// use concurrency to create and connect nodes in graph
		// no layout computation at this point
		generateNodes();
		connectNodes();
	}
	
	private void generateNodes() {}
	private void creatNode() {}
	private void connectNodes() {}
	private void connectTwoNodes() {}
	
	public Graph getGraph() {
		return this.graph;
	}
	
	public void setGraph(Graph graph) {
		this.graph = graph;
	}
	
}
