package ca.empowered.nms.simulator.api;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.NodeFactory;
import org.graphstream.graph.implementations.AbstractGraph;
import org.graphstream.graph.implementations.AdjacencyListGraph;
import org.graphstream.ui.graphicGraph.GraphicElement;
import org.graphstream.ui.swingViewer.DefaultView;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.Viewer.CloseFramePolicy;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.event.EventObserver;
import ca.empowered.nms.simulator.event.Notification;
import ca.empowered.nms.simulator.event.NotificationFactory;
import ca.empowered.nms.simulator.node.NodeElement;
import ca.empowered.nms.simulator.topology.source.file.json.NodeTemplate;
import ca.empowered.nms.simulator.utils.Constants.FILE_FORMAT;
import ca.empowered.nms.simulator.utils.Constants.STATE;

/**
 * This entity is responsible for reading templates, creating node instances from these templates
 * and then interrelating them.
 * 
 * @author mboparai
 *
 */
public final class NodeManager {
	
	private static final Logger log = LogManager.getLogger(NodeManager.class.getName());
	
	/** observer that detects state changes on a node */
	private static EventObserver eventObserver;
	/** node templates read from the configuration file (json) */
	private static ArrayList<NodeTemplate> nodeTemplates = new ArrayList<NodeTemplate>();
	/** graph object from GraphStream */
	private static Graph graph;
	/** initialization flag */
	private static boolean usable = false;
	/** topology information */
	private static String topologyText = "";
	/** topology information format */
	private static FILE_FORMAT topologyFormat;
	/** generated nodes */
	private static ArrayList<String> allNodes = new ArrayList<>();
	/** nodes that are not connected to any other node */
	private static ArrayList<String> disconnectedNodes = new ArrayList<>();
	/** number of nodes connected to a node with lower rank */
	private static int underlyingNodeCount = 0;
	
	/**
	 * This method must be called for properly setting up the NodeManger
	 */
	public static void init() {
		// create graph
		graph = new AdjacencyListGraph(Settings.getAppName());  // SingleGraph or MultiGraph() // less memory/faster - AdjacencyListGraph // thread-safe - ConcurrentGraph
		graph.setStrict(true);
		graph.setAutoCreate( false );
		graph.addAttribute("ui.title", Settings.getAppName());
		
		// improve visual quality
		if (Settings.isUiAntiAlias())
			graph.addAttribute("ui.quality");
		if (Settings.isUiQuality())
			graph.addAttribute("ui.antialias");
		
		topologyFormat = FILE_FORMAT.valueOf(Settings.getTopologyFormat().toUpperCase());
		
		// css stylesheet
		graph.addAttribute("ui.stylesheet", "url('"+Settings.getCssStyleSheet()+"')");
		
		// use NodeElement instead of default Node class
		graph.setNodeFactory(new NodeFactory<NodeElement>() {
			public NodeElement newInstance(String id, Graph graph) {
				return new NodeElement((AbstractGraph) graph, id);
			}
		});
		
		// graph -> viewer -> view
		//				   -> renderer -> camera

		// show GUI ?
		if (Settings.isDisplayGUI()) {
			Viewer viewer = graph.display();
			DefaultView view = ((DefaultView)viewer.getView(Viewer.DEFAULT_VIEW_ID));
			view.getCamera().setViewCenter(0, 0, 0);

			if (!Settings.isGuiClosesApp()) {
				viewer.setCloseFramePolicy(CloseFramePolicy.CLOSE_VIEWER);
			}
			
			// show connected node names when clicked on
			view.addMouseListener(new MouseListener() {
				@Override public void mouseReleased(MouseEvent e) {}
				@Override public void mousePressed(MouseEvent e) {}				
				@Override public void mouseExited(MouseEvent e) {}				
				@Override public void mouseEntered(MouseEvent e) {}
				@Override public void mouseClicked(MouseEvent e) {
					//view.moveElementAtPx(element, event.getX(), event.getY());
					GraphicElement curElement = view.findNodeOrSpriteAt(e.getX(), e.getY());
					if (curElement == null)
						return;
					
					NodeElement node = graph.getNode(curElement.getLabel());
					String nodes = node.getId();
					
					for (Edge edge : node.getEdgeSet()) {
						NodeElement otherNode = edge.getOpposite(node);
						nodes += "\n  -  "+otherNode.getId();
					}
					if (Settings.isUiShowRelatedNodesOnClick())
						JOptionPane.showMessageDialog(null, nodes);
					log.debug("\n"+nodes);
				}
			});
			
			view.addMouseMotionListener(new MouseMotionListener() {				
				@Override public void mouseMoved(MouseEvent e) {
					JOptionPane.getRootFrame().dispose();
				}
				@Override public void mouseDragged(MouseEvent e) {}
			});
			
			// zoom with mouse
			view.addMouseWheelListener(new MouseWheelListener() {				
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					//log.debug("mouse wheel event: "+e.getWheelRotation()+" "+e.getScrollAmount());
					if (!Settings.isUiZoomWithMouse())
						return;
					
					// zoom 1x to nx
					double currentViewPercent = view.getCamera().getViewPercent();
					double newViewPercent = currentViewPercent;
					double temp = currentViewPercent + (e.getWheelRotation() * Settings.getUiZoomFactor());
					if ( temp > 0 && temp < 2 ) {
						newViewPercent = temp;
						//log.debug("current: "+currentViewPercent+" new: "+newViewPercent);
					}
					view.getCamera().setViewPercent(newViewPercent);
					
					// change camera view center
					double width = view.getSize().getWidth();
					double height = view.getSize().getHeight();
					double xCenter = width / 2;
					double yCenter = height  / 2;
					double x = e.getX();						
					double y = e.getY();
					double xFromCenter = x - xCenter;						
					double yFromCenter = -y + yCenter;
					
					double cameraWidth = view.getCamera().getMetrics().getSize().x();
					double cameraHeight = view.getCamera().getMetrics().getSize().y();
					double cameraRatioX = width / cameraWidth;
					double cameraRatioY = width / cameraHeight;
					/*log.debug(width+","+height);
					log.debug(Arrays.toString(view.getCamera().getMetrics().viewport));
					log.debug(xCenter+" x "+yCenter);
					log.debug(x+","+y);
					log.debug(xFromCenter+","+yFromCenter);
					log.debug(cameraWidth+","+cameraHeight);
					log.debug(cameraRatioX+","+cameraRatioY);*/
					
					view.getCamera().setViewCenter(xFromCenter/cameraRatioX, yFromCenter/cameraRatioY, 0);
				}
			});
		}
		
