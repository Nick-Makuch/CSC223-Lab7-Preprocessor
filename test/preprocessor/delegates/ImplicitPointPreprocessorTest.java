package preprocessor.delegates;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;

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
	 * the middle intersection point as it is not
	 * an implicit point
	 * */
	@Test
	void ComputeTest() 
	{
		Point a = new Point("A", 8.0, 4.0);
		Point b = new Point("B", 6.0, 6.0);
		Point c = new Point("C", 2.0, 6.0);
		Point d = new Point("D", 4.0, 4.0);
		
		Segment ab = new Segment(a, b);
		Segment ac = new Segment(a, b);
		Segment ad = new Segment(a, b);
		Segment bc = new Segment(a, b);
		Segment bd = new Segment(a, b);
		Segment cd = new Segment(a, b);
		
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
		
		Set<Point> tester = ImplicitPointPreprocessor.compute(pointsDatabase, segList);
		assertFalse(tester.isEmpty());
		assertEquals(1, tester.size());
		//assertTrue(tester.contains(new Point(4.0, 6.0)));
		Object[] arr = tester.toArray();
		//System.out.println(tester.size());
		System.out.println(arr[0].toString());
	}
}
