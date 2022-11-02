package geometry_objects.points;

import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/*
 * Given a pair of coordinates; generate a unique name for it;
 * return that point object.
 *
 * Names go from A..Z..AA..ZZ..AAA...ZZZ
 */
public class PointNamingFactory
{
	private static final String _PREFIX = "*_"; // Distinguishes generated names

	private static final char START_LETTER = 'A';
	private static final char END_LETTER = 'Z';

	private String _currentName = "A";
	private int _numLetters = 1;

	//
	// A hashed container for the database of points;
	// This requires the Point class implement equals
	// based solely on the individual values and not a name
	// We need a get() method; HashSet doesn't offer one.
	// Each entry is a <Key, Value> pair where Key == Value
	//
	protected Map<Point, Point> _database;

	public PointNamingFactory()
	{
		_database = new LinkedHashMap<>();
	}

	/**
	 * 
	 * @param points -- a list of points, named or not named
	 */
	public PointNamingFactory(List<Point> points)
	{
		_database = new LinkedHashMap<>();
		
		for(Point point : points)
			_database.put(point, point);
	}

	/**
	 * @param pt -- (x, y) coordinate pair object
	 * @return a point (if it already exists) or a completely new point that
	 *         has been added to the database
	 */
	public Point put(Point pt)
	{
		Set<Point> points = getAllPoints();
		
		for(Point p : points)
			if(pt.equals(p))
				return p;
		
		_database.put(pt, pt);
		return pt;
	}

	/**
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return a point (if it already exists) or a completely new point that has been added to the database
	 */
	public Point put(double x, double y)
	{
		Point newPoint = new Point(getCurrentName(), x, y);
		Set<Point> points = getAllPoints();
		
		for(Point p : points)
			//if(newPoint.compareTo(p) == 0)
			if(newPoint.equals(p))
				return p;
		
		_database.put(newPoint, newPoint);
		return newPoint;
	}

	/**
	 * @param name -- the name of the point 
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return a point (if it already exists) or a completely new point that
	 *         has been added to the database.
	 *         
	 *         If the point is in the database and the name differs from what
	 *         is given, nothing in the database will be changed; essentially
	 *         this means we use the first name given for a point.
	 *         
	 *         The exception is that a valid name can overwrite an unnamed point.
	 */
	public Point put(String name, double x, double y)
	{
		return createNewPoint(name, x, y);

	}    

	/**
	 * Strict access (read-only of the database)
	 * 
	 * @param x
	 * @param y
	 * @return stored database Object corresponding to (x, y) 
	 */
	public Point get(double x, double y)
	{		
		Point newPoint = new Point(x, y);
		Set<Point> points = getAllPoints();
		
		for(Point p : points) 
			if(p.compareTo(newPoint) == 0)
				return p;
		
		return null;
	}	
	public Point get(Point pt)
	{
		return _database.get(pt);
	}

	/**
	 * @param name -- the name of the point 
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return a point (if it already exists) or a completely new point that
	 *         has been added to the database.
	 *         
	 *         If the point is in the database and the name differs from what
	 *         is given, nothing in the database will be changed; essentially
	 *         this means we use the first name given for a point.
	 *         
	 *         The exception is that a valid name can overwrite an unnamed point.
	 */
	private Point lookupExisting(String name, double x, double y)
	{
		Point newPoint = new Point(name, x, y);
		Set<Point> points = getAllPoints();
		
		for(Point p : points) {
			if(newPoint.equals(p) && p.getName() != Point.ANONYMOUS)
				return p;
			
		}
		
		return null;
	}  

	/**
	 * @param name -- the name of the point 
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return a point (if it already exists) or a completely new point that
	 *         has been added to the database.
	 *         
	 *         If the point is in the database and the name differs from what
	 *         is given, nothing in the database will be changed; essentially
	 *         this means we use the first name given for a point.
	 *         
	 *         The exception is that a valid name can overwrite an unnamed point.
	 */
	private Point createNewPoint(String name, double x, double y)
	{
		if(!(lookupExisting(name, x, y) == null)) return lookupExisting(name, x, y);

		Point newPoint = null;
		if(name == Point.ANONYMOUS)
			newPoint = new Point(getCurrentName(), x, y);
		else
			newPoint = new Point(name, x, y);
		
		Set<Point> points = getAllPoints();
		
		for(Point p : points) 
		{			
			if(newPoint.equals(p) && p.getName() == Point.ANONYMOUS) 
			{
				_database.remove(p, p);
				_database.put(newPoint, newPoint);
				return newPoint;
			}
				
		}
		_database.put(newPoint, newPoint);
		return newPoint;
	}

	/**
	 * @param x -- single coordinate
	 * @param y -- single coordinate
	 * @return simple containment; no updating
	 */
	public boolean contains(double x, double y) { return _database.containsKey(new Point(x, y)); }
	public boolean contains(Point p) { return _database.containsKey(p);}

	/**
	 * @return acquires and returns the next name in sequence; also
	 * generates the next name in a 'lazy list' manner 
	 */
	private String getCurrentName()
	{
        String string = _PREFIX + _currentName;
        updateName();
        return string;
	}
	
	/**
	 * used for testing
	 * */
	public String getCurrentNameTester()
	{
        String string = _PREFIX + _currentName;
        updateName();
        return string;
	}

	/**
	 * Advances the current generated name to the next letter in the alphabet:
	 * 'A' -> 'B' -> 'C' -> 'Z' --> 'AA' -> 'BB'
	 */
	private  void updateName()
	{
		if(_currentName.charAt(0) == END_LETTER) {
			_currentName = "" + START_LETTER;
			_numLetters++;
		}	
		else {
			char next = (char) (_currentName.charAt(0)+1);
			_currentName = "" + next;
		}
		
        for(int i = 1; i < _numLetters; i++)
        	_currentName += _currentName.charAt(0);
	}

	/**
	 * @return The entire database of points.
	 */
	public  Set<Point> getAllPoints()
	{
        return _database.keySet();
	}

	public void clear() { _database.clear(); }
	public int size() { return _database.size(); }

	@Override
	public String toString()
	{
        Set<Point> points = getAllPoints();
        String str = "";
        
        for(Point point : points) {
        	str += "" + point.getName() + "(" + point.getX() + " , " + point.getY() + ")" + "\n";
        }
        
        return str;
	
	}
}