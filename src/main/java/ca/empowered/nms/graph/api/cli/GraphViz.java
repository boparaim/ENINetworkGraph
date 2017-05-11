package ca.empowered.nms.graph.api.cli;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.config.Settings;

public class GraphViz {

	private static final Logger log = LogManager.getLogger(GraphViz.class.getName());
	
	public boolean run() throws IOException {
		String app = Settings.getGraphvizPath()+File.pathSeparator+"bin"+File.pathSeparator+Settings.getGraphvizLayout()+".exe";
		String format = "-T"+Settings.getGraphvizFormat();
		String inputFile = "bin"+File.pathSeparator+Settings.getOutputInitialGraphFile();
		String outputFile = "-obin"+File.pathSeparator+Settings.getGraphvizOutputFile();
		
		log.debug("calling graphviz:\n"
				+ "app: "+app
				+ "format: "+format
				+ "input: "+inputFile
				+ "output: "+outputFile);
		
		Process process = new ProcessBuilder(new String[]{
				app,
				format,
				inputFile, 
				outputFile
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
		return true;
	}
	
}
