package ca.empowered.nms.simulator.topology.source.file.json;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import ca.empowered.nms.simulator.config.Settings;
import ca.empowered.nms.simulator.topology.element.Node;
import ca.empowered.nms.simulator.topology.source.TopologySource;
import ca.empowered.nms.simulator.utils.Constants.STATE;
import scala.Array;

/**
 * This entity reads json configuration file and builds node templates from it.
 * 
 * @author mboparai
 *
 */
public class JsonFileTopologySource extends TopologySource {

	private static final Logger log = LogManager.getLogger(JsonFileTopologySource.class.getName());
	
	/** json object mapper */
	private ObjectMapper objectMapper;
	/** json root node */
	private JsonNode jsonRootNode;
	/** node templates */
	private ArrayList<NodeTemplate> nodeTemplates = new ArrayList<NodeTemplate>();
	
	
	/**
	 * Read the file and create node templates.
	 * 
	 * @param file
	 */
	public JsonFileTopologySource(File configurationFile) {
		super(configurationFile);
		try {
			objectMapper = new ObjectMapper();
			objectMapper.configure(JsonParser.Feature.ALLOW_YAML_COMMENTS, true);
			//InputStream inputstream = this.getClass().getClassLoader().getResourceAsStream(configurationFile);
			InputStream inputstream = new FileInputStream(configurationFile);
			if ( inputstream != null ) {
				log.debug("reading json config file");
				jsonRootNode = objectMapper.readTree(inputstream);
			}
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
	}
	
	/**
	 * Create node template objects.
	 */
	@Override
	public void parseConfigurationFile(File confFile) {
		if (jsonRootNode == null) {
			log.error("Unable to read json configuration file. Can't create nodes.");
			return;
		}
		
		JsonNode nodes = jsonRootNode.path("nodes");
		for ( JsonNode node : nodes ) {
			log.debug(node.toString());
			
			NodeTemplate nodeTemplate = new NodeTemplate();
			nodeTemplate.setName(node.path("name").textValue());
			nodeTemplate.setDescription(node.path("description").textValue());
			nodeTemplate.setCount(node.path("count").asInt(0));
			nodeTemplate.setEnabled(node.path("enabled").asBoolean(false));
			if ( node.path("init-state").textValue().equalsIgnoreCase("down") )
				nodeTemplate.setInitialState(STATE.DOWN);
			else if ( node.path("init-state").textValue().equalsIgnoreCase("degraded") )
				nodeTemplate.setInitialState(STATE.DEGRADED);
			else if ( node.path("init-state").textValue().equalsIgnoreCase("up") )
				nodeTemplate.setInitialState(STATE.UP);
			else
				nodeTemplate.setInitialState(STATE.UNKNOWN);
			nodeTemplate.setRank(node.path("rank").asInt(0));
			
			JsonNode relatableTos = node.path("relatable-to");
			for ( JsonNode relatableTo : relatableTos ) {
				String name = relatableTo.path("name").textValue();
				Integer max = relatableTo.path("max").asInt(0);
				nodeTemplate.getRelatableTo().put(name, max);
			}
			
			nodeTemplates.add(nodeTemplate);
		}
	}

	/**
	 * Get node templates.
	 * 
	 * @return
	 */
	@Override
	public MultiValuedMap<Node, Node> getNetworkMap() {
		
		
		//return nodeTemplates;
		return networkMap;
	}
	
	public void addNodes(int start, int count, String className, String description, STATE initialState, int rank,  HashMap<String, Integer> relatableTo) {
		//Callable<Boolean> task = () -> {
			//log.debug(Thread.currentThread().getName()+": creating nodes from "+start+" to "+(start + count - 1)+" for "+className);
			for (int i = start; i < (start + count); i++) {
				String instanceName = className + "-" + (i + 1000) + Settings.getNodeNameSuffix();
				//log.debug("creating node - "+instanceName);
				Node node = new Node();
				node.setClassName(className);
				node.setName(instanceName);
				node.setDescription(description);
				node.setInitialState(initialState);
				node.setRank(rank);
				node.setRelatableTo(relatableTo);
				nodes.add(node);
			}
			/*return true;
		};
		Main.executor.submit(task);*/
	}
	
	public void connectTwoNodes(Node nodeA, Node nodeB) {
		if ( nodeA.isRelatableTo(nodeB) ) {
			networkMap.put(nodeA, nodeB);
			
			//log.debug("connected: node1: " + nodeA.getName() + " node2: " + nodeB.getName());
			/*synchronized (disconnectedNodes) {
				if (disconnectedNodes.contains(thisNodeElement.getId())) {
					disconnectedNodes.remove(thisNodeElement.getId());
				}
				if (disconnectedNodes.contains(otherNodeElement.getId())) {
					disconnectedNodes.remove(otherNodeElement.getId());
				}
			}
			
			// implement a single line in topology export file
			if (topologyFormat.equals(FILE_FORMAT.TXT))
				topologyText += thisNodeElement.getId()+"\t"+otherNodeElement.getId()+"\t1\n";*/
		} else {
			//log.debug("invalid relationship: node1: " + thisNode.getId() + " node2: " + otherNode.getId());
		}
	}

	@Override
	public void processConfigurationFile() {
		// 70k nodes = 2000ms
		// 700k nodes = 2000ms
		// 7M nodes = 8200ms
		/*nodeTemplates
			.parallelStream()
			.forEach(nodeTemplate -> {
				log.debug(" -> "+nodeTemplate.toString());
				if ( !nodeTemplate.getEnabled() ) {
					log.info(nodeTemplate.getName()+": this template is disabled");
					return;
				}
				
				int setSize = nodeTemplate.getCount();
				while (setSize > 0) {
					//log.debug("creating "+nodeTemplate.getCount()+" nodes for "+nodeTemplate.getName());
					if (setSize < 1000) {
						addNodes(1, setSize, 
								nodeTemplate.getName(), 
								nodeTemplate.getDescription(), 
								nodeTemplate.getInitialState(), 
								nodeTemplate.getRank(), 
								nodeTemplate.getRelatableTo());
						break;
					}
					
					addNodes((setSize - 1000), 1000, 
							nodeTemplate.getName(), 
							nodeTemplate.getDescription(), 
							nodeTemplate.getInitialState(), 
							nodeTemplate.getRank(), 
							nodeTemplate.getRelatableTo());
					setSize = setSize - 1000;
				}
			});*/
		
		// 70k nodes = 1700ms
		// 700k nodes = 2000ms
		// 7M nodes = 7400ms
		for ( NodeTemplate nodeTemplate : nodeTemplates ) {
			log.debug(" -> "+nodeTemplate.toString());
			if ( !nodeTemplate.getEnabled() ) {
				log.info(nodeTemplate.getName()+": this template is disabled");
				continue;
			}
			addNodes(0, nodeTemplate.getCount(),
					nodeTemplate.getName(), 
					nodeTemplate.getDescription(), 
					nodeTemplate.getInitialState(), 
					nodeTemplate.getRank(), 
					nodeTemplate.getRelatableTo());
		}
		
		log.debug("done with creation - "+nodes.size());
		
		// 7k nodes = 2000ms
		// 70k nodes = 75s
		// 700k nodes = 
		// 7M nodes = 
		/*nodes
			.parallelStream()
			.forEach(thisNode -> {
				//log.info("1 processing: "+Thread.currentThread().getName()+" -> "+thisNode.getId());
				nodes
					.parallelStream()
					.forEach(otherNode -> {
						connectTwoNodes(thisNode, otherNode);
					});
			});*/
		
		// 7k nodes = 900ms
		// 70k nodes = 45s
		// 700k nodes = 1hr
		// 7M nodes = 
		nodes
			.parallelStream()
			.forEach(thisNode -> {
				//log.info("1 processing: "+Thread.currentThread().getName()+" -> "+thisNode.getId());
				for (Node otherNode : nodes) {
					connectTwoNodes(thisNode, otherNode);
				}
			});

		// 7k nodes = 4400ms
		// 70k nodes = >3mins
		// 700k nodes = 
		// 7M nodes = 
		/*for (Node thisNode : nodes) {
			for (Node otherNode : nodes) {
				connectTwoNodes(thisNode, otherNode);
			}
		}*/
		log.debug("entries in map: "+networkMap.size());
		
		for (Node key : networkMap.keySet()) {
			for (Node list : networkMap.get(key)) {
				log.debug(key.getName()+" -> "+list.getName());
			}
		}
	}
	
}
