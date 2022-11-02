package input.visitor;

import java.util.AbstractMap;
import java.util.Map;
import java.util.Set;

import input.components.*;
import input.components.point.*;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;
import utilities.io.StringUtilities;

//
// A JSON file may contain:
//
//     Figure:
//       Points
//       Segments
//
public class UnparseVisitor implements ComponentNodeVisitor
{
	@Override
	public Object visitFigureNode(FigureNode node, Object o)
	{
		// Unpack the input object containing a Stringbuilder and an indentation level
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		sb.append(StringUtilities.indent(level) + "{" + "\n");
		pair.setValue(pair.getValue() + 2);
		
        sb.append(StringUtilities.indent(level+1) + "Figure:\n");
        sb.append(StringUtilities.indent(level+1) +"{" + "\n");
        sb.append(StringUtilities.indent(level+2)+ "Description: " + node.getDescription() + ",\n");
        
        node.getPointsDatabase().accept(this, o);
        node.getSegments().accept(this, o);
        
        sb.append(StringUtilities.indent(level+1) + "}\n");
        sb.append(StringUtilities.indent(level) + "}\n");
        return sb;
	}

	@Override
	public Object visitSegmentDatabaseNode(SegmentNodeDatabase node, Object o)
	{
		// Unpack the input object containing a Stringbuilder and an indentation level
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
        
		node.unparse(sb, level);
		
        return sb;
	}

	/**
	 * This method should NOT be called since the segment database
	 * uses the Adjacency list representation
	 */
	@Override
	public Object visitSegmentNode(SegmentNode node, Object o)
	{
		return null;
	}

	@Override
	public Object visitPointNodeDatabase(PointNodeDatabase node, Object o)
	{
		// Unpack the input object containing a Stringbuilder and an indentation level
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		node.unparse(sb, level);
		
        return sb;
	}
	
	@Override
	public Object visitPointNode(PointNode node, Object o)
	{
		// Unpack the input object containing a Stringbuilder and an indentation level
		@SuppressWarnings("unchecked")
		AbstractMap.SimpleEntry<StringBuilder, Integer> pair = (AbstractMap.SimpleEntry<StringBuilder, Integer>)(o);
		StringBuilder sb = pair.getKey();
		int level = pair.getValue();
		
		node.unparse(sb, level);
		
        return sb;
	}
}