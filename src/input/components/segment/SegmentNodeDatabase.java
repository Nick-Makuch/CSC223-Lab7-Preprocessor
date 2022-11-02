
/**
 * A database to store segment nodes
 * 
 * <p>Bugs: 
 * 
 * @author Jace Rettig and James Crawford
 * @Date 9-1-22
 */
package input.components.segment;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.*;

import utilities.io.StringUtilities;

import input.components.ComponentNode;
import input.components.point.*;
import input.visitor.ComponentNodeVisitor;

public class SegmentNodeDatabase implements ComponentNode {
	protected Map<PointNode, Set<PointNode>> _adjLists;
	
	public SegmentNodeDatabase() {
		_adjLists = new HashMap<PointNode, Set<PointNode>>();
	}
	
	public SegmentNodeDatabase(Map<PointNode, Set<PointNode>> m) {
		_adjLists = m;
	}
	
	@Override
	 public Object accept(ComponentNodeVisitor visitor, Object o)
	 {
		return visitor.visitSegmentDatabaseNode(this, o);
	 }
	
	public Map<PointNode, Set<PointNode>> get_adjLists() {return _adjLists;}
	
	/**
	 * Calculates the number of undirectedEdges in the database
	 * @return the number of undirected edges
	 */
	public int numUndirectedEdges() {
		int numUndirectedEdges = 0;
		//count number of pairs in adjLists
		//returns set of every entry pair
		for (Map.Entry<PointNode, Set<PointNode>> OuterPoint: _adjLists.entrySet()) {
			for (PointNode value: OuterPoint.getValue()) {
				if (_adjLists.get(value).contains(OuterPoint.getKey())) {
					numUndirectedEdges += 1;
				}	
			}
		}
		//divide by 2 to remove duplicates
		numUndirectedEdges = numUndirectedEdges / 2;
		return numUndirectedEdges;
	}
	
	/**
	 * Helper method to add a one way/directed edge from two given points
	 * @param pt1
	 * @param pt2
	 * @throws Exception 
	 */
	private void addDirectedEdge(PointNode pt1, PointNode pt2)  {
		if (_adjLists.get(pt1) == null) {
			Set<PointNode> nodeSet = new HashSet<PointNode>();
			_adjLists.put(pt1, nodeSet);
			_adjLists.get(pt1).add(pt2);
		} 
		else if (_adjLists.get(pt1).contains(pt2) || pt1.equals(pt2)) {
			throw new ArithmeticException("Invalid Edge");
		}
		else {
			_adjLists.get(pt1).add(pt2);
		}
	}
	
	/**
	 * Adds an undirectedEdge from two given points
	 * @param pt1
	 * @param pt2
	 */
	public void addUndirectedEdge(PointNode pt1, PointNode pt2) {
		//Call directed edge twice, makes 1 directed edge
		addDirectedEdge(pt1, pt2);
		addDirectedEdge(pt2, pt1);
			//pt1 -> pt2 and pt1 <- pt2
			//pt1 <-> pt2
	}
	
	/**
	 * Helper method to turn this list into set
	 * @param list of pointNodes
	 * @return set of PointNodes
	 */
	private Set<PointNode> listToSet(List<PointNode> list) {
		Set<PointNode> nodeSet = new HashSet<PointNode>();
		for (PointNode node : list) {
			nodeSet.add(node);
		}
		return nodeSet;
	}
	
	/**
	 * Adds a new adjacencyList to adjLists
	 * @param point- point to add
	 * @param list- list of points that point is next to
	 */
	public void addAdjacencyList(PointNode point, List<PointNode> list) {
		//turn into set
		//Add point as the key as d list as the "value" pair to _adjLists
		_adjLists.put(point, this.listToSet(list));
	}
	
	
	/**
	 * Creates a list of segmentNodes based on the adjacency lists, including duplicates
	 * @return segmentList
	 */
	public List<SegmentNode> asSegmentList() {
		List<SegmentNode> segmentList = new ArrayList<SegmentNode>();
		//loop through each list in adjLists
		for (Map.Entry<PointNode, Set<PointNode>> OuterPoint: _adjLists.entrySet()) {
			//loop through list values
			for (PointNode value: OuterPoint.getValue()) {
				SegmentNode tempSegment = new SegmentNode(OuterPoint.getKey(), value);
				segmentList.add(tempSegment);
			}
		}
		return segmentList;
		
	}
	/**
	 * Helper Method to check if a segment's compliment exists in the list 
	 * @param start of segment
	 * @param end of segment
	 * @param segmentList
	 * @return True if the segmentList contains the reversed segment
	 */
	private boolean hasDirectedSegment(PointNode start, PointNode end, List<SegmentNode> segmentList) {
		SegmentNode segmentReversed = new SegmentNode(end, start);
		//check if compliment is in list
		if (segmentList.contains(segmentReversed)) return true;
		//if not false
		return false;
	}
	
	/**
	 * Creates a list of unique segmentNodes based on the adjacency lists
	 * @return a unique segmentList
	 */
	public List<SegmentNode> asUniqueSegmentList() {
		//unique, CANNOT have duplicate segments
		List<SegmentNode> segmentList = new ArrayList<SegmentNode>();
		//loop through each list in adjLists
		for (Map.Entry<PointNode, Set<PointNode>> OuterPoint: _adjLists.entrySet()) {
			//loop through list values
			for (PointNode value: OuterPoint.getValue()) {
				SegmentNode tempSegment = new SegmentNode(OuterPoint.getKey(), value);
				if (!(this.hasDirectedSegment(OuterPoint.getKey(), value, segmentList))) {
					segmentList.add(tempSegment);
				}
			}
		}
		return segmentList;
	}
	
	/**
	 * Converts the list of segmentNodes into a string of the segmentedNodes
	 * @param list of segmentNodes
	 * @return the list in string form
	 */
	public String segmentListToString(List<SegmentNode> list) {
		String segList = "";
		if (list.size() < 0) return null;
		
		for (SegmentNode sn : list)
		{
			segList += sn.toString() + " ";
		}
		return segList;
	}

	
	
	

	/*
	 * 
	 * Method to add all the segments in the correct format to a string builder
	 * @param string builder and level
	 */
	
	
	@Override
	public void unparse(StringBuilder sb, int level) 
	{
		//sb.append(StringUtilities.indent(level) +"{" + "\n");
		sb.append(StringUtilities.indent(level) + "Segments:" + "\n");
	    sb.append(StringUtilities.indent(level) + "{" + "\n");
		
		for (PointNode key : _adjLists.keySet()) 
		{
			sb.append(StringUtilities.indent(level+1) + key.getName() + " : ");
			
			for(PointNode value : _adjLists.get(key)) 
				sb.append(value.getName() + " ");
			
			sb.append("\n");
		}
		
		sb.append(StringUtilities.indent(level) + "}" + "\n");
		//sb.append(StringUtilities.indent(level) +"}" + "\n");
	}
	
	
}

