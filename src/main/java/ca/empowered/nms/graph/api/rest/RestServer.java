package ca.empowered.nms.graph.api.rest;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

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
	
	TopologyManager topologyManager;
	
	/**
	 * RestServer - add all managed paths.
	 */
	public RestServer(TopologyManager topologyManager) {
		log.debug("INIT -> listening for rest requests on http://" 
				+ Settings.getRestServerIP() + ":" + Settings.getRestServerPort()
				+Settings.getRestServerPath());
		
		this.topologyManager = topologyManager;
		
		System.setProperty("SPARK_LOCAL_IP", Settings.getRestServerIP());
		// TODO: this doesn't work
		System.setProperty("SPARK_LOCAL_PORT", String.valueOf(Settings.getRestServerPort()));
		
		Spark.staticFileLocation("/public");
		
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
		
        post(Settings.getRestServerPath(), (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		put(Settings.getRestServerPath()+"/set/node-state/:state", (request, response) -> {
			return processWebRequest(request, response).body();
		});

		delete(Settings.getRestServerPath()+"/", (request, response) -> {
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
        //String pathText = Arrays.toString(path).replaceAll(", ", ".");
        
        switch (path[1]) {
	        case "get":
	        	switch (path[2]) {
		        	case "help":
		        		data = "running !!";
		        		break;
		        	case "graph":
		        		switch (path[3]) {
		        			case "layout":
		        				data = readLayoutFile();
		        				break;
		        			default:
		        		}
		        		break;
		        		
		        	default:
		        		
	        	}
	        	break;
	        case "post":
	        	break;
	        case "set":
	        	switch (path[2]) {
		        	case "node-state":
		        				        		
		        		break;
		        		
	        		default:
	        			
	        	}
	        	break;
	        case "delete":
	        	break;
        	default:
        		data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")
        				+"\", \"status\":\"ERROR\", \"message\":\"Undefined method.\"}";
        }
        
        //JSONObject event = new Event().toJSON();
		log.debug("duration["+Benchmark.diffFromLast("milli")+"ms] "+request.requestMethod() 
				+ " request at " + request.pathInfo() + " Response: " + data);
		//response.redirect("index.html");
        response.body( data );
		
		return response;
	}
	
	private String readLayoutFile() {
		//File graphLayoutFile = topologyManager.getTopologyOutputManager().getTopologyOutput().getOutputFile();
		//File graphLayoutFile = new File(topologyManager.getGraphviz().getOutputFile());
		File graphLayoutFile = new File("bin"+File.separator+Settings.getGraphvizOutputFile());
		
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
	        
	        int nodes = 0;
	        String[] originalLines = fileData.toString().split("\n");
	        String[] newLines = fileData.toString().split("\n");
	        //log.debug("lines: "+originalLines.length);
	        for (int i=0; i<originalLines.length; i++) {
	        	if (originalLines[i].trim().matches(".*pos=\"[0-9.e+-]+,[0-9.e+-]+\".*")) {
	        		nodes++;
	        		String positionLine = originalLines[i];
	        		int indexFirstQuote = positionLine.indexOf('"');
	        		int indexComma = positionLine.indexOf(',');
	        		int indexSecondQuote = positionLine.indexOf('"', indexFirstQuote+1);
	        		
	        		//log.debug(positionLine+" "+indexFirstQuote+" "+indexComma+" "+indexSecondQuote);
	        		
	        		String x = positionLine.substring(indexFirstQuote + 1, indexComma);
	        		String y = positionLine.substring(indexComma + 1, indexSecondQuote);
	        		
	        		//log.debug("x: "+x+", y: "+y);
	        		newLines[i] = positionLine.substring(0, indexSecondQuote) 
	        						+ "\",x=" + x 
	        						+ ",y="+ y 
	        						+ positionLine.substring(indexSecondQuote+1);
	        		//log.debug(newLines[i]);
	        	}
	        }
	        //log.debug("nodes: "+nodes);
	        log.debug("x, y parsing took "+Benchmark.diffFromLast("milli")+"ms");
	        
	        //String fileDataString = fileData.toString()
	        String fileDataString = String.join("", newLines)
	        		.replaceAll("[ ,+]\\\\", "")
	        		.replaceAll("\"", "\\\\\"")
	        		.replaceAll("\n", "")
	        		.replaceAll("\r", "")
	        		.replaceAll("\t", "");
	        /*pos="1.2185e+007,6.0923e+006",*/
	        // calculate x and y from pos - doing so in js can slow things down 
	       /* String[] sections = fileDataString.split("pos=\\\\\"");
	        log.debug("# of records: "+sections.length);
	        Arrays.asList(sections).forEach(section -> {
	        	// 2.0065e+005,2.228e+005\",width=4.2066];
	        	// 2.008e+005,2.2308e+005 2.0077e+005,2.2302e+005 2.0069e+005,2.2287e+005 2.0066e+005,2.2282e+005\"];
	        	if (section.matches("^[0-9.e+-]+,[0-9.e+-]+\\\\\".*")) {
	        		log.debug(section);
	        		nodes++;
	        		int indexComma = section.indexOf(',');
	        		int indexQuote = section.indexOf('\\');
	        		String complete = section.substring(0, indexQuote);
	        		String x = section.substring(0, indexComma - 1);
	        		String y = section.substring(indexComma + 1, indexQuote);
	        		log.debug(indexComma+", "+indexQuote+" "+complete+" |"+x+"|"+y+"|");
	        		finalString.append(section.replace(complete+"\\\"", complete+"\\\",x="+x+",y="+y));
	        	}
	        });
	        log.debug(finalString.toString());
	        log.debug("total nodes: "+nodes);*/
	        //String pattern = "(pos=\"\",)";
	        return "{\"method\":\"ENINetworkGraph/get/graph/layout\", \"status\":\"SUCCESS\", \"message\":\""+fileDataString+"\"}";
		} catch (Exception e) {
			log.debug(e.getMessage(), e);
			return "{\"method\":\"ENINetworkGraph/get/graph/layout\", \"status\":\"ERROR\", \"message\":\"Error reading the graph layout file from server. "+e.getMessage()+"\"}";
		}
    }

	public TopologyManager getTopologyManager() {
		return topologyManager;
	}

	public void setTopologyManager(TopologyManager topologyManager) {
		this.topologyManager = topologyManager;
	}
	
}
