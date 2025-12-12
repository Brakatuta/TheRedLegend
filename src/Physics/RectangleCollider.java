package Physics;

import Node.Node;
import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;


public class RectangleCollider extends Node {
    final int Group;
    final int[] rect_size;
    private int[] rect_position;
    private Polygon polygon;

    public RectangleCollider(int[] size, int[] position, int collisionGroup, Boolean drawDebug) {
        super(size, position, new Color(0xA927600D, true), drawDebug);

        Group = collisionGroup;
        rect_size = size;
        rect_position = getGlobalPosition();

        updatePolygon(new int[]{-1000, -1000}, new int[]{-1000, -1000});
    }

    // overwrite the rect position if needed (if x -1 and y -1 then ignore)
    public void updatePolygon(int[] overwrite_rect_position, int[] overwriteSize) {
        if (overwrite_rect_position[0] > -500) {
            rect_position = overwrite_rect_position;
        }

        // Create rectangle polygon based on position and size
        List<Point> points = new ArrayList<>();

        if (overwriteSize[0] > -500) {
            points.add(new Point(rect_position[0], rect_position[1]));
            points.add(new Point(rect_position[0] + overwriteSize[0], rect_position[1]));
            points.add(new Point(rect_position[0] + overwriteSize[0], rect_position[1] + overwriteSize[1]));
            points.add(new Point(rect_position[0], rect_position[1] + overwriteSize[1]));

            setSize(overwriteSize[0], overwriteSize[1]);
        } else {
            points.add(new Point(rect_position[0], rect_position[1]));
            points.add(new Point(rect_position[0] + rect_size[0], rect_position[1]));
            points.add(new Point(rect_position[0] + rect_size[0], rect_position[1] + rect_size[1]));
            points.add(new Point(rect_position[0], rect_position[1] + rect_size[1]));

            setSize(rect_size[0], rect_size[1]);
        }

        polygon = new Polygon(points);

        // Add polygon to collision handler
        CollisionsHandler.addPolygonToGroup(Group, polygon);
    }

    public void setEnabled(boolean state) {
        if (state) {
            readdPolygonToCollisionGroup();
        } else {
            removePolygonFromCollisionGroup();
        }
    }

    public void removePolygonFromCollisionGroup() {
        CollisionsHandler.removePolygonFromGroup(Group, polygon);
        setVisibility(false);
    }

    public void readdPolygonToCollisionGroup() {
        CollisionsHandler.addPolygonToGroup(Group, polygon);
        setVisibility(true);
    }

    @Override
    public void removeFromParent() {
        removePolygonFromCollisionGroup();
        if (parent != null) {
            parent.removeChild(this);
            parent = null;
        }
    }

    public Polygon getPolygon() {
        return polygon;
    }
}
