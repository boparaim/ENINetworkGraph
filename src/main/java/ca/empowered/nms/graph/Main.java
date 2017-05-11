package ca.empowered.nms.graph;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.empowered.nms.graph.config.Settings;
import ca.empowered.nms.graph.topology.TopologyManager;
import ca.empowered.nms.graph.utils.Benchmark;

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
		
		//long startTime = System.nanoTime();
		Benchmark.init();
		
		log.info("INIT: ENINetworkSimulator");
				
		try {
			// initialize Spring
			ApplicationContext context = new ClassPathXmlApplicationContext("beans.xml");
			executor = Executors.newCachedThreadPool(); // like fixed thread pool but uses cached instances and gets rid of dead threads
			
			log.debug("initialization took "+Benchmark.diffFromLast("milli")+"ms");
			
			TopologyManager topologyManager = (TopologyManager) context.getBean("topologyManager");
			boolean mapWithLayoutWrittenSuccessfully = false;
			
			if (!Settings.isSourceReadContinuously()) {
				log.debug("reading source for network map");
				mapWithLayoutWrittenSuccessfully = topologyManager.run();
			}
			while (Settings.isSourceReadContinuously()) {
				log.debug("reading source for network map");
				mapWithLayoutWrittenSuccessfully = topologyManager.run();
				
				Thread.sleep(Settings.getSourceReadInterval());
			}
			
			// got final output
			if (mapWithLayoutWrittenSuccessfully) {
				
			}
			
			executor.shutdown();
			while (!executor.isTerminated()) {
				Thread.sleep(1000);
			}
			
			((ConfigurableApplicationContext)context).close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		/*long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;
		log.info("END: took "+timeDiff+"(ns) "+(timeDiff/1000000)+"(ms) or "+(timeDiff/1000000000)+"(s)");*/
	}

}
