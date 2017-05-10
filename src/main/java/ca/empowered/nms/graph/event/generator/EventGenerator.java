package ca.empowered.nms.graph.event.generator;

import java.util.ArrayList;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Base class for all Event generators.
 * 
 * @author mboparai
 *
 */
public abstract class EventGenerator implements EventBuilder {

	protected static final Logger log = LogManager.getLogger(EventGenerator.class.getName());
	/** generator thread */
	protected Thread generatorThread;
	/** interval in seconds between two events */
	protected Integer intervalSeconds;
	/** list of nodes to operate on */
	protected ArrayList<String> nodeList;
	/** comma separated list of event names to use */
	protected String eventNameSet;
	/** list of event names to use */
	protected ArrayList<String> alarmNameList;
	
	public EventGenerator(final Integer intervalSeconds, final ArrayList<String> nodeList, final String eventNameSet) {
		this.intervalSeconds = intervalSeconds;
		this.nodeList = nodeList;
		this.eventNameSet = eventNameSet;
		
		this.alarmNameList = parseEventNameSet();
	}
	
	/**
	 * Create array list from a event list string.
	 * 
	 * @return
	 */
	private ArrayList<String> parseEventNameSet() {
		ArrayList<String> alarmNames = new ArrayList<>();
		try {
			alarmNames = new ArrayList<String>(Arrays.asList(this.eventNameSet.split(",")));
			alarmNames.stream().forEach(alarmName -> alarmName.trim());
		} catch (Exception e) {
			log.error(e.getMessage(), e);
		}
		
		return alarmNames;
	}
	
	public Integer getIntervalSeconds() {
		return intervalSeconds;
	}
	public void setIntervalSeconds(Integer intervalSeconds) {
		this.intervalSeconds = intervalSeconds;
	}
	public ArrayList<String> getNodeList() {
		return nodeList;
	}
	public void setNodeList(ArrayList<String> nodeList) {
		this.nodeList = nodeList;
	}
	public String getEventNameSet() {
		return eventNameSet;
	}
	public void setEventNameSet(String eventNameSet) {
		this.eventNameSet = eventNameSet;
	}
	public ArrayList<String> getAlarmNameList() {
		return alarmNameList;
	}
	public void setAlarmNameList(ArrayList<String> alarmNameList) {
		this.alarmNameList = alarmNameList;
	}
	
}
