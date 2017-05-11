package ca.empowered.nms.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import ca.empowered.nms.graph.topology.element.Node;
import ca.empowered.nms.graph.topology.output.TopologyOutput;
import ca.empowered.nms.graph.topology.output.TopologyOutputManager;
import ca.empowered.nms.graph.topology.output.file.gv.GVFileTopologyOutput;
import ca.empowered.nms.graph.topology.source.TopologySource;
import ca.empowered.nms.graph.topology.source.TopologySourceManager;
import ca.empowered.nms.graph.topology.source.file.json.JsonFileTopologySource;
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
			
			TopologySourceManager topologySourceManager = (TopologySourceManager) context.getBean("topologySourceManager");
			
			/*File file = Paths.get(Main.class.getClass().getResource("/config.json").toURI()).toFile();
			TopologySource topoSource = new JsonFileTopologySource(file);
			TopologySourceManager topoManager = new TopologySourceManager(topoSource);*/
			MultiValuedMap<Node, Node> networkMap = topologySourceManager.process();

			File ofile = Paths.get(Main.class.getClass().getResource("/out.gv").toURI()).toFile();
			TopologyOutput topologyOutput = new GVFileTopologyOutput(networkMap, ofile);
			TopologyOutputManager topoOManger = new TopologyOutputManager(topologyOutput);
			boolean success = topoOManger.process();

			log.debug("written "+success+" "+Benchmark.diffFromLast("milli")+"ms");
			
			if (success) {
				Process process = new ProcessBuilder(new String[]{
						"C:\\Users\\mboparai\\Downloads\\graphviz-2.38\\bin\\dot.exe",
						//"-Tjpeg", 
						"-Tdot",
						"bin\\out.gv", 
						"-obin\\out.f.gv"
					}).start();
				InputStream stdout = process.getInputStream();
				InputStream stderr = process .getErrorStream();
				//OutputStream stdin = process.getOutputStream();

				String line = "";
				BufferedReader br = new BufferedReader(new InputStreamReader(stdout, "utf8"));
				while ((line = br.readLine()) != null) {
					log.debug("stdout: "+line);
					success = false;
				}
				br = new BufferedReader(new InputStreamReader(stderr, "utf8"));
				while ((line = br.readLine()) != null) {
					log.debug("stderr: "+line);
					success = false;
				}
			}
			
			// got final output
			if (success) {
				
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
