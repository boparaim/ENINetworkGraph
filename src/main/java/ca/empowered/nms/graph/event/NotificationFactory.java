package ca.empowered.nms.graph.event;

import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.api.rest.RestServer;
import ca.empowered.nms.graph.config.Settings;
import ca.empowered.nms.graph.node.NodeElement;

/**
 * This class handles notifications.
 * 
 * @author mboparai
 *
 */
public class NotificationFactory {

	private static final Logger log = LogManager.getLogger(NotificationFactory.class.getName());
	/** list of notifications */
	private static HashMap<String, Notification> allNotifications = new HashMap<>();

	/**
	 * Add notification to the notification list.
	 * 
	 * @param node
	 */
	public static void submitNotifcation(NodeElement node) {
		Notification notification = new Notification(node);
		notification.updateSeverity(node);
		notification.updateDescription();
		allNotifications.put(notification.getId(), notification);
		
		reportNotifcation(notification);
	}
	
	/**
	 * Send a notification to rest web service.
	 * 
	 * @param notification
	 */
	public static void reportNotifcation(Notification notification) {
		if (!Settings.getRestClientEnabled()) {
			log.warn("trying to send JSON while eni.nms.simulator.rest.client.enabled is set to FALSE");
			return;
		}
		//log.debug("notification: "+notification);
		RestServer.sendJSON(notification.toJSON());
	}

	/**
	 * Get list of all notifications.
	 * 
	 * @return
	 */
	public static HashMap<String, Notification> getAllNotifications() {
		return allNotifications;
	}
	
}
