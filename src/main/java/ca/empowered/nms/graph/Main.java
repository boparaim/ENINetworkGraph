package ca.empowered.nms.graph;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
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
			
			File file = Paths.get(Main.class.getClass().getResource("/config.json").toURI()).toFile();
			TopologySource topoSource = new JsonFileTopologySource(file);
			TopologySourceManager topoManager = new TopologySourceManager(topoSource);
			MultiValuedMap<Node, Node> networkMap = topoManager.process();

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
				OutputStream stdin = process.getOutputStream();

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
			
			//log.debug(networkMap.size());
			/*
			Tree tree = new Tree();
			for (Node nodeA : networkMap.keySet()) {
				for (Node nodeB : networkMap.get(nodeA)) {
					if (nodeA.getRank() > nodeB.getRank())
						tree.addNode(nodeA, nodeB);
					else
						tree.addNode(nodeB, nodeA);
				}
			}
			
			tree.clean();
			log.debug("tree creation took "+Benchmark.diffFromLast("milli")+"ms");
			log.debug(tree.allNodes.size());
			/*tree.allNodes.keySet().forEach(
				nodeName -> log.debug(nodeName)
			);*/
			/*
			StringBuffer buffer = new StringBuffer();
			//log.debug(tree.printChildren(tree.rootNode, buffer));
			tree.printChildren(tree.rootNode, buffer);
			log.debug(buffer.toString());
			log.debug("tree printing took "+Benchmark.diffFromLast("milli")+"ms");
			
			
			/*
			Graph graph = new AdjacencyListGraph(Settings.getAppName());
			Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_GUI_THREAD);
			graph.addAttribute("ui.stylesheet", "url('"+Settings.getCssStyleSheet()+"')");
			
			graph.addNode("1");
			graph.addNode("2");
			graph.addEdge("1", "1", "2");
			
			Layout layout = Layouts.newLayoutAlgorithm();
			LayoutRunner optLayout = new LayoutRunner(graph, layout, true, false);
			ProxyPipe layoutPipeIn = optLayout.newLayoutPipe();
			layoutPipeIn.addAttributeSink(viewer.getGraphicGraph());
			
			do { 
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					// TODO: handle exception
				}
			} while (layout.getStabilization() < 0.9);
			
			optLayout.release();
			/*graph.getNodeSet().forEach((node) -> {
				log.debug("node - "+node.getId()+" : "+node.toString());
			});*/
			/*
			viewer.getGraphicGraph().getNodeSet().forEach(node -> {
				GraphicNode gNode = (GraphicNode) node;
				log.debug(gNode.getId()+" : "+gNode.getX()+" "+gNode.getY()+" "+gNode.getZ());
				
			});
			/*GraphRenderer renderer = Viewer.newGraphRenderer();
			viewer.addView(Viewer.DEFAULT_VIEW_ID, renderer);
			if (autoLayout) {
				Layout layout = Layouts.newLayoutAlgorithm();
				viewer.enableAutoLayout(layout);
			}
			return viewer;*/
			
			
			/*graph.replay();
			layoutPipeIn = optLayout.newLayoutPipe();
			layoutPipeIn.addAttributeSink(graph);*/
			
			/*ArrayList<String> createdNode = new ArrayList<>();
			for (Node key : ntwkMap.keySet()) {
				if (!createdNode.contains(key.getName())) {
					graph.addNode(key.getName());
					createdNode.add(key.getName());
				}
				for (Node list : ntwkMap.get(key)) {
					log.debug(key.getName()+" -->> "+list.getName());
					if (!createdNode.contains(key.getName())) {
						graph.addNode(list.getName());
						graph.addEdge(key.getName()+"."+list.getName(), key.getName(), list.getName());
						createdNode.add(key.getName());
					}
				}
			}*/
			
			/*SpringBox sbox = new SpringBox();
			//sbox.addAttributeSink(buffer);

			do
				sbox.compute();
			while (sbox.getStabilization() < 0.9);*/
			
			//graph.display();
			
			/*
			// start from here
			// Viewer
			optLayout = new LayoutRunner(graph, layout - new SpringBox(false), true - start, false - replay);
			
			// LayoutRunner
			this.pumpPipe = new ThreadProxyPipe();
			
			while (loop - true) {
				double limit = layout.getStabilizationLimit();

				pumpPipe.pump();
				if (limit > 0) {
					if (layout.getStabilization() > limit) {
						nap(longNap);
					} else {
						layout.compute();
						nap(shortNap);
					}
				} else {
					layout.compute();
					nap(shortNap);
				}
			}
			
			this.pumpPipe.init(graph, replay - false);
			
			// BarnesHutLayout
			NodeParticle np = newNodeParticle(id);
			nodes.addParticle(np);
			
			while (!eventQueue.isEmpty())
				eventQueue.remove().trigger();

			for (int i = 0; i < eltsSinks.size(); i++)
				eltsSinks.get(i).nodeAdded(sourceId, timeId, nodeId);

			while (!eventQueue.isEmpty())
				eventQueue.remove().trigger();
			
			graph.replay();
			
			
			SpringBox sbox = new SpringBox();
			//sbox.addAttributeSink(buffer);

			do
				sbox.compute();
			while (sbox.getStabilization() < 0.9);
			*/
			
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
		/*long endTime = System.nanoTime();
		long timeDiff = endTime - startTime;
		log.info("END: took "+timeDiff+"(ns) "+(timeDiff/1000000)+"(ms) or "+(timeDiff/1000000000)+"(s)");*/
	}

}
