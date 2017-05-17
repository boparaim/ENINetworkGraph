package ca.empowered.nms.graph.topology.output;

import java.io.File;
import java.io.FileNotFoundException;

import org.apache.commons.collections4.MultiValuedMap;

import ca.empowered.nms.graph.topology.element.Node;

/**
 * Base interface used by TopologyOutputManger.
 * @see TopologyOutput for extending the functionality.
 * 
 * @author mboparai
 *
 */
public interface TopologyOutputWriter {

	/**
	 * Write the current network map to the given file resource.
	 * 
	 * @return
	 * @throws FileNotFoundException
	 * @throws SecurityException
	 */
	public boolean write() throws FileNotFoundException, SecurityException;
	public boolean write(MultiValuedMap<Node, Node> networkMap) throws FileNotFoundException, SecurityException;
	public boolean write(MultiValuedMap<Node, Node> networkMap, File outputFile) throws FileNotFoundException, SecurityException;
	public boolean write(MultiValuedMap<Node, Node> networkMap, String outputFilePath) throws FileNotFoundException, SecurityException;
	
}
