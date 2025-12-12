package Physics;

import java.awt.*;
import java.awt.geom.Area;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.List;


public class Polygon {
    private final List<Point> points;

    public Polygon(List<Point> points) {
        this.points = new ArrayList<>(points);
    }

    public List<Point> getPoints() {
        return points;
    }

    /**
     * Converts this polygon to a java.awt.Polygon object
     */
    private java.awt.Polygon toAWTPolygon() {
        int[] xPoints = points.stream().mapToInt(p -> p.x).toArray();
        int[] yPoints = points.stream().mapToInt(p -> p.y).toArray();
        return new java.awt.Polygon(xPoints, yPoints, points.size());
    }

    /**
     * Checks if this polygon overlaps with another polygon
     * and returns an approximate point inside the overlapping region, or null if no overlap
     */
    public Point getOverlapPoint(Polygon other) {
        Area areaA = new Area(this.toPath2D());
        Area areaB = new Area(other.toPath2D());

        areaA.intersect(areaB);

        if (!areaA.isEmpty()) {
            Rectangle bounds = areaA.getBounds();
            // Approximate center of overlap
            return new Point(bounds.x + bounds.width / 2, bounds.y + bounds.height / 2);
        } else {
            return null;
        }
    }

    public boolean overlapsWith(Polygon other) {
        return getOverlapPoint(other) != null;
    }

    /**
     * Converts this polygon to a Path2D (used for Area operations)
     */
    private Path2D toPath2D() {
        Path2D path = new Path2D.Double();
        if (points.isEmpty()) return path;

        Point first = points.get(0);
        path.moveTo(first.x, first.y);

        for (int i = 1; i < points.size(); i++) {
            Point p = points.get(i);
            path.lineTo(p.x, p.y);
        }

        path.closePath();
        return path;
    }
}