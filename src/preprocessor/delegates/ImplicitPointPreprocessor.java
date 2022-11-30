package preprocessor.delegates;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import geometry_objects.Segment;
import geometry_objects.delegates.intersections.IntersectionDelegate;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import geometry_objects.points.PointNamingFactory;

public class ImplicitPointPreprocessor
{
	/**
	 * It is possible that some of the defined segments intersect
	 * and points that are not named; we need to capture those
	 * points and name them.
	 * 
	 * Algorithm:
	 *    Check each segment for intersection. If two segments intersect, 
	 *    check if the implicit point already exists, and if it doesn't
	 *    then we add to the set of implicit points.
	 */
	public static Set<Point> compute(PointDatabase givenPoints, List<Segment> givenSegments)
	{
		Set<Point> implicitPoints = new LinkedHashSet<Point>();
		
		Set<Point> allGivenPoints = givenPoints.getPoints();
		for(int i = 0; i < givenSegments.size()-1; i++) 
		{
			for(int j = i+1; j < givenSegments.size(); j++) 
			{
				
				 Point checkPoint = givenSegments.get(i).segmentIntersection(givenSegments.get(j));
				 
				//checks if point of intersection is an existing point
				if(checkPoint != null && !(allGivenPoints.contains(checkPoint))) 
				{
					implicitPoints.add(checkPoint);
				}
			}
		}
		return implicitPoints;
	}

}
