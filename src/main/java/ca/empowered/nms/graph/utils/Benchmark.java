package ca.empowered.nms.graph.utils;

/**
 * Utility class for getting the duration passed b/w two operations.
 * 
 * @author mboparai
 *
 */
public class Benchmark {

	private static long now;
	private static long previous;
	
	public static void init() {
		previous = System.nanoTime();
	}
	
	private static long fromLast() {
		if (previous < 1L)
			previous = System.nanoTime();
		
		now = System.nanoTime();
		long diff = now - previous;		
		previous = now;		
		
		return diff;
	}
	
	public static String diffFromLast(String unit) {
		String diff = "0";
		unit = unit.toLowerCase();
		
		switch (unit) {
		case "nano":
			diff = String.valueOf(fromLast());
			break;
		case "milli":
			diff = String.valueOf(fromLast()/1000000);
			break;
		case "sec":
			diff = String.valueOf(fromLast()/1000000000);
			break;
		case "min":
			diff = String.valueOf(fromLast()/60000000000L);
			break;
		case "hour":
			diff = String.valueOf(fromLast()/3600000000000L);
			break;
		default:
			diff = "use nano|milli|sec|min|hour";
		}
		
		return diff;
	}
	
}
