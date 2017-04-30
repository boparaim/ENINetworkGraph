package ca.empowered.nms.simulator.topology.element;

import java.util.ArrayList;
import java.util.HashMap;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.simulator.utils.Constants.STATE;

public class Node {

	private static final Logger log = LogManager.getLogger(Node.class.getName());
	
	private String name;
	private String className;
	private String description;
	private STATE initialState;
	private Integer rank;
	private HashMap<String, Integer> relatableTo;
	private ArrayList<String> connectedTo = new ArrayList<>();
	
	public boolean isRelatableTo(Node otherNode) {
		boolean isValid = false;
		Node thisNode = this;
		
		// don't connect to itself
		if ( thisNode.equals(otherNode) ) {
			//log.debug("same object");
			return isValid;
		}
		
		// don't connect to same object multiple times
		if ( thisNode.connectedTo.contains(otherNode.getName()) ) {
			//log.debug("already connected 1");
			return isValid;
		}
		if ( otherNode.connectedTo.contains(thisNode.getName()) ) {
			//log.debug("already connected 2");
			return isValid;
		}
		
		String thisNodeClass = thisNode.getClassName();
		String otherNodeClass = otherNode.getClassName();
				
		// is a relationship defined for these two types
		if ( !thisNode.relatableTo.containsKey(otherNodeClass) ) {
			//log.debug(thisNodeClass+" can't connect to 1 "+otherNodeClass);
			return isValid;
		}
		if ( !otherNode.relatableTo.containsKey(thisNodeClass) ) {
			//log.debug(thisNodeClass+" can't connect to 2 "+otherNodeClass);
			return isValid;
		}
		
		int thisNodeOtherPossibleCount = 0;
		int otherNodeThisPossibleCount = 0;
		
		thisNodeOtherPossibleCount = thisNode.relatableTo.get(otherNodeClass);
		otherNodeThisPossibleCount = otherNode.relatableTo.get(thisNodeClass);
		//log.debug("count this - "+thisNodeOtherPossibleCount+" count other - "+otherNodeThisPossibleCount);
		
		// validate we haven't passed the limit on any side
		if (thisNodeOtherPossibleCount > 0
				&& otherNodeThisPossibleCount > 0) {
			synchronized (thisNode) {
				thisNode.relatableTo.put(otherNodeClass, (thisNodeOtherPossibleCount - 1));
				thisNode.connectedTo.add(otherNode.getName());
			}
			synchronized (otherNode) {
				otherNode.relatableTo.put(thisNodeClass, (otherNodeThisPossibleCount - 1));
				otherNode.connectedTo.add(thisNode.getName());
			}
			
			isValid = true;
		}
		
		return isValid;
	}

	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public STATE getInitialState() {
		return initialState;
	}
	public void setInitialState(STATE initialState) {
		this.initialState = initialState;
	}
	public Integer getRank() {
		return rank;
	}
	public void setRank(Integer rank) {
		this.rank = rank;
	}
	public HashMap<String, Integer> getRelatableTo() {
		return relatableTo;
	}
	public void setRelatableTo(HashMap<String, Integer> relatableTo) {
		this.relatableTo = relatableTo;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public ArrayList<String> getConnectedTo() {
		return connectedTo;
	}
	public void setConnectedTo(ArrayList<String> connectedTo) {
		this.connectedTo = connectedTo;
	}		
}
