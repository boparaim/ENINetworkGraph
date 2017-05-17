package ca.empowered.nms.graph.topology.output.file.gv;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

import ca.empowered.nms.graph.config.Settings;
import ca.empowered.nms.graph.topology.element.Node;
import ca.empowered.nms.graph.topology.output.TopologyOutput;
import ca.empowered.nms.graph.utils.Benchmark;

/**
 * Final implementation of TopologyOutput to be used with TopologyOutputManager.
 * This entity writes the given network map to the given file.
 * It can be used for writing out the network map in dot format (http://www.graphviz.org/content/dot-language)
 * 
 * @author mboparai
 *
 */
public class GVFileTopologyOutput extends TopologyOutput {

	private static final Logger log = LogManager.getLogger(GVFileTopologyOutput.class.getName());

	public GVFileTopologyOutput(Resource configurationFileResource) throws IOException {
		super(configurationFileResource);
	}
	
	public GVFileTopologyOutput(MultiValuedMap<Node, Node> networkMap, File outputFile) {
		super(networkMap, outputFile);
	}

	@Override
	public boolean write(MultiValuedMap<Node, Node> networkMap, File outputFile) throws FileNotFoundException, SecurityException {
		boolean successfullyWroteToFile = true;
		
		PrintWriter pw = new PrintWriter(this.outputFile);
		//pw.println("strict graph ENINetworkGraph {");
		pw.println("strict digraph ENINetworkGraph { ");
		pw.println("\tgraph [ "
					+ "label=\""+Settings.getAppName()+"\", "
					+ "nodesep=\".5\" "
				+ " ]; ");
		pw.println("\tnode [ "
				+ "label=\"\" "
				//+ "fixedsize=\"true\""
				//+ "shape=\"box\""
			+ "]; ");
		pw.println("\tedge [ "
				+ "arrowhead=\"none\", "
				+ "arrowtail=\"none\", "
				+ "color=\"blue\", "
				+ "dir=\"none\", "
				+ "minlen=\"2\" "
			+ "]; ");
		
		for (Node nodeA : networkMap.keySet()) {
			pw.println("\t"+nodeA.getName().replaceAll("[-.]+", "_")+"\t"
					+ "[ "
						+ "comment=\""+nodeA.getClassName()+"\", "
						//+ "id=\""+nodeA.getName()+"\", "
						//+ "image=\""+nodeA.getName()+"\","
						+ "label=\""+nodeA.getName()+"\" "
					+ "]; ");
			for (Node nodeB : networkMap.get(nodeA)) {
				pw.println("\t"+nodeB.getName().replaceAll("[-.]+", "_")+"\t[ "
						+ "comment=\""+nodeB.getClassName()+"\", "
						//+ "id=\""+nodeB.getName()+"\", "
						//+ "image=\""+nodeA.getName()+"\","
						+ "label=\""+nodeB.getName()+"\" "
					+ "]; ");
				//pw.println("\t"+nodeA.getName().replaceAll("[-.]+", "_")+" -- "+nodeB.getName().replaceAll("[-.]+", "_")+"\t[];");
				
				if (nodeA.getRank() > nodeB.getRank())
					pw.println("\t"+nodeA.getName().replaceAll("[-.]+", "_")+" -> "+nodeB.getName().replaceAll("[-.]+", "_")+"\t"
							+ "[ "
								+ "id=\""+nodeA.getName()+"_"+nodeB.getName()+"\" "
							+ "]; ");
				else
					pw.println("\t"+nodeB.getName().replaceAll("[-.]+", "_")+" -> "+nodeA.getName().replaceAll("[-.]+", "_")+"\t"
							+ "[ "
								+ "id=\""+nodeB.getName()+"_"+nodeA.getName()+"\" "
							+ "]; ");
			}
		}
		
		pw.println("} ");
		pw.flush();
		pw.close();
		
		log.debug("writing graph layout to "+this.outputFile.getAbsolutePath());
		log.debug("written to intermediate output file in "+Benchmark.diffFromLast("milli")+"ms");
		
		return successfullyWroteToFile;
	}
	
}