		usable = true;
	}
	
	/**
	 * Create nodes if template is enabled.
	 */
	public static void generateNodes() {
		if (!usable || graph == null) {
			log.error("NodeFactory is not initialized properly. Call init() before using it.");
			return;
		}

		// for a large data set, use multiple threads
		//if (allNodes.size() > 5000) {
			nodeTemplates
				.parallelStream()
				.forEach(nodeTemplate -> {
					log.debug(" -> "+nodeTemplate.toString());
					if ( !nodeTemplate.getEnabled() ) {
						log.info(nodeTemplate.getName()+": this template is disabled");
						return;
					}
					// TODO: divide this work in worker threads
					for ( int i = 0; i < nodeTemplate.getCount(); i++ ) {
						addNode(i,
								nodeTemplate.getName(), 
								nodeTemplate.getDescription(), 
								nodeTemplate.getInitialState(), 
								nodeTemplate.getRank(), 
								nodeTemplate.getRelatableTo());
					}
				});
		/*}
		// for a small data set, use single thread
		else {
			for ( NodeTemplate nodeTemplate : nodeTemplates ) {
				log.debug(" -> "+nodeTemplate.toString());
				if ( !nodeTemplate.getEnabled() ) {
					log.info(nodeTemplate.getName()+": this template is disabled");
					continue;
				}
				for ( int i = 0; i < nodeTemplate.getCount(); i++ ) {
					addNode(i,
							nodeTemplate.getName(), 
							nodeTemplate.getDescription(), 
							nodeTemplate.getInitialState(), 
							nodeTemplate.getRank(), 
							nodeTemplate.getRelatableTo());
				}
			}
		}*/
		
		log.info("generated objects: " + graph.getNodeCount());
		
	}
	
	/**
	 * Create a node and add it to graph.
	 * 
	 * @param id
	 * @param className
	 * @param description
	 * @param initialState
	 * @param rank
	 * @param relatableTo
	 */
	public static void addNode(int id, String className, String description, STATE initialState, int rank, HashMap<String, Integer> relatableTo) {
		if (!usable || graph == null) {
			log.error("NodeFactory is not initialized properly. Call init() before using it.");
			return;
		}
		
		String instanceName = className + "-" + (id + 1000) + Settings.getNodeNameSuffix();
		log.debug("instance name: "+instanceName);		
		graph.addNode(instanceName);
		
		NodeElement node = graph.getNode(instanceName);
		node.addAttribute("description", description);
		node.addAttribute("state", initialState.toString());
		node.addAttribute("rank", rank);
		node.addAttribute("ui.label", instanceName);
		node.addAttribute("ui.class", className);
		// for css property manipulation
		node.addAttribute("class", className);
		
		// node types this node can connect to
		for (String key: relatableTo.keySet()) {
			node.addAttribute("rel"+key, relatableTo.get(key));
		}
		
		node.addObserver(eventObserver);
		
		allNodes.add(node.getId());
		disconnectedNodes.add(node.getId());
		//try { Thread.sleep(20); } catch (Exception e) {}
	}
	
	/**
	 * Connect nodes.
	 */
	public static void relateNodes() {
		if (!usable || graph == null) {
			log.error("NodeFactory is not initialized properly. Call init() before using it.");
			return;
		}
		
		// for a large data set, use multiple threads
		if (allNodes.size() > 5000) {
			graph.getNodeSet()
				.parallelStream()
				.forEach(thisNode -> {
					//log.info("1 processing: "+Thread.currentThread().getName()+" -> "+thisNode.getId());
					
					graph.getNodeSet()
					.parallelStream()
					.forEach(otherNode -> {
						connectTwoNodes(thisNode, otherNode);
					});
				});
		}
		// for a small data set, use single thread
		else {
			for (Node thisNode : graph.getNodeSet()) {
				for (Node otherNode : graph.getNodeSet()) {
					connectTwoNodes(thisNode, otherNode);
				}
			}
		}		
		
		log.info("disconnected objects: " + disconnectedNodes.size());
		for (String nodeName : disconnectedNodes) {
			log.debug("x\t "+nodeName);
		}
	}
	
	/**
	 * Connect two nodes. This is the bottleneck of the application. 
	 * TODO: Improve the iteration duration.
	 * 
	 * @param nodeA
	 * @param nodeB
	 */
	public static void connectTwoNodes(Node nodeA, Node nodeB) {
		NodeElement thisNodeElement = (NodeElement)nodeA;
		NodeElement otherNodeElement = (NodeElement)nodeB;
		
		if ( thisNodeElement.isRelatableTo(otherNodeElement) ) {
			graph.addEdge(thisNodeElement.getId()+"."+otherNodeElement.getId(), thisNodeElement.getId(), otherNodeElement.getId());
			
			log.debug("connected: node1: " + thisNodeElement.getId() + " node2: " + otherNodeElement.getId());
			synchronized (disconnectedNodes) {
				if (disconnectedNodes.contains(thisNodeElement.getId())) {
					disconnectedNodes.remove(thisNodeElement.getId());
				}
				if (disconnectedNodes.contains(otherNodeElement.getId())) {
					disconnectedNodes.remove(otherNodeElement.getId());
				}
			}
			
			// implement a single line in topology export file
			if (topologyFormat.equals(FILE_FORMAT.TXT))
				topologyText += thisNodeElement.getId()+"\t"+otherNodeElement.getId()+"\t1\n";
		} else {
			//log.debug("invalid relationship: node1: " + thisNode.getId() + " node2: " + otherNode.getId());
		}
	}
	
	/**
	 * Export topology to a file.
	 */
	public static void exportTopology() {
		if (!Settings.isExportTopology())
			return;
		
		PrintWriter writer = null;
		try {				
			String fileName = "topology." + topologyFormat.toString().toLowerCase();
			
			writer = new PrintWriter(fileName, "UTF-8");
			writer.println(topologyText);
			
			log.info("exported topology information to "+fileName);
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			log.debug(e.getMessage(), e);
		} finally {
			if (writer != null)
				writer.close();
		}
	}
	
	/**
	 * Get help text. List available methods.
	 * 
	 * @return
	 */
	public static String getHelp() {
		return "Available paths: \n"
				+ "/get/help\t\t shows this help\n"
				+ "/get/nodes\t\t lists all nodes\n"
				+ "/get/node/<node-name>\t\t shows the given node\n"
				+ "/get/related-nodes/<node-name>\t\t lists nodes given nodes are related to\n"
				+ "/get/all-underlying-nodes/<node-name>\t\t lists nodes given nodes are related including nodes below nodes\n"
				+ "/get/notifications\t\t lists all notifications\n"
				+ "/set/node-state/<node-name>/<state>\t\t updates state for the given node\n"
				+ "/set/all-underlying-nodes-state/<node-name>/<state>\n";
	}
	
	/**
	 * Get list of all nodes and their current states.
	 * 
	 * @return
	 */
	public static String getNodes() {
		String data = "{}";
		try {
			data = "[";
			for (String name : allNodes) {
				data += "{\"name\":\""+name+"\", \"state\":\""+graph.getNode(name).getAttribute("state")+"\"},";
			}
	
			StringBuffer buffer = new StringBuffer(data);
			data = buffer.reverse().toString().replaceFirst(",", "");
			data = new StringBuffer(data).reverse().toString();
	
			data += "]";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return data;
	}
	
	/**
	 * Get current state for the given node.
	 * 
	 * @param nodeName
	 * @param pathText
	 * @return
	 */
	public static String getNode(String nodeName, String pathText) {
		String data = "{}";
		try {
    		if (nodeName == null || nodeName.isEmpty())
    			data = "{\"method\":\""+pathText
    					+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
    		else {
    			if ( !allNodes.contains(nodeName) )
    				data = "{\"method\":\""+pathText
    						+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
    			else {
    				data = "{\"name\":\""+nodeName+"\", \"state\":\""
    						+NodeManager.getGraph().getNode(nodeName).getAttribute("state")+"\"}";
    			}
    		}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return data;
	}
	
	/**
	 * Get list of nodes related to the given node and their current states.
	 * 
	 * @param nodeName
	 * @param pathText
	 * @return
	 */
	public static String getRelatedNodes(String nodeName, String pathText) {
		String data = "{}";
		try {
			if (nodeName == null || nodeName.isEmpty())
    			data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
    		else {
    			if ( !allNodes.contains(nodeName) )
    				data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
    			else {
    				NodeElement requestedNode = NodeManager.getGraph().getNode(nodeName);
    				data = "{\"count\":\""+requestedNode.getEdgeSet().size()+"\", \"nodes\":[";		        				
    				for (Edge edge : requestedNode.getEdgeSet()) {
    					data += "\"" + edge.getOpposite(requestedNode).getId() + "\",";
    				}

    				StringBuffer buffer = new StringBuffer(data);
					data = buffer.reverse().toString().replaceFirst(",", "");
					data = new StringBuffer(data).reverse().toString();
					
    				data += "]}";
    			}
    		}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return data;
	}
	
	/**
	 * Get all nodes that are connected to the given node and have rank lower than this node and their current states.
	 * 
	 * @param nodeName
	 * @param pathText
	 * @return
	 */
	public static String getAllUnderlyingNodes(String nodeName, String pathText) {
		String data = "{}";
		try {
			if (nodeName == null || nodeName.isEmpty())
    			data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
    		else {
    			if ( !allNodes.contains(nodeName) )
    				data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
    			else {
    				underlyingNodeCount = 0;
    				NodeElement requestedNode = NodeManager.getGraph().getNode(nodeName);
    				data = "{\"count\":\"UNDERLYING_NODE_COUNT\", \"nodes\":[";
    				data = getAllUnderlyingNodes(requestedNode, data);

    				StringBuffer buffer = new StringBuffer(data);
					data = buffer.reverse().toString().replaceFirst(",", "");
					data = new StringBuffer(data).reverse().toString();
					
    				data += "]}";
    				data = data.replace("UNDERLYING_NODE_COUNT", String.valueOf(underlyingNodeCount));
    			}
    		}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return data;
	}
	
	public static String getAllUnderlyingNodes(NodeElement node, String data) {
		for (Edge edge : node.getEdgeSet()) {
			NodeElement otherNode = edge.getOpposite(node);
			if ( Integer.parseInt(otherNode.getAttribute("rank").toString()) < Integer.parseInt(node.getAttribute("rank").toString()) ) {
				data += "\"" + otherNode.getId() + "\",";
				underlyingNodeCount++;
				
				data = getAllUnderlyingNodes(otherNode, data);
			}
		}
		
		return data;
	}
	
	/**
	 * Get all notification objects.
	 * 
	 * @return
	 */
	public static String getNotifications() {
		String data = "{}";
		try {
			HashMap<String, Notification> allNotifications = NotificationFactory.getAllNotifications();
    		data = "[";
    		for (Notification notification : allNotifications.values()) {
    			data += notification.toJSON()+",";
    		}

    		StringBuffer buffer = new StringBuffer(data);
			data = buffer.reverse().toString().replaceFirst(",", "");
			data = new StringBuffer(data).reverse().toString();

    		data += "]";
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return data;
	}
	
	/**
	 * Set state for the given node.
	 * 
	 * @param nodeName
	 * @param newState
	 * @param pathText
	 * @return
	 */
	public static String setNodeState(String nodeName, String newState, String pathText) {
		String data = "{}";
		try {
			if (nodeName == null || nodeName.isEmpty() || newState == null || newState.isEmpty())
    			data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
    		else {
    			allNodes = NodeManager.getAllNodes();
    			
    			if ( !allNodes.contains(nodeName) )
    				data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
    			else if ( !newState.equalsIgnoreCase("up")
    					&& !newState.equalsIgnoreCase("down")
    					&& !newState.equalsIgnoreCase("degraded"))
    				data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":"
    						+ "\"Invalid state. Valid values for state are UP|DOWN|DEGRADED\"}";
    			else {
    				NodeElement requestedNode = NodeManager.getGraph().getNode(nodeName);
    				if ( newState.equalsIgnoreCase("up") )
    					requestedNode.setCurrentState(STATE.UP);
    				else if ( newState.equalsIgnoreCase("down") )
    					requestedNode.setCurrentState(STATE.DOWN);
    				else if ( newState.equalsIgnoreCase("degraded") )
    					requestedNode.setCurrentState(STATE.DEGRADED);
    				data = "{\"method\":\""+pathText+"\", \"status\":\"SUCCESS\", \"message\":\"State for "+requestedNode.getId()
    						+" updated to "+requestedNode.getCurrentState().toString()+"\"}";
    			}
    		}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return data;
	}

	/**
	 * Set state for all nodes that are connected to the given node and have lower rank.
	 * 
	 * @param nodeName
	 * @param newState
	 * @param pathText
	 * @return
	 */
	public static String setAllUnderlyingNodesState(String nodeName, String newState, String pathText) {
		String data = "{}";
		try {
			if (nodeName == null || nodeName.isEmpty() || newState == null || newState.isEmpty())
    			data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"This method requires \"name\" of a node.\"}";
    		else {
    			allNodes = NodeManager.getAllNodes();
    			
    			if ( !allNodes.contains(nodeName) )
    				data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"No node found with given name - "+nodeName+"\"}";
    			else if ( !newState.equalsIgnoreCase("up")
    					&& !newState.equalsIgnoreCase("down")
    					&& !newState.equalsIgnoreCase("degraded"))
    				data = "{\"method\":\""+pathText+"\", \"status\":\"ERROR\", \"message\":\"Invalid state. "
    						+ "Valid values for state are UP|DOWN|DEGRADED\"}";
    			else {
    				NodeElement requestedNode = NodeManager.getGraph().getNode(nodeName);
    				data = "{\"method\":\""+pathText+"\", \"status\":\"SUCCESS\", \"message\":\"States updated\", \"nodes\":[";		        				

    				if ( newState.equalsIgnoreCase("up") )
    					requestedNode.setCurrentState(STATE.UP);
    				else if ( newState.equalsIgnoreCase("down") )
    					requestedNode.setCurrentState(STATE.DOWN);
    				else if ( newState.equalsIgnoreCase("degraded") )
    					requestedNode.setCurrentState(STATE.DEGRADED);
    				
    				data += "{\"name\":\"" + requestedNode.getId() + "\", \"state\":\""+requestedNode.getCurrentState().toString()+"\"},";		        				
    				data = setStateForAllUnderlyingNodes(requestedNode, newState, data);

    				StringBuffer buffer = new StringBuffer(data);
					data = buffer.reverse().toString().replaceFirst(",", "");
					data = new StringBuffer(data).reverse().toString();
					
    				data += "]}";
    			}
    		}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		return data;
	}
	
	public static String setStateForAllUnderlyingNodes(NodeElement node, String newState, String data) {
		for (Edge edge : node.getEdgeSet()) {
			NodeElement otherNode = edge.getOpposite(node);
			if ( Integer.parseInt(otherNode.getAttribute("rank").toString()) < Integer.parseInt(node.getAttribute("rank").toString()) ) {
				if ( newState.equalsIgnoreCase("up") )
					otherNode.setCurrentState(STATE.UP);
				// DOWN and DEGRADED gets propagated automatically
				else if ( newState.equalsIgnoreCase("down") )
					otherNode.setCurrentState(STATE.DOWN);
				else if ( newState.equalsIgnoreCase("degraded") )
					otherNode.setCurrentState(STATE.DEGRADED);
				
				data += "{\"name\":\"" + otherNode.getId() + "\", \"state\":\""+otherNode.getCurrentState().toString()+"\"},";
			
				log.debug("===== "+node.getAttribute("rank").toString()+" other "+otherNode.getAttribute("rank").toString());
				data = setStateForAllUnderlyingNodes(otherNode, newState, data);
			}
		}
		
		return data;
	}

	public static EventObserver getEventObserver() {
		return eventObserver;
	}

	public static void setEventObserver(EventObserver eventObserver) {
		NodeManager.eventObserver = eventObserver;
	}

	public static ArrayList<String> getAllNodes() {
		return allNodes;
	}

	public ArrayList<NodeTemplate> getNodeTemplates() {
		return nodeTemplates;
	}

	public void setNodeTemplates(ArrayList<NodeTemplate> nodeTemplates) {
		NodeManager.nodeTemplates = nodeTemplates;
	}

	public static Graph getGraph() {
		return graph;
	}
}
