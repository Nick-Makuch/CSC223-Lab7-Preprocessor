package input;

import geometry_objects.Segment;
import geometry_objects.points.Point;
import geometry_objects.points.PointDatabase;
import input.builder.GeometryBuilder;
import input.components.FigureNode;
import input.components.point.PointNode;
import input.components.point.PointNodeDatabase;
import input.components.segment.SegmentNode;
import input.components.segment.SegmentNodeDatabase;
import input.parser.JSONParser;

import java.util.*;
import java.util.stream.Stream;

public class InputFacade {
    /**
     * Acquire a figure from the given JSON file.
     *
     * @param filename -- the name of a file
     * @return a FigureNode object corresponding to the input file.
     */
    public static FigureNode extractFigure(String filename) {
        String figureStr = utilities.io.FileUtilities.readFileFilterComments(filename);
        return (FigureNode) new JSONParser(new GeometryBuilder()).parse(figureStr);
    }

    /**
     * 1) Read in a figure from a JSON file.
     * 2) Convert the PointNode and SegmentNode objects to a Point and Segment objects
     * (those classes have more meaningful, geometric functionality).
     *
     * @param filename
     * @return a pair <set of points as a database, set of segments>
     */
    public static Map.Entry<PointDatabase, Set<Segment>> toGeometryRepresentation(String filename) {
        FigureNode node = extractFigure(filename);

        return figureToGeometry(node);

        // return new AbstractMap.SimpleEntry<>(new PointDatabase(node.getPointsDatabase().getPoints().stream().map((pointNode) -> new Point(pointNode.getName(), pointNode.getX(), pointNode.getY())).toList()), new HashSet<>(node.getSegments().asUniqueSegmentList().stream().map((segmentNode) -> new Segment(new Point(segmentNode.getPoint1().getName(), segmentNode.getPoint1().getX(), segmentNode.getPoint1().getY()), new Point(segmentNode.getPoint2().getName(), segmentNode.getPoint2().getX(), segmentNode.getPoint2().getY()))).toList()));
    }

    private static Map.Entry<PointDatabase, Set<Segment>> figureToGeometry(FigureNode figure) {
        PointNodeDatabase pointNodeDatabase = figure.getPointsDatabase();
        SegmentNodeDatabase segmentNodeDatabase = figure.getSegments();

        PointDatabase pointDatabase = InputFacade.pointDatabase(pointNodeDatabase);
        Set<Segment> segments =  InputFacade.segmentSet(segmentNodeDatabase);

        return new AbstractMap.SimpleEntry<>(pointDatabase, segments);
    }
    private static Point point(PointNode pointNode) {
        return new Point(pointNode.getName(), pointNode.getX(), pointNode.getY());
    }

    private static Segment segment(SegmentNode segmentNode) {
        Point point1 = InputFacade.point(segmentNode.getPoint1());
        Point point2 = InputFacade.point(segmentNode.getPoint2());

        return new Segment(point1, point2);
    }

    private static PointDatabase pointDatabase(PointNodeDatabase pointNodeDatabase) {
        Set<PointNode> pointNodes = pointNodeDatabase.getPoints();

        // Map the set of PointNodes to a list of Points
        Stream<PointNode> pointNodeStream = pointNodes.stream();
        List<Point> pointList = pointNodeStream.map(InputFacade::point).toList();

        return new PointDatabase(pointList);
    }
    private static Set<Segment> segmentSet(SegmentNodeDatabase segments) {
        List<SegmentNode> segmentNodes = segments.asUniqueSegmentList();

        // Map the list of SegmentNodes to a list of Segments
        Stream<SegmentNode> segmentNodeStream = segmentNodes.stream();
        List<Segment> segmentList = segmentNodeStream.map(InputFacade::segment).toList();

        // Return the list as a Set
        return new HashSet<>(segmentList);
    }
}
