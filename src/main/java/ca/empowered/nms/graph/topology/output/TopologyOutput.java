package ca.empowered.nms.graph.topology.output;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.io.Resource;

import ca.empowered.nms.graph.topology.element.Node;

public abstract class TopologyOutput implements TopologyOutputWriter {

	private static final Logger log = LogManager.getLogger(TopologyOutput.class.getName());
	protected File outputFile;
	protected MultiValuedMap<Node, Node> networkMap;

	protected TopologyOutput(Resource outputFile) throws IOException {
		this.outputFile = outputFile.getFile();
	}
	
	public TopologyOutput(MultiValuedMap<Node, Node> networkMap, File outputFile) {
		this.networkMap = networkMap;
		this.outputFile = outputFile;
	}
	
	@Override
	public boolean write(MultiValuedMap<Node, Node> networkMap, String outputFilePath) throws FileNotFoundException {
		File file = new File(outputFilePath);
        boolean fileCreated = false;
        try {
            fileCreated = file.createNewFile();
            if (fileCreated)
            	this.outputFile = file;
        } catch (IOException e) {
			log.error("unable to create output file at given path.", e);
        	throw new FileNotFoundException();
        }
		if (this.outputFile == null) {
			log.error("no output file defined.");
			throw new FileNotFoundException();
		}
		return write(networkMap, outputFile);
	}
	
	@Override
	public boolean write(MultiValuedMap<Node, Node> networkMap) throws FileNotFoundException {
		if (this.outputFile == null) {
			log.error("no output file defined.");
			throw new FileNotFoundException();
		}
		return write(networkMap, outputFile);
	}
	
	@Override
	public boolean write() throws FileNotFoundException {
		if (this.networkMap == null) {
			log.error("not valid network map");
			return false;
		}
		if (this.outputFile == null) {
			log.error("no output file defined.");
			throw new FileNotFoundException();
		}
		return write(networkMap, outputFile);
	}

	public File getOutputFile() {
		return outputFile;
	}

	public void setOutputFile(File outputFile) {
		this.outputFile = outputFile;
	}

	public MultiValuedMap<Node, Node> getNetworkMap() {
		return networkMap;
	}

	public void setNetworkMap(MultiValuedMap<Node, Node> networkMap) {
		this.networkMap = networkMap;
	}
	
}
