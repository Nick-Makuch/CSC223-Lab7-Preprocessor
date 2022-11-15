package preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;
import java.util.SortedSet;

import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import preprocessor.delegates.ImplicitPointPreprocessor;
import geometry_objects.Segment;

public class Preprocessor
{
	// The explicit points provided to us by the user.
	// This database will also be modified to include the implicit
	// points (i.e., all points in the figure).
	protected PointDatabase _pointDatabase;

	// Minimal ('Base') segments provided by the user
	protected Set<Segment> _givenSegments;

	// The set of implicitly defined points caused by segments
	// at implicit points.
	protected Set<Point> _implicitPoints;

	// The set of implicitly defined segments resulting from implicit points.
	protected Set<Segment> _implicitSegments;

	// Given all explicit and implicit points, we have a set of
	// segments that contain no other subsegments; these are minimal ('base') segments
	// That is, minimal segments uniquely define the figure.
	protected Set<Segment> _allMinimalSegments;

	// A collection of non-basic segments
	protected Set<Segment> _nonMinimalSegments;

	// A collection of all possible segments: maximal, minimal, and everything in between
	// For lookup capability, we use a map; each <key, value> has the same segment object
	// That is, key == value. 
	protected Map<Segment, Segment> _segmentDatabase;
	public Map<Segment, Segment> getAllSegments() { return _segmentDatabase; }

	public Preprocessor(PointDatabase points, Set<Segment> segments)
	{
		_pointDatabase  = points;
		_givenSegments = segments;
		
		_segmentDatabase = new HashMap<Segment, Segment>();
		
		analyze();
	}

	/**
	 * Invoke the precomputation procedure.
	 */
	public void analyze()
	{
		//
		// Implicit Points
		//
		_implicitPoints = ImplicitPointPreprocessor.compute(_pointDatabase, _givenSegments.stream().toList());

		//
		// Implicit Segments attributed to implicit points
		//
		_implicitSegments = computeImplicitBaseSegments(_implicitPoints);

		//
		// Combine the given minimal segments and implicit segments into a true set of minimal segments
		//     *givenSegments may not be minimal
		//     * implicitSegmen
		//
		_allMinimalSegments = identifyAllMinimalSegments(_implicitPoints, _givenSegments, _implicitSegments);

		//
		// Construct all segments inductively from the base segments
		//
		_nonMinimalSegments = constructAllNonMinimalSegments(_allMinimalSegments);

		//
		// Combine minimal and non-minimal into one package: our database
		//
		_allMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
		_nonMinimalSegments.forEach((segment) -> _segmentDatabase.put(segment, segment));
	}

	/**
	 * computes the implicit segments given the implicit points. 
	 *
	 * 
	 * @param _implicitPoints2 -- implicit points
	 * @return impSeg -- a set of all implicit segments
	 */
	protected Set<Segment> computeImplicitBaseSegments(Set<Point> _implicitPoints2) 
	{
		Set<Segment> impSeg = new HashSet<Segment>();
		// looks at each segment
		for (Segment s : _givenSegments) {
			
			// gets the set of imp points on the segment and converts it to a list
			SortedSet<Point> pointsOnSegSet = s.collectOrderedPointsOnSegment(_implicitPoints2);
			Iterator<Point> itrPoint = pointsOnSegSet.iterator();
			
			Segment newS = null;
			Point currP = null;
			
			// determines which end point is lower
			int cmp = s.getPoint1().compareTo(s.getPoint2());

			// imp points are sorted from low to high
			// so we start with the lower endpoint
			if (cmp < 0) {
				currP = s.getPoint1();
			}
			else if (cmp > 0){
				currP = s.getPoint2();
			}
			// create segments from the endpoints to closest imp points
			while (itrPoint.hasNext()) {
				newS = new Segment(currP, itrPoint.next());
				impSeg.add(newS);
				currP = newS.getPoint2();
			}
			// if there was an implicit point, then connect last imp
			// point with the remaining endpoint
			if (s.pointLiesBetweenEndpoints(currP) && (cmp < 0)) {
				newS = new Segment(currP, s.getPoint2());
				impSeg.add(newS);
			}
			else if (s.pointLiesBetweenEndpoints(currP) && (cmp > 0)) {
				newS = new Segment(currP, s.getPoint1());
				impSeg.add(newS);
			}
		}
		return impSeg;	
	}

	/**
	 * loops through each segment and checks if it is a min segment
	 * by checking if there's an implicit point on the segment
	 * 
	 * @param _implicitPoints2
	 * @param _givenSegments2
	 * @param _implicitSegments2
	 * @return allMinSegments -- set containing all min segments
	 */
	protected Set<Segment> identifyAllMinimalSegments(Set<Point> _implicitPoints2, Set<Segment> _givenSegments2, 
																					Set<Segment> _implicitSegments2) 
	{
		// create boolean -> take one segment from givenSeg -> loop through impPoints -> if boolean is true after the  
		Set<Segment> allMinSegments = new LinkedHashSet<Segment>();
		boolean min = true;
		// loop through the segments to check if there's a imp point on given seg
		for (Segment s : _givenSegments) {
			for (Point p : _implicitPoints2) {
				// if point lies on segment, set boolean to false
				if (s.pointLiesBetweenEndpoints(p)) {
					min = false;
					// add imp seg containing p to set
					for (Segment impS : _implicitSegments2) {
						if (impS.has(p)) { 
							allMinSegments.add(impS);
						}
					}
				}
			}
			// if segment does not have any imp points, then add to set
			if (min) allMinSegments.add(s);
			min = true;
		}
		return allMinSegments;
	}
	
	/**
	 * checks every segment if there is a connected segment
	 * 
	 * @param _allMinimalSegments2
	 * @return
	 */
	protected Set<Segment> constructAllNonMinimalSegments(Set<Segment> _allMinimalSegments2) 
	{
		Set<Segment> nonMinSeg = new HashSet<Segment>();
		// create a queue and add all minimal segments
		Queue<Segment> q = new LinkedList<Segment>();
		q.addAll(_allMinimalSegments2);
		while (!q.isEmpty()) {
			// check each segment if it's a subsegment of another
			Segment s1 = q.remove();
			for (Segment s2 : _allMinimalSegments2) {
				// if the segments are collinear, share a vertex and are not the same
				Point sharedP = s1.sharedVertex(s2);
				if (sharedP != null && s1.coincideWithoutOverlap(s2)) {
					Segment newS = new Segment(s1.other(sharedP), s2.other(sharedP));
					// add the segment to queue and to set
					q.add(newS);
					nonMinSeg.add(newS);
				}
			}
		}
		return nonMinSeg;
	}
}
