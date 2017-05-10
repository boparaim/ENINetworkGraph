package ca.empowered.nms.graph.api.rest;

import static spark.Spark.delete;
import static spark.Spark.get;
import static spark.Spark.post;
import static spark.Spark.put;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.api.NodeManager;
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
        String pathText = Arrays.toString(path).replaceAll(", ", ".");
        
        //ArrayList<String> allNodes = null;
        //HashMap<String, Notification> allNotifications = null;
		String nodeName = null;
		String newState = null;
		//StringBuffer buffer = null;
        
        switch (path[1]) {
	        case "get":
	        	switch (path[2]) {
		        	case "help":
		        		data = NodeManager.getHelp();
		        		break;
		        		
		        	case "nodes":
		        		data = NodeManager.getNodes();
		        		break;
		        		
		        	case "node":
		        		nodeName = request.params(":name");
		        		data = NodeManager.getNode(nodeName, pathText);
		        		break;
		        		
		        	case "related-nodes":
		        		nodeName = request.params(":name");
		        		data = NodeManager.getRelatedNodes(nodeName, pathText);
		        		break;
		        		
		        	case "all-underlying-nodes":
		        		nodeName = request.params(":name");
		        		data = NodeManager.getAllUnderlyingNodes(nodeName, pathText);
		        		break;
		        		
		        	case "notifications":
		        		data = NodeManager.getNotifications();
		        		break;
		        		
		        	default:
		        		
	        	}
	        	break;
	        case "post":
	        	break;
	        case "set":
	        	switch (path[2]) {
		        	case "node-state":
		        		nodeName = request.params(":name");
		        		newState = request.params(":state");
		        		data = NodeManager.setNodeState(nodeName, newState, pathText);
		        		break;
		        		
		        	case "all-underlying-nodes-state":
		        		nodeName = request.params(":name");
		        		newState = request.params(":state");
		        		data = NodeManager.setAllUnderlyingNodesState(nodeName, newState, pathText);		        		
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
	
	public static void sendJSON(String jsonPayload) {
				
		// TODO: this will cause an issue when # of objects goes above few Ks, use executor instead
		new Thread() {
			@Override
			public void run() {
				super.run();
				try {
					URL url = new URL(Settings.getRestDestinationURL());
			
					HttpURLConnection con = (HttpURLConnection) url.openConnection();
					con.setDoOutput(true);
					con.setDoInput(true);
					con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
					con.setRequestProperty("Accept", "application/json");
					con.setRequestMethod("POST");
			
					log.debug("sending HTTP request with JSON : "+jsonPayload);
			
					OutputStreamWriter wr = new OutputStreamWriter(con.getOutputStream());
					wr.write(jsonPayload);
					wr.flush();
					
					//display what returns the POST request	
					StringBuilder sb = new StringBuilder();  
					int HttpResult = con.getResponseCode(); 
					if (HttpResult == HttpURLConnection.HTTP_OK) {
					    BufferedReader br = new BufferedReader(
					            new InputStreamReader(con.getInputStream(), "utf-8"));
					    String line = null;  
					    while ((line = br.readLine()) != null) {  
					        sb.append(line + "\n");  
					    }
					    br.close();
					    log.debug("received response: " + sb.toString());  
					} else {
						log.warn(con.getContent());
						log.warn(con.getResponseMessage());  
						log.warn(con.toString());
					}
				} catch (Exception e) {
					log.debug(e.getMessage(), e);
				}
			}
		}.start();		
	}
}
