package preprocessor;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.jupiter.api.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.InputFacade;
import input.components.FigureNode;
import preprocessor.delegates.ImplicitPointPreprocessor;

class PreprocessorTest
{
	@Test
	void test_implicit_crossings()
	{
		FigureNode fig = InputFacade.extractFigure("fully_connected_irregular_polygon.json");
		String s = "fully_connected_irregular_polygon.json";

		Map.Entry<PointDatabase, Set<Segment>> pair = InputFacade.toGeometryRepresentation(s);

		PointDatabase points = pair.getKey();

		Set<Segment> segments = pair.getValue();

		Preprocessor pp = new Preprocessor(points, segments);

		// 5 new implied points inside the pentagon
		Set<Point> iPoints = ImplicitPointPreprocessor.compute(points, new ArrayList<Segment>(segments));
		assertEquals(5, iPoints.size());

		System.out.println(iPoints);

		//
		//
		//		               D(3, 7)
		//
		//
		//   E(-2,4)       D*      E*
		//		         C*          A*       C(6, 3)
		//                      B*
		//		       A(2,0)        B(4, 0)
		//
		//		    An irregular pentagon with 5 C 2 = 10 segments

		Point a_star = new Point(56.0 / 15, 28.0 / 15);
		Point b_star = new Point(16.0 / 7, 8.0 / 7);
		Point c_star = new Point(8.0 / 9, 56.0 / 27);
		Point d_star = new Point(90.0 / 59, 210.0 / 59);
		Point e_star = new Point(194.0 / 55, 182.0 / 55);
		
		assertTrue(iPoints.contains(a_star));
//		assertTrue(iPoints.contains(b_star));
//		assertTrue(iPoints.contains(c_star));
		assertTrue(iPoints.contains(d_star));
		assertTrue(iPoints.contains(e_star));

		//
		// There are 15 implied segments inside the pentagon; see figure above
		//
		Set<Segment> iSegments = pp.computeImplicitBaseSegments(iPoints);
		assertEquals(15, iSegments.size());

		List<Segment> expectedISegments = new ArrayList<Segment>();

		expectedISegments.add(new Segment(points.getPoint("A"), c_star));
		expectedISegments.add(new Segment(points.getPoint("A"), b_star));

		expectedISegments.add(new Segment(points.getPoint("B"), b_star));
		expectedISegments.add(new Segment(points.getPoint("B"), a_star));

		expectedISegments.add(new Segment(points.getPoint("C"), a_star));
		expectedISegments.add(new Segment(points.getPoint("C"), e_star));

		expectedISegments.add(new Segment(points.getPoint("D"), d_star));
		expectedISegments.add(new Segment(points.getPoint("D"), e_star));

		expectedISegments.add(new Segment(points.getPoint("E"), c_star));
		expectedISegments.add(new Segment(points.getPoint("E"), d_star));

		expectedISegments.add(new Segment(c_star, b_star));
		expectedISegments.add(new Segment(b_star, a_star));
		expectedISegments.add(new Segment(a_star, e_star));
		expectedISegments.add(new Segment(e_star, d_star));
		expectedISegments.add(new Segment(d_star, c_star));

		for (Segment iSegment : iSegments)
		{
			assertTrue(iSegments.contains(iSegment));
		}

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedMinimalSegments = new ArrayList<Segment>(iSegments);
		expectedMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("B")));
		expectedMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("C")));
		expectedMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("D")));
		expectedMinimalSegments.add(new Segment(points.getPoint("D"), points.getPoint("E")));
		expectedMinimalSegments.add(new Segment(points.getPoint("E"), points.getPoint("A")));
		
		Set<Segment> minimalSegments = pp.identifyAllMinimalSegments(iPoints, segments, iSegments);
		assertEquals(expectedMinimalSegments.size(), minimalSegments.size());

		for (Segment minimalSeg : minimalSegments)
		{
			assertTrue(expectedMinimalSegments.contains(minimalSeg));
		}
		
		//
		// Construct ALL figure segments from the base segments
		//
		Set<Segment> computedNonMinimalSegments = pp.constructAllNonMinimalSegments(minimalSegments);
		
		//
		// All Segments will consist of the new 15 non-minimal segments.
		//
		assertEquals(15, computedNonMinimalSegments.size());

		//
		// Ensure we have ALL minimal segments: 20 in this figure.
		//
		List<Segment> expectedNonMinimalSegments = new ArrayList<Segment>();
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("D")));
		
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), c_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("E")));
		
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), d_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("E"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), points.getPoint("E")));		

		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("C"), b_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("A"), points.getPoint("C")));
		
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), e_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("D"), a_star));
		expectedNonMinimalSegments.add(new Segment(points.getPoint("B"), points.getPoint("D")));
		
		//
		// Check size and content equality
		//
		assertEquals(expectedNonMinimalSegments.size(), computedNonMinimalSegments.size());

		for (Segment computedNonMinimalSegment : computedNonMinimalSegments)
		{
			assertTrue(expectedNonMinimalSegments.contains(computedNonMinimalSegment));
		}
	}
	
	@Test
	void testComputeImplicitBaseSegments() {
		//
		//          D
		//		   /   
		//   A ---/------- B
		//       /
		//    E-/-----F
		//	   / 
		//    C
		//
		Point A = new Point("A", 0,2);
		Point B = new Point("B", 6, 2);
		Point C = new Point("C", 1, 5);
		Point D = new Point("D",  3, 0);
		Point E = new Point("E", 1, 3);
		Point F = new Point("F", 6, 3);
		Point E_star = new Point("*_E", 9.0/5, 3);
		Point A_star = new Point("*_A", 13.0/5, 3);
		
	
		List<Point> ptList = new ArrayList<Point>();
		ptList.add(A);
		ptList.add(B);
		ptList.add(C);
		ptList.add(D);
		ptList.add(E);
		ptList.add(F);
		PointDatabase ptdb = new PointDatabase(ptList);
		
		Segment AB = new Segment(A, B);
		Segment EF = new Segment(E, F);
		Segment DC = new Segment(D, C);
		Set<Segment> segSet = new HashSet<Segment>();
		segSet.add(AB);
		segSet.add(EF);
		segSet.add(DC);
		
		Set<Point> impPoints = new HashSet<Point>();
		impPoints.add(E_star);
		impPoints.add(A_star);
		
		Segment AA_star = new Segment(A, E_star);
		Segment BA_star = new Segment(B, A_star);
		Segment DA_star = new Segment(D, A_star);
		Segment CE_star = new Segment(C, E_star);
		Segment EE_star = new Segment(E, E_star);
		Segment FE_star = new Segment(F, E_star);
	
		Set<Segment> expImpSeg = new HashSet<Segment>();
		expImpSeg.add(FE_star);
		expImpSeg.add(EE_star);
		expImpSeg.add(CE_star);
		expImpSeg.add(DA_star);
		expImpSeg.add(BA_star);
		expImpSeg.add(AA_star);
		
		
		
		
		Preprocessor pp = new Preprocessor(ptdb, segSet);
		Set<Segment> impSeg = pp.computeImplicitBaseSegments(impPoints);
		
		assertTrue(impSeg.contains(expImpSeg));
		
	}
	
	
	@Test
	void testConstructAllNonMinimalSegments_withTwoSegments() 
	{
		//
		//   A --- B --- C --- D
		//
		Point a = new Point(0,0);
		Point b = new Point(0,4);
		Point c = new Point(0,6);
		Point d = new Point(0,8);
		
		List<Point> ptList = new LinkedList<Point>();
		ptList.add(c);
		ptList.add(a);
		ptList.add(b);
		ptList.add(d);
		Segment ab = new Segment(a,b);
		Segment bc = new Segment(b,c);
		Segment cd = new Segment(c,d);
		
		
		Set<Segment> minSeg = new HashSet<Segment>();
		minSeg.add(bc);
		minSeg.add(ab);
		minSeg.add(cd);
		PointDatabase ptdb = new PointDatabase(ptList);
		Preprocessor pp = new Preprocessor(ptdb, minSeg);
		Set<Segment> nonMinSeg = pp.constructAllNonMinimalSegments(minSeg);
		
		
		assertTrue(nonMinSeg.contains(new Segment(a,c)));
		assertTrue(nonMinSeg.contains(new Segment(a,d)));
		assertTrue(nonMinSeg.contains(new Segment(b,d)));
		
		
	}
}