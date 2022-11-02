package input.visitor;

import java.util.AbstractMap;

import org.json.JSONObject;
import org.json.JSONTokener;

import input.components.FigureNode;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;
import utilities.io.StringUtilities;

public class ToJSONvisitor implements ComponentNodeVisitor
{	
	/**
	 * Converts provided FigureNode into a JSONobject
	 * @return object (JSONobject)
	 * */
	@Override
	public Object visitFigureNode(FigureNode node, Object o) 
	{
		JSONObject jsonFigureNode= new JSONObject();
		StringBuilder sb = new StringBuilder();
		int level = 0;
		
		sb.append("\n" + StringUtilities.indent(level+1) + "{" + "\n");
        sb.append(StringUtilities.indent(level+2)+ "Description: " + node.getDescription() + ",\n");
        
        //node.getPointsDatabase().accept(this, o);
        sb.append(StringUtilities.indent(level + 2) + "Points:" + "\n");
		//add this to the string builder
		for(PointNode point : node.getPointsDatabase().getPoints())		
			sb.append(StringUtilities.indent(level+3) + "Point"+ point.toString() + "\n");
		
        //node.getSegments().accept(this, o);
		sb.append(StringUtilities.indent(level + 2) + "Segments:" + "\n");
		
		for (PointNode key : node.getSegments().get_adjLists().keySet()) 
		{
			sb.append(StringUtilities.indent(level+3) + key.getName() + " : ");
			
			for(PointNode value : node.getSegments().get_adjLists().get(key)) 
				sb.append(value.getName() + " ");
			
			sb.append("\n");
		}
        
        sb.append(StringUtilities.indent(level+1) + "}\n");
		
		return jsonFigureNode.put("Figure", sb.toString());
	}

	/**
	 * Converts provided SegNodeDatabase into a JSONobject
	 * @return object (JSONobject)
	 * */
	@Override
	public Object visitSegmentDatabaseNode(SegmentNodeDatabase node, Object o) 
	{		
		JSONObject jsonSegNodeDatabase = new JSONObject();
		StringBuilder sb = new StringBuilder();
		int level = 0;
		
		sb.append(StringUtilities.indent(level) + "{" + "\n");
		
		for (PointNode key : node.get_adjLists().keySet()) 
		{
			sb.append(StringUtilities.indent(level+1) + key.getName() + " : ");
			
			for(PointNode value : node.get_adjLists().get(key)) 
				sb.append(value.getName() + " ");
			
			sb.append("\n");
		}
		
		sb.append(StringUtilities.indent(level) + "}" + "\n");
		
		return jsonSegNodeDatabase.put("Segments", sb.toString());
	}

	/**
	 * Method is unused
	 * */
	@Override
	public Object visitSegmentNode(SegmentNode node, Object o) {
		return null;
	}

	/**
	 * Converts provided PointNode into a JSONobject
	 * @return object (JSONobject)
	 * */
	@Override
	public Object visitPointNode(PointNode node, Object o) 
	{	
		JSONObject jsonPoint = new JSONObject();
		return jsonPoint.put("Point", node.toString());
	}

	/**
	 * Converts provided PointNodeDatabase into a JSONobject
	 * @return object (JSONobject)
	 * */
	@Override
	public Object visitPointNodeDatabase(PointNodeDatabase node, Object o) 
	{
		JSONObject jsonPointNodeDatabase = new JSONObject();
		StringBuilder sb = new StringBuilder();
		int level = 0;
		//node.unparse(sb, 0);
		sb.append(StringUtilities.indent(level) + "\n" + "{" + "\n");
		//add this to the string builder
		for(PointNode point : node.getPoints())		
			sb.append(StringUtilities.indent(level+1) + "Point"+ point.toString() + "\n");
		return jsonPointNodeDatabase.put("Points ", sb.toString());

	}
	
	/**
	 * Converts given JSONobject into "pretty-printed" String
	 * @return String
	 * */
	public String toString(int i, JSONObject jObject) 
	{
		return jObject.toString(i);
	}

}
