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
	 *    TODO
	 */
	public static Set<Point> compute(PointDatabase givenPoints, List<Segment> givenSegments)
	{
		Set<Point> implicitPoints = new LinkedHashSet<Point>();
		PointNamingFactory p = new PointNamingFactory();
		//checks each segment for intersections and if they intersect 
		//checks if point of intersection is an existing point
		Set<Point> allGivenPoints = givenPoints.getPoints();
		
		for(int i = 0; i < givenSegments.size()-1; i++) 
		{
			for(int j = i+1; j < givenSegments.size(); j++) 
			{
				 Point checkPoint = IntersectionDelegate.segmentIntersection(givenSegments.get(i), givenSegments.get(j));
				 checkPoint = p.rename(checkPoint); 
				
				if(!(allGivenPoints.contains(checkPoint)) && checkPoint != null) 
				{
					implicitPoints.add(checkPoint);
				}
			}
		}

		return implicitPoints;
	}

}
