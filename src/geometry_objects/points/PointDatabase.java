package geometry_objects.points;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class represents a bi-directional database of points.
 * 
 * We can lookup a point given:
 *   (a) coordinates --> name
 *   (b) name --> coordinates
 * 
 * This is a Decorator class with the Point Factory in the background
 * 
 * @author xxx
 */
public class PointDatabase
{
	// The factory is the central means of representing all
    // points in a figure
    protected PointNamingFactory _factory;

    public Set<Point> getPoints() { return _factory.getAllPoints(); }
    
	public PointDatabase()
	{
        _factory = new PointNamingFactory();
	}

	public PointDatabase(List<Point> points)
	{
		if (points == null) _factory = new PointNamingFactory();
		else _factory = new PointNamingFactory(points);
	}

	public int size() { return _factory.size(); }
	
	/**
	 * Add this point to the database.
	 *   If the point with the given coordinates are not in the database, we add it (as is)
	 *   If the point is in the database:
	 *      * If the new point defines a name (and old is unnamed), we overwrite
	 *      * If the new point is unnamed, we do not overwrite
     *        the name (this is a do-nothing operation)
	 *     
	 * @param pt 
	 */
	public void put(String name, double x, double y)
	{
		_factory.put(name, x, y);
	}

	/**
	 * Given raw coordinates of a point, determine if it is named.
	 * 
	 * @param x,y -- doubles defining a point (x,y)
	 * @return a string corresponding to that point, if it is named.
	 */
	public String getName(double x, double y)
	{
		if (this.getPoint(x, y) == null) return null;
        return this.getPoint(x, y).getName();
	}
	public String getName(Point pt)
	{
		if (this.getPoint(pt) == null) return null;
        return this.getPoint(pt).getName();
	}

	/**
	 * Given the name of a point, determine the coordinates.
	 * 
	 * @param name -- a String name
	 * @return an (x,y) corresponding to name, if it has been defined.
	 */
	public Point getPoint(String name)
	{
		Stream<Point> pointStream = _factory.getAllPoints().stream();
        Optional<Point> point = pointStream.filter((Point pt) -> pt.getName().equals(name)).findFirst();

		return point.orElse(null);
	}

	/**
	 * Given a point, acquire the stored database object; facilitates
	 * singular objects and mitigates lingering copies of points.
	 * 
	 * @param pt -- a basic point
	 * @return the database entry for the point
	 */
	public Point getPoint(Point pt)
	{
        return _factory.get(pt);
	}

	/**
	 * Given a raw point (x, y), acquire the stored database object .
	 * 
	 * @param x,y -- doubles defining a point (x,y)
	 * @return a string corresponding to that point, if it is named.
	 */
	public Point getPoint(double x, double y)
	{
        return _factory.get(x, y);
	}
}