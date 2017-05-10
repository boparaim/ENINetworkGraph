package ca.empowered.nms.graph.topology.element;

import java.awt.Point;
import java.util.ArrayList;

public class TreeNode {

	private TreeNode parent;
	private ArrayList<TreeNode> children;
	private Node thisNode;
	private String id;
	private Point location;
	private float weight;
	
	public TreeNode(Node thisNode) {
		if (children == null)
			children = new ArrayList<>();
		setThisNode(thisNode);
		setId(thisNode.getName());
		setLocation(new Point(0, 0));
		setWeight(0.0f);
	}
	
	public TreeNode getParent() {
		return parent;
	}
	public void setParent(TreeNode parent) {
		this.parent = parent;
	}
	public ArrayList<TreeNode> getChildren() {
		return children;
	}
	public void setChildren(ArrayList<TreeNode> children) {
		this.children = children;
	}
	public Node getThisNode() {
		return thisNode;
	}
	public void setThisNode(Node thisNode) {
		this.thisNode = thisNode;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Point getLocation() {
		return location;
	}
	public void setLocation(Point location) {
		this.location = location;
	}
	public float getWeight() {
		return weight;
	}
	public void setWeight(float weight) {
		this.weight = weight;
	}
	
}
