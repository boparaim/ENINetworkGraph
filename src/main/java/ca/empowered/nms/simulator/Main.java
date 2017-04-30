package ca.empowered.nms.simulator;

import java.io.File;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.empowered.nms.simulator.topology.element.Node;
import ca.empowered.nms.simulator.topology.source.TopologySource;
import ca.empowered.nms.simulator.topology.source.TopologySourceManager;
import ca.empowered.nms.simulator.topology.source.file.json.JsonFileTopologySource;

/**
 * Entry point for the application.
 * 
 * @author mboparai
 *
 */
public class Main {

	private static final Logger log = LogManager.getLogger(Main.class.getName());
	
	public static ExecutorService executor;
	
	public static void main(String[] args) {
		
		long startTime = System.nanoTime();
		
		log.info("INIT: ENINetworkSimulator");
				
		try {
			// initialize Spring
			ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
			executor = Executors.newCachedThreadPool(); // like fixed thread pool but uses cached instances and gets rid of dead threads
			
			File file = Paths.get(Main.class.getClass().getResource("/config.json").toURI()).toFile();
			TopologySource topoSource = new JsonFileTopologySource(file);
			TopologySourceManager topoManager = new TopologySourceManager(topoSource);
			MultiValuedMap<Node, Node> ntwkMap = topoManager.process();
			
			executor.shutdown();
			while (!executor.isTerminated()) {
				Thread.sleep(1000);
			}
			
			((ConfigurableApplicationContext)context).close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*
		
		NodeManager.generateNodes();
		log.info("CREATION: took "+((System.nanoTime() - startTime)/1000000)+"(ms) "+((System.nanoTime() - startTime)/1000000000)+"(s)");
		NodeManager.relateNodes();
		NodeManager.exportTopology();
		
		if (Settings.isNoisyEventGeneration()) {
			EventBuilder evenBuilder1 = new NoisyEventGenerator(
					Settings.getNoisyEventGenerationInterval(), 
					NodeManager.getAllNodes(), 
					Settings.getNoisyEventGenerationEvents());
			evenBuilder1.start();
		}

		if (Settings.isRandomizeEventGeneration()) {
			EventBuilder evenBuilder2 = new RandomEventGenerator(
					Settings.getRandomizeEventGenerationInterval(), 
					NodeManager.getAllNodes());
			evenBuilder2.start();
		}
		
		
		*/
		long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;
		log.info("END: took "+timeDiff+"(ns) "+(timeDiff/1000000)+"(ms) or "+(timeDiff/1000000000)+"(s)");
	}

}
