package ca.empowered.nms.simulator.utils;

/**
 * Constants - utility class.
 * 
 * @author mboparai
 *
 */
public class Constants {

	/** possible states for nodes */
	public static enum STATE { DOWN, UP, DEGRADED, UNKNOWN, OTHER  };
	/** possible severities for an event */
	public static enum SEVERITY { CLEAR, INFO, MINOR, MAJOR, CRITICAL };
	/** possible file formats for topology export */
	public static enum FILE_FORMAT { TXT, JSON, XML };
	/** possible node/edge info sources */
	public static enum TOPOLOGY_SOURCE { JSON_FILE, MYSQL_DATABASE, SERIALIZED_OBJECT };
}