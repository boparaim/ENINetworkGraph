package ca.empowered.nms.graph.topology.output.file.gv;

import java.io.File;
import java.io.PrintWriter;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.topology.element.Node;
import ca.empowered.nms.graph.topology.output.TopologyOutput;
import ca.empowered.nms.graph.utils.Benchmark;

public class GVFileTopologyOutput extends TopologyOutput {

	private static final Logger log = LogManager.getLogger(GVFileTopologyOutput.class.getName());
	
	public GVFileTopologyOutput(MultiValuedMap<Node, Node> networkMap, File outputFile) {
		super(networkMap, outputFile);
	}

	@Override
	public boolean write(MultiValuedMap<Node, Node> networkMap, File outputFile) {
		boolean successfullyWroteToFile = true;
		
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(this.outputFile);
			//BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(this.outputFile));
			
			//bos.write(bytes("strict graph ENINetworkGraph {"));
			//pw.println("strict graph ENINetworkGraph {");
			pw.println("strict digraph ENINetworkGraph {");
			
			for (Node nodeA : networkMap.keySet()) {
				pw.println("\t"+nodeA.getName().replaceAll("[-.]+", "_")+"\t[];");
				for (Node nodeB : networkMap.get(nodeA)) {
					pw.println("\t"+nodeB.getName().replaceAll("[-.]+", "_")+"\t[];");
					//pw.println("\t"+nodeA.getName().replaceAll("[-.]+", "_")+" -- "+nodeB.getName().replaceAll("[-.]+", "_")+"\t[];");
					
					if (nodeA.getRank() > nodeB.getRank())
						pw.println("\t"+nodeA.getName().replaceAll("[-.]+", "_")+" -> "+nodeB.getName().replaceAll("[-.]+", "_")+"\t[];");
					else
						pw.println("\t"+nodeB.getName().replaceAll("[-.]+", "_")+" -> "+nodeA.getName().replaceAll("[-.]+", "_")+"\t[];");
				}
			}
	
			/*bos.write(bytes("}"));
			bos.flush();
			bos.close();*/
			pw.println("}");
			//pw.flush();
			//pw.close();
			
			log.debug(this.outputFile.getAbsolutePath());
			log.debug("written to output file in "+Benchmark.diffFromLast("milli")+"ms");
		} catch (Exception e) {
			successfullyWroteToFile = false;
			log.error(e.getMessage(), e);
		} finally {
			try {
				if (pw != null)
					pw.close();
			} catch (Exception ex) {}
		}
		
		return successfullyWroteToFile;
	}
	
	public byte[] bytes(String msg) {
		msg = msg + "\n";
		return msg.getBytes();
	}
	
}
