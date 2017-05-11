package ca.empowered.nms.graph.topology.output;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.collections4.MultiValuedMap;

import ca.empowered.nms.graph.topology.element.Node;

public interface TopologyOutputWriter {

	public boolean write() throws FileNotFoundException;
	public boolean write(MultiValuedMap<Node, Node> networkMap) throws FileNotFoundException;
	public boolean write(MultiValuedMap<Node, Node> networkMap, File outputFile) throws FileNotFoundException;
	public boolean write(MultiValuedMap<Node, Node> networkMap, String outputFilePath) throws FileNotFoundException;
	
}
