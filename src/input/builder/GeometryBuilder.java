package input.builder;

import java.util.List;

import input.components.FigureNode;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;

public class GeometryBuilder extends DefaultBuilder
{
	public GeometryBuilder() { }

	
	
	/**
	 * Creates a new figure node with the appropriate 
	 * description, set of points and set of segments
	 * @param description -- the description of the figure as a string
	 * @param points -- a database of the points of a figure
	 * @param segments -- a database of the segments of a figure
	 * @return figure node being created
	 */
	@Override
    public FigureNode buildFigureNode(String description,
    		                          PointNodeDatabase points,
    		                          SegmentNodeDatabase segments)
    {
		return new FigureNode(description , points, segments);
 
    }
    
	/**
	 * Creates a new database for the segment nodes in a 
	 * geometry figure
	 * @return the segment node database being created
	 */
	@Override
    public SegmentNodeDatabase buildSegmentNodeDatabase()
    {
        return new SegmentNodeDatabase();
    }
    
	/**
	 * Adds the segment from input to the existing database
	 * @param from -- one end of the segment being added
	 * @param to -- other end of segment being added to database
	 */
	@Override
    public void addSegmentToDatabase(SegmentNodeDatabase segments, PointNode from, PointNode to)
    {
    	if (segments != null) segments.addUndirectedEdge(from, to);
 
    }
    
	/**
	 * Builds a segment node with points from input
	 * @param pt1 -- one end point of a segment
	 * @param pt2 -- second end of point in a segment 
	 * @return segment node being created
	 */
	@Override
    public SegmentNode buildSegmentNode(PointNode pt1, PointNode pt2)
    {
        return new SegmentNode(pt1 , pt2);
    }
    
	
	/**
	 * creates a database of point nodes and adds points from input
	 * @param points -- list of points being added to database
	 */
	@Override
    public PointNodeDatabase buildPointDatabaseNode(List<PointNode> points)
    {
        return new PointNodeDatabase(points);
    }
    
	/**
	 * Builds a point node with information from input
	 * @param name -- the name of point node
	 * @param x -- the horizontal coordinate of the point 
	 * @param y -- the vertical coordinate of the point
	 */
	
	@Override
    public PointNode buildPointNode(String name, double x, double y)
    {
		return new PointNode(name , x ,y);
    }
}
