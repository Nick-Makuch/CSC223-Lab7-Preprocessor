package preprocessor;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.SortedSet;

import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import preprocessor.delegates.ImplicitPointPreprocessor;
import geometry_objects.Segment;
import geometry_objects.delegates.SegmentDelegate;

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
	 * looks at each segment and checks if there are any implicit points
	 * on the segment. If there are, then we create the implicit segments 
	 * related to the point and add it to the set that is being returned.   
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
			SortedSet<Point> pointsOnSegment = s.collectOrderedPointsOnSegment(_implicitPoints2);
			// checks if there are any implicit points on the segment
			for (Point p : pointsOnSegment) {
				// adds the two segments that is connected with the endpoints
				Segment newS1 = new Segment(s.getPoint1(), p);
				impSeg.add(newS1);
				Segment newS2 = new Segment(s.getPoint2(), p);
				impSeg.add(newS2);
				// if there are multiple implicit points on the segment, 
				// then we create segments between the implicit points as well
				for (Point p2 : pointsOnSegment) {
					if (!p2.equals(p) && !impSeg.contains(new Segment(p, p2))) {
						Segment newS3 = new Segment(p, p2);
						impSeg.add(newS3);
					}
				}
			}
		}
		return impSeg;
	}

	
	protected Set<Segment> identifyAllMinimalSegments(Set<Point> _implicitPoints2, Set<Segment> _givenSegments2)																			Set<Segment> _implicitSegments2) 
	{
		return null;
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
		for (Segment s1 : _allMinimalSegments2) {
			for (Segment s2: _allMinimalSegments2) {
				// if they are collinear and they segments are the same
				if (!s1.equals(s2) && s1.coincideWithoutOverlap(s2)) {
					// if they share a vertex, then proceed
					if (s1.sharedVertex(s2) != null) {
						Segment newS = null;
						Point sharedP = s1.sharedVertex(s2);
						// determines which which points that make up the segment
						if (sharedP.equals(s1.getPoint1()) && sharedP.equals(s2.getPoint1())) { 
							newS = new Segment(s1.getPoint2(), s2.getPoint2());
						}
						else if (sharedP.equals(s1.getPoint1()) && sharedP.equals(s2.getPoint2())) {
							newS = new Segment(s1.getPoint2(), s2.getPoint1());
						}
						else if (sharedP.equals(s1.getPoint2()) && sharedP.equals(s2.getPoint1())) {
							newS = new Segment(s1.getPoint1(), s2.getPoint2());
						}
						else {
							newS = new Segment(s1.getPoint1(), s2.getPoint1());
						}
						nonMinSeg.add(newS);
					}
				}
			}
		}
		return nonMinSeg;
	}
	
}
