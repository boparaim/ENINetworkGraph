package ca.empowered.nms.graph.api.rest;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.config.Settings;
import spark.Request;
import spark.Response;

/**
 * This entity is responsible for managing all web service calls.
 * 
 * @author mboparai
 *
 */
public class RestServer {

	private static final Logger log = LogManager.getLogger(RestServer.class.getName());
	
	/**
	 * RestServer - add all managed paths.
	 */
	public RestServer() {
		log.debug("INIT -> listening for rest requests on http://" 
				+ Settings.getRestServerIP() + ":" + Settings.getRestServerPort()
				+Settings.getRestServerPath());
		
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
		
		get(Settings.getRestServerPath()+"/get/nodes", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/node/:name", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/related-nodes/:name", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/all-underlying-nodes/:name", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/get/notifications", (request, response) -> {
            return processWebRequest(request, response).body();
        });
		
		get(Settings.getRestServerPath()+"/set/node-state/:name/:state", (request, response) -> {
			return processWebRequest(request, response).body();
		});
		
		get(Settings.getRestServerPath()+"/set/all-underlying-nodes-state/:name/:state", (request, response) -> {
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
        		data = "{\"method\":\""+Arrays.toString(path).replaceAll(", ", ".")+"\", \"status\":\"ERROR\", \"message\":\"Undefined method.\"}";
        }
        
        //JSONObject event = new Event().toJSON();
		log.debug(request.requestMethod() + " request at " + request.pathInfo() + " Response: " + data);
        response.body( data );
		
		return response;
	}
	
}
