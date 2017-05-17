package ca.empowered.nms.graph.api.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.config.Settings;
import ca.empowered.nms.graph.utils.Benchmark;

public class GraphViz {

	private static final Logger log = LogManager.getLogger(GraphViz.class.getName());

	String graphvizApplication;
	String format;
	String inputFile;
	String outputFile;
	
	public GraphViz() {
		graphvizApplication = Settings.getGraphvizPath()+File.separator+"bin"+File.separator+Settings.getGraphvizLayout()+".exe";
		format = Settings.getGraphvizFormat();
		inputFile = "bin"+File.separator+Settings.getOutputInitialGraphFile();
		outputFile = "bin"+File.separator+Settings.getGraphvizOutputFile();
	}
	
	public boolean run() throws IOException {		
		log.debug("calling graphviz:\n"
				+ "app: "+graphvizApplication+"\n"
				+ "format: "+format+"\n"
				+ "input: "+inputFile+"\n"
				+ "output: "+outputFile);
		
		Process process = new ProcessBuilder(new String[]{
				graphvizApplication,
				"-T"+format,
				inputFile, 
				"-o"+outputFile
			}).start();
		InputStream stdout = process.getInputStream();
		InputStream stderr = process .getErrorStream();
		//OutputStream stdin = process.getOutputStream();

		String line = "";
		BufferedReader br = new BufferedReader(new InputStreamReader(stdout, "utf8"));
		while ((line = br.readLine()) != null) {
			log.debug("stdout: "+line);
			return false;
		}
		br = new BufferedReader(new InputStreamReader(stderr, "utf8"));
		while ((line = br.readLine()) != null) {
			log.debug("stderr: "+line);
			return false;
		}
		
		log.debug("graphviz layout computation took "+Benchmark.diffFromLast("milli")+"ms");
		
		return true;
	}

	public String getGraphvizApplication() {
		return graphvizApplication;
	}
	public void setGraphvizApplication(String graphvizApplication) {
		this.graphvizApplication = graphvizApplication;
	}
	public String getFormat() {
		return format;
	}
	public void setFormat(String format) {
		this.format = format;
	}
	public String getInputFile() {
		return inputFile;
	}
	public void setInputFile(String inputFile) {
		this.inputFile = inputFile;
	}
	public String getOutputFile() {
		return outputFile;
	}
	public void setOutputFile(String outputFile) {
		this.outputFile = outputFile;
	}
	
}
