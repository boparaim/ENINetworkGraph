package ca.empowered.nms.graph.event.generator;

/**
 * Interface for Event generators.
 * 
 * @author mboparai
 *
 */
public interface EventBuilder {

	/**
	 * Start the generator thread.
	 */
	public void start();
	
	/**
	 * Stop the generator thread.
	 */
	public void stop();
	
}
