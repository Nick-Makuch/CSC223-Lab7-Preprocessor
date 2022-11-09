package geometry_objects;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import org.junit.jupiter.api.Test;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.InputFacade;
import input.components.FigureNode;
import preprocessor.delegates.ImplicitPointPreprocessor;

public class SegmentTest {

	@Test
	void HasSubSegmentTest() {
		//
		//  A -- B -- C
		//   
		Point A = new Point("A", 0, 0);
		Point B = new Point("B", 0, 3);
		Point C = new Point("C", 0, 5);
		Segment AB = new Segment(A, B);
		Segment AC = new Segment(A, C);
		Segment BC = new Segment(B, C);
		
		assertFalse(AB.HasSubSegment(BC));
		assertTrue(AC.HasSubSegment(BC));
		assertTrue(AC.HasSubSegment(AB));
		assertTrue(AC.HasSubSegment(AC));
		assertTrue(AC.HasSubSegment(BC));
		assertFalse(BC.HasSubSegment(AC));	
		
		//   A
		//   |
		//   |
		//   B
		//   |
		//   C
		// 
		B = new Point("B", 0,3);
		C = new Point("C", 0, 5);
		AB = new Segment(A, B);
		AC = new Segment(A, C);
		BC = new Segment(B, C);
		
		assertFalse(AB.HasSubSegment(AC));
		assertFalse(BC.HasSubSegment(AC));
		assertTrue(AB.HasSubSegment(AB));
		assertTrue(AC.HasSubSegment(BC));
		assertTrue(AC.HasSubSegment(AB));
		
		
		// A
		//  \
		//   \
		//    B
		//     \
		//      \
		//       C
		B = new Point("B", 2, 2);
		C = new Point("C", 4, 4);
		AB = new Segment(A, B);
		AC = new Segment(A, C);
		BC = new Segment(B, C);
		
		assertTrue(AB.HasSubSegment(AB));
		assertFalse(AB.HasSubSegment(AC));
		assertTrue(AC.HasSubSegment(BC));
		assertTrue(AC.HasSubSegment(AB));
		assertTrue(AC.HasSubSegment(AC));
	}
	
	@Test
	void CoincideWithoutOverlapTest() {
		//
		//  A -- B -- C -- D
		//   
		Point A = new Point("A", 0, 0);
		Point B = new Point("B", 0, 3);
		Point C = new Point("C", 0, 5);
		Point D = new Point("D", 0, 7);
		Segment AD = new Segment(A, D);
		Segment CD = new Segment(C, D);
		Segment BC = new Segment(B, C);
		Segment AB = new Segment(A, B);
		Segment BD = new Segment(B, D);
		
	
		assertFalse(AD.coincideWithoutOverlap(BC));
		assertTrue(AB.coincideWithoutOverlap(CD));
		assertTrue(CD.coincideWithoutOverlap(AB));
		assertTrue(AB.coincideWithoutOverlap(BD));

		//
		//	A -- B -- C -- D
		//  |
		//  |
		//  B2
		//  |
		//  C2
		//
		Point A2 = new Point("A", 0, 0);
		Point B2 = new Point("B", 0, 3);
		Point C2 = new Point("C", 0, 5);
		Segment AB2 = new Segment(A2, B2);
		Segment AC2 = new Segment(A2, C2);
		Segment BC2 = new Segment(B2, C2);
		
		assertFalse(AB2.coincideWithoutOverlap(AC2));
		assertFalse(AC2.coincideWithoutOverlap(AC2));
		assertTrue(AB2.coincideWithoutOverlap(BC2));
		assertTrue(BC2.coincideWithoutOverlap(AB2));
		assertFalse(AB2.coincideWithoutOverlap(AB));
		
		//   A
		//    \
		//     \
		//      B3
		//       \
		//        C3
		//         
		//   D--------E
		//       
		Point B3 = new Point("B", 2, 2);
		Point C3 = new Point("C", 4, 4);
		Point D3 = new Point("D", 0, 6);
		Point E3 = new Point("E", 7, 6);
		Segment AB3 = new Segment(A, B3);
		Segment AC3 = new Segment(A, C3);
		Segment BC3 = new Segment(B3, C3);
		Segment DE = new Segment(D3, E3);
		
		assertTrue(AB3.coincideWithoutOverlap(BC3));
		assertTrue(BC3.coincideWithoutOverlap(AB3));
		assertFalse(AC3.coincideWithoutOverlap(DE));
	}
	
	@Test
	void collectOrderedPointsOnSegmentsTest() {
		//
		// A
		//  \
		//   \
		//    B ----- E
		//     \ 
		//      C
		//       \
		//        D
		Point A = new Point(0,0);
		Point B = new Point(3, 3);
		Point C = new Point(5, 5);
		Point D = new Point(7, 7);
		Point E = new Point(7, 3);
		Segment AD = new Segment(A,D);
		Set<Point> ptSet = new TreeSet<Point>();
		ArrayList<Point> list = new ArrayList<Point>(Arrays.asList(A,B,C,D,E));
		ptSet.addAll(list);
		ptSet = AD.collectOrderedPointsOnSegment(ptSet);
		ArrayList<Point> listCon = new ArrayList<Point>(Arrays.asList(B,C));
		assertTrue(ptSet.containsAll(listCon));
	}
}