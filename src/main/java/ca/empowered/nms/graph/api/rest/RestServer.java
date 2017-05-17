package ca.empowered.nms.graph.api.rest;

import static spark.Spark.get;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.config.Settings;
import ca.empowered.nms.graph.topology.TopologyManager;
import ca.empowered.nms.graph.utils.Benchmark;
import spark.Request;
import spark.Response;
import spark.Spark;

/**
 * This entity is responsible for managing all web service calls.
 * 
 * @author mboparai
 *
 */
public class RestServer {

	private static final Logger log = LogManager.getLogger(RestServer.class.getName());
	
	/**
	 * This is not used here for now, but is kept for future use.
	 */
	private TopologyManager topologyManager;
	
	/**
	 * RestServer - add all managed paths.
	 */
	public RestServer(TopologyManager topologyManager) {
		log.debug("INIT -> listening for rest requests on http://" 
				+ Settings.getRestServerIP() + ":" + Settings.getRestServerPort()
				+Settings.getRestServerPath());
		
		this.topologyManager = topologyManager;

		Spark.staticFileLocation("/public");
		System.setProperty("SPARK_LOCAL_IP", Settings.getRestServerIP());
		// TODO: this doesn't work
		System.setProperty("SPARK_LOCAL_PORT", String.valueOf(Settings.getRestServerPort()));
		
		
		// using apache spark
		// by default port is 4567
		// options can be set on command line - https://github.com/apache/spark/blob/master/conf/spark-env.sh.template
		
		// GET - show
		// POST - create
		// PUT - update
		// DELETE - remove
		
		// TODO: for development we are doing everything with GET
		// later - remove /get,/set, etc from paths  and get() routes
		
		get(Settings.getRestServerPath()+"/get/help", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/graph/layout", (request, response) -> {
			return processWebRequest(request, response).body();
		});
	}
	
	/**
	 * Process web calls. Accept params from URL and send out JSON data.
	 * 
	 * @param request
	 * @param response
	 * @return
	 */
	private Response processWebRequest(Request request, Response response) {
		Benchmark.init();
		response.status(200);
        response.type("application/json");
        
        String[] path = request.pathInfo().split("/");
        path = Arrays.stream(path)
        		.filter(s -> (s != null && !s.isEmpty()))
        		.toArray(String[]::new);
        
        String data = "{}";
        String method = Arrays.toString(path).replaceAll(", ", ".");
        
        String status = "ERROR";
        String payload = "Undefined method.";
        
        switch (path[1]) {
	        case "get":
	        	switch (path[2]) {
		        	case "help":
		        		status = "SUCCESS";
		        		payload = "Available paths: \n"
		        				+ "/get/help\t\t shows this help\n"
		        				+ "/get/graph/layout\t\t gives initial layout in dot format.\n";
		        		break;
		        	case "graph":
		        		switch (path[3]) {
		        			case "layout":
				        		status = "SUCCESS";
		        				payload = readLayoutFile();
		        				break;
		        			default:
		        		}
		        		break;		        		
		        	default:		        		
	        	}
	        	break;
        	default:
        }
        
        data = "{\"method\":\""+method + "\", "
				+ "\"status\":\""+status+"\", "
				+ "\"payload\":\""+payload+"\"}";
        
        if (data.length() > 200)
        	log.debug("duration["+Benchmark.diffFromLast("milli")+"ms] "+request.requestMethod() 
					+ " request at " + request.pathInfo() + " Response: " + data.substring(0, 100)
					+".."+data.substring(data.length()-100, data.length()));
        else
        	log.debug("duration["+Benchmark.diffFromLast("milli")+"ms] "+request.requestMethod() 
					+ " request at " + request.pathInfo() + " Response: " + data);
		//response.redirect("index.html");
        response.body( data );
		
		return response;
	}
	
	/**
	 * Reads the final layout file and computes x,y coordinates from 'pos' attributes for each node.
	 * TODO: implement streams, this will break if the resulting string is > 2^31-1
	 * 
	 * @return String containing usable graph description in dot format.
	 */
	private String readLayoutFile() {
		File graphLayoutFile = new File(Settings.getGraphvizOutputFile());
		
		try {
			if (!graphLayoutFile.exists())
				throw new FileNotFoundException();
			
	        StringBuffer fileData = new StringBuffer();
	        BufferedReader reader = new BufferedReader(new FileReader(graphLayoutFile) );
	        char[] buf = new char[1024];
	        int numRead=0;
	        while ((numRead = reader.read(buf)) != -1) {
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	        }
	        reader.close();
	        
	        log.debug("reading of layout file took "+Benchmark.diffFromLast("milli")+"ms");
	        
	        return fileData.toString()
	        		.replaceAll("\n", "")			// remove all white spaces
	        		.replaceAll("\r", "");
		} catch (Exception e) {
			log.error(e.getMessage(), e);
			return "Error reading the graph layout file from server. "+e.getMessage();
		}
    }

	public TopologyManager getTopologyManager() {
		return topologyManager;
	}

	public void setTopologyManager(TopologyManager topologyManager) {
		this.topologyManager = topologyManager;
	}
	
}
