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

	protected Set<Segment> computeImplicitBaseSegments(Set<Point> _implicitPoints2) 
	{
		// get the set of points that are on a specific segment
		// if there's an implicit point, then create a new segment
		// from the implicit point to the edge or if there's 
		// another implicit point before the edge
		for (Segment s : _givenSegments) {
			SortedSet<Point> points = s.collectOrderedPointsOnSegment(_implicitPoints2);
			for (Point p : points) {
				// adds the two segments that is connected with the endpoints
				Segment newS1 = new Segment(s.getPoint1(), p);
				_implicitSegments.add(newS1);
				Segment newS2 = new Segment(s.getPoint2(), p);
				_implicitSegments.add(newS2);
				// creates segments between the implicit points
				for (Point p2 : points) {
					if (!p2.equals(p) && !_implicitSegments.contains(new Segment(p, p2))) {
						Segment newS3 = new Segment(p, p2);
						_implicitSegments.add(newS3);
					}
				}
			}
		}
		return null;
	}

	protected Set<Segment> identifyAllMinimalSegments(Set<Point> _implicitPoints2, Set<Segment> _givenSegments2,
																					Set<Segment> _implicitSegments2) 
	{
		// TODO Auto-generated method stub
		return null;
	}

	protected Set<Segment> constructAllNonMinimalSegments(Set<Segment> _allMinimalSegments2) 
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
