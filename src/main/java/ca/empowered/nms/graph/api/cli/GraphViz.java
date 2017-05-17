package ca.empowered.nms.graph.api.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.stream.Stream;

import org.apache.commons.lang.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.config.Settings;
import ca.empowered.nms.graph.utils.Benchmark;

/**
 * This entity is responsible for calling the graphviz binary to generate the final layout.
 * 
 * @author mboparai
 */
public class GraphViz {

	private static final Logger log = LogManager.getLogger(GraphViz.class.getName());

	/**
	 * Complete path to the Graphviz executable.
	 */
	private String graphvizApplication;
	/**
	 * Required output format. In current scenario, only dot language format is supported.
	 */
	private String format;
	/**
	 * Input file with graph in dot language with structure of the map.
	 */
	private String inputFile;
	/**
	 * Output file with graph in dot language with structure and final layout of the map.
	 */
	private String outputFile;
	
	public GraphViz() {
		String binary = (SystemUtils.IS_OS_UNIX)?"":".exe";
		graphvizApplication = Settings.getGraphvizPath()+File.separator+"bin"+File.separator+Settings.getGraphvizLayout()+binary;
		format = Settings.getGraphvizFormat();
		try {
			inputFile = new File(Settings.getOutputInitialGraphFile()).getCanonicalPath();
		} catch (IOException e) {
			log.error(e.getMessage(), e);
			inputFile = Settings.getOutputInitialGraphFile();
		}
		//inputFile = System.getProperty("user.dir")+File.separator+Settings.getOutputInitialGraphFile().replaceAll("/", File.separator);
		outputFile = Settings.getGraphvizOutputFile();
	}
	
	/**
	 * Executes the Graphviz binary on system and waits for the result. 
	 * 
	 * @return If No error is thrown, returns TRUE, FALSE otherwise.
	 * @throws IOException
	 */
	public boolean process() throws IOException {		
		log.info("calling graphviz:\n"
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
			log.info("stdout: "+line);
			return false;
		}
		br = new BufferedReader(new InputStreamReader(stderr, "utf8"));
		while ((line = br.readLine()) != null) {
			log.error("stderr: "+line);
			return false;
		}
		
		log.debug("graphviz layout computation took "+Benchmark.diffFromLast("milli")+"ms");
		
		return true;
	}
	
	/**
	 * This is a utility method for cleaning/streamlining the final layout text.
	 * Currently we are reading the final layout file and compute x,y coordinates from 'pos' attributes for each node.
	 * 
	 * @throws IOException 
	 */
	public void postProcessing() throws IOException {
		// make a copy
		Path source = Paths.get(Settings.getGraphvizOutputFile());
	    Path destination = Paths.get(Settings.getGraphvizOutputFile()+".temp");	 
	    Files.copy(source, destination, StandardCopyOption.REPLACE_EXISTING);
		
		@SuppressWarnings("resource")
		Stream<String> fileStream = Files.lines(Paths.get(Settings.getGraphvizOutputFile()+".temp"));
		Iterable<String> iterator = fileStream
			.map(aLine -> {
				if (aLine.trim().matches(".*pos=\"[0-9.e+-]+,[0-9.e+-]+\".*")) {
	        		String positionLine = aLine;
	        		int indexFirstQuote = positionLine.indexOf('"');						// pos="0.0e+0,1.1e+1",
	        		int indexComma = positionLine.indexOf(',');
	        		int indexSecondQuote = positionLine.indexOf('"', indexFirstQuote+1);
	        		
	        		//log.debug(positionLine+" "+indexFirstQuote+" "+indexComma+" "+indexSecondQuote);
	        		
	        		String x = positionLine.substring(indexFirstQuote + 1, indexComma);
	        		String y = positionLine.substring(indexComma + 1, indexSecondQuote);
	        		
	        		//log.debug("x: "+x+", y: "+y);
	        		aLine = positionLine.substring(0, indexSecondQuote) 
	        						+ "\",x=" + x 
	        						+ ",y="+ y 
	        						+ positionLine.substring(indexSecondQuote+1);
	        		//log.debug(originalLines[i]);
	        	}
        		aLine = aLine
    	        		.replaceAll("[ ,+]\\\\", "")	// remove \ DOT uses for continuing on the next line
    	        		.replaceAll("\"", "\\\\\"")		// json encode
    	        		.replaceAll("\t", "");
				return aLine;
			})
			::iterator;
		Files.write(
			Paths.get(Settings.getGraphvizOutputFile()), iterator, StandardOpenOption.TRUNCATE_EXISTING
		);
        log.debug("x, y parsing took "+Benchmark.diffFromLast("milli")+"ms");
		
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
