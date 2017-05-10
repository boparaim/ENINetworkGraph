package ca.empowered.nms.graph.topology.generator;

import java.util.HashMap;
import java.util.Iterator;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import ca.empowered.nms.graph.topology.element.Node;
import ca.empowered.nms.graph.topology.element.TreeNode;

public class Tree {

	private static final Logger log = LogManager.getLogger(Tree.class.getName());
	public HashMap<String, TreeNode> allNodes = new HashMap<>();
	public TreeNode rootNode;
	private int level = 0;
	
	public void addNode(Node parent, Node child) {
		TreeNode tpNode = null;
		TreeNode tcNode = null;
		
		if (allNodes.containsKey(parent.getName()))
			tpNode = allNodes.get(parent.getName());
		else 
			tpNode = new TreeNode(parent);
		
		if (allNodes.containsKey(child.getName()))
			tcNode = allNodes.get(child.getName());
		else 
			tcNode = new TreeNode(child);
		
		tcNode.setParent(tpNode);
		tpNode.getChildren().add(tcNode);
		
		allNodes.put(tpNode.getId(), tpNode);
		allNodes.put(tcNode.getId(), tcNode);
		
		/*while (tcNode.getParent() != null) {
			Log.debug("adding nodes "+tpNode.getThisNode().getName()+" & "+tcNode.getThisNode().getName()
					+" root is "+tcNode.getParent().getThisNode().getName());
			allNodes.clear();
			allNodes.add(tcNode.getParent());
		}*/
	}
	
	public void clean() {
		Iterator<TreeNode> it = allNodes.values().iterator();
		while (it.hasNext()) {
			TreeNode node = it.next();
			if (node.getParent() != null) {
				it.remove();
			}
		}
		
		// bind everything to a root node
		Node rNode = new Node();
		rNode.setName("root");
		this.rootNode = new TreeNode(rNode);
		allNodes.values().forEach(node -> {
			this.rootNode.getChildren().add(node);
			node.setParent(this.rootNode);
		});
		
		// remove everything but root
		it = allNodes.values().iterator();
		while (it.hasNext()) {
			TreeNode node = it.next();
			if (node != this.rootNode) {
				it.remove();
			}
		}
	}
	
	public void printTree() {
		
	}
	
	public StringBuffer printChildren(TreeNode parent, StringBuffer buffer) {
		level++;
		if (parent.getChildren() == null)
			return buffer;
		String tabt = "";
		for (int i = 0; i < level; i++) {
			tabt += "\t";
		}
		//final String tab = tabt;
		//parent.getChildren().forEach(child -> {
		for (int i = 0; i < parent.getChildren().size(); i++) {
			//log.debug(tab+"["+level+"] "+child.getId());
			buffer.append(tabt+"["+level+"] "+parent.getChildren().get(i).getId()+"\n");
			printChildren(parent.getChildren().get(i), buffer);
		}
		//});
		--level;
		return buffer;
	}
	
	
}
