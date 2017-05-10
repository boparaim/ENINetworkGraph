package ca.empowered.nms.graph.event;

import java.sql.Timestamp;

import ca.empowered.nms.graph.config.Settings;
import ca.empowered.nms.graph.node.NodeElement;
import ca.empowered.nms.graph.utils.Constants.SEVERITY;
import ca.empowered.nms.graph.utils.Constants.STATE;

/**
 * This class represents a notification.
 * 
 * @author mboparai
 *
 */
public class Notification {
	
	// class/instance/type
	private String id;
	
	// this app
	private String manager;
	// element
	private String source;
	private String className;
	private String instanceName;
	private String eventName;
	// populate on element on generation
	private String location;
	// populate on element on generation
	private String type;
	// notify or clear
	private SEVERITY severity;
	private String description;
	private long timestamp;
	
	public Notification(String className, String instanceName, String eventName, String tag) {
		this.className = className;
		this.instanceName = instanceName;
		this.eventName = eventName;
		
		id = this.className + ":_:" + this.instanceName + ":_:" + tag;
		manager = Settings.getAppName();
		source = "Agent-"+instanceName;
		
		// TODO: change back to milliseconds
		timestamp = new Timestamp(System.currentTimeMillis()).toInstant().toEpochMilli();
		//timestamp = new Timestamp(System.currentTimeMillis()).toInstant().getEpochSecond();
	}
	
	public Notification(NodeElement node) {
		this(node, "");
	}
	
	public Notification(NodeElement node, String tag) {
		this(node.getAttribute("class"), node.getId(), node.getCurrentState().toString(), tag);
	}
	
	/**
	 * Get json representation of this notification.
	 * 
	 * @return
	 */
	public String toJSON() {
		// TODO: testing with moog
		String sev = "1";
		if (severity.toString().equals("CLEAR"))
			sev = "0";
		if (severity.toString().equals("CRITICAL"))
			sev = "5";
		
		return "{\"id\":\""+id+"\", "
				+ "\"manager\":\""+manager+"\", "
				+ "\"source\":\""+source+"\", "
				+ "\"className\":\""+className+"\", "
				+ "\"instanceName\":\""+instanceName+"\", "
				+ "\"eventName\":\""+eventName+"\", "
				+ "\"location\":\""+location+"\", "
				+ "\"type\":\""+type+"\", "
				+ "\"severity\":\""+sev+"\", "
				+ "\"description\":\""+description+"\", "
				+ "\"timestamp\":\""+timestamp+"\"}";
	}
	
	public String getManager() {
		return manager;
	}
	public void setManager(String manager) {
		this.manager = manager;
	}
	public String getSource() {
		return source;
	}
	public void setSource(String source) {
		this.source = source;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public String getInstanceName() {
		return instanceName;
	}
	public void setInstanceName(String instanceName) {
		this.instanceName = instanceName;
	}
	public String getEventName() {
		return eventName;
	}
	public void setEventName(String eventName) {
		this.eventName = eventName;
	}
	public String getLocation() {
		return location;
	}
	public void setLocation(String location) {
		this.location = location;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public SEVERITY getSeverity() {
		return severity;
	}
	public void setSeverity(SEVERITY severity) {
		this.severity = severity;
	}
	/**
	 * Update severity of this notification based on current state.
	 * 
	 * @param node
	 */
	public void updateSeverity(NodeElement node) {
		if (node.getCurrentState().equals(STATE.DOWN))
			this.setSeverity(SEVERITY.CRITICAL);
		else if (node.getCurrentState().equals(STATE.DEGRADED))
			this.setSeverity(SEVERITY.MAJOR);
		else if (node.getCurrentState().equals(STATE.UP))
			this.setSeverity(SEVERITY.CLEAR);
		else
			this.setSeverity(SEVERITY.INFO);
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public void updateDescription() {
		description = className+" "+instanceName+" "+eventName+" "+severity;
	}
	public long getTimestamp() {
		return timestamp;
	}
	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}
	public String getId() {
		return id;
	}
	
}
