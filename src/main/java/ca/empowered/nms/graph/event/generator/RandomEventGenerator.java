package ca.empowered.nms.graph.event.generator;

import java.util.ArrayList;
import java.util.Random;

import ca.empowered.nms.graph.api.NodeManager;
import ca.empowered.nms.graph.node.NodeElement;
import ca.empowered.nms.graph.utils.Constants.STATE;

/**
 * This class is an event generator that creates events on list of given nodes.
 * 
 * @author mboparai
 *
 */
public class RandomEventGenerator extends EventGenerator {

	public RandomEventGenerator(final Integer intervalSeconds, final ArrayList<String> nodeList) {
		super(intervalSeconds, nodeList, "UP,DOWN,DEGRADED");
		
		generatorThread = new Thread() {
			@Override
			public void run() {
				super.run();
				
				Random random1 = new Random();
				Random random2 = new Random();
				
				// generated events doesn't depend on node state
				// and doesn't have any effect on the state of that node
				while (true) {
					if (generatorThread.isInterrupted())
						break;
					
					try {
						Thread.sleep(intervalSeconds * 1000);
						
						log.info("generating an event in "+RandomEventGenerator.this.getClass().getSimpleName());
						int i = random1.nextInt(nodeList.size());
						int j = random2.nextInt(alarmNameList.size());
						
						NodeElement node = NodeManager.getGraph().getNode(nodeList.get(i));
						node.setCurrentState(STATE.valueOf(alarmNameList.get(j)));
					} catch (Exception e) {
						log.error(e.getMessage(), e);
					}
				}
			}
		};
	}
	
	@Override
	public void start() {
		generatorThread.start();
	}
	
	@Override
	public void stop() {
		generatorThread.interrupt();
	}
	
}
