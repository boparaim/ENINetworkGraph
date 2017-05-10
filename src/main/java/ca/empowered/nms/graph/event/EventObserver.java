package ca.empowered.nms.graph.event;

import java.util.Observable;
import java.util.Observer;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.node.NodeElement;

/**
 * Event observer for Node Elemenets.
 * 
 * @author mboparai
 *
 */
public class EventObserver implements Observer {

	private static final Logger log = LogManager.getLogger(EventObserver.class.getName());

	/**
	 * Send node to Notification factory for processing.
	 */
	@Override
	public void update(Observable observable, Object object) {
		NodeElement node = (NodeElement)object;
		
		log.debug("got status update for "+node.getId()+" --> "+node.getAttribute("state"));
		
		NotificationFactory.submitNotifcation(node);
	}

}
