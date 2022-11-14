package preprocessor.delegates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import preprocessor.delegates.ImplicitPointPreprocessor;

public class ImplicitPointPreprocessorTest 
{
	/**
	 * 		A
	 *     /|\
	 *    / | \
	 *   B--|--C
	 *    \ | /
	 *     \|/
	 *      D
	 *      
	 * This test uses the above shape and computes 
	 * the middle intersection point as it is
	 * an implicit point
	 * */
	@Test
	void ComputeTest() 
	{
		Point a = new Point("A", 4.0, 8.0);
		Point b = new Point("B", 2.0, 6.0);
		Point c = new Point("C", 6.0, 6.0);
		Point d = new Point("D", 4.0, 4.0);
		
		Point x = new Point(4.0, 6.0);
		
		Segment ab = new Segment(a, b);
		Segment ac = new Segment(a, c);
		Segment ad = new Segment(a, d);
		Segment bc = new Segment(b, c);
		Segment bd = new Segment(b, d);
		Segment cd = new Segment(c, d);
		
		List<Point> points = new ArrayList<Point>();
		points.add(a);
		points.add(b);
		points.add(c);
		points.add(d);
		PointDatabase pointsDatabase = new PointDatabase(points);
		
		List<Segment> segList = new ArrayList<Segment>();
		segList.add(ab);
		segList.add(ac);
		segList.add(ad);
		segList.add(bc);
		segList.add(bd);
		segList.add(cd);
		
		Set<Point> tester = new HashSet<Point>();
		assertTrue(tester.isEmpty());
		
		tester = ImplicitPointPreprocessor.compute(pointsDatabase, segList);
		assertFalse(tester.isEmpty());
		assertEquals(1, tester.size());
		assertFalse(tester.contains(null));
		
		Object[] arr = tester.toArray();
		assertTrue(arr[0] instanceof Point);
		Point point = (Point) arr[0];
		//System.out.println(point.getName() + " : " + point.getX() + ", " + point.getY());
		assertEquals(point, x);

	}
}
