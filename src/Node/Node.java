package Node;

import Util.*;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;


public class Node {
    public JPanel panel;
    protected Node parent;
    protected List<Node> children = new ArrayList<>();

    // Constructor with default opaque = false
    public Node(int[] size, int[] relativePosition) {
        panel = new JPanel(null);
        panel.setBounds(relativePosition[0], relativePosition[1], size[0], size[1]);
        panel.setOpaque(false);
    }

    // Overloaded constructor with background color and opaque flag
    public Node(int[] size, int[] relativePosition, Color backgroundColor, boolean opaque) {
        this(size, relativePosition);
        panel.setBackground(backgroundColor);
        panel.setOpaque(opaque);
    }

    public Node() {}

    public JPanel getPanel() {
        return panel;
    }

    public Node getParent() {
        return parent;
    }

    public void addChild(Node child) {
        child.parent = this;
        children.add(child);
        panel.add(child.getPanel());

        // Automatically bring the newest child to the front (render last)
        panel.setComponentZOrder(child.getPanel(), 0);

        panel.repaint();
        panel.revalidate();
    }

    public void removeChild(Node child) {
        if (child == null) return;
        // Safely detach
        children.remove(child);
        panel.remove(child.getPanel());

        GameWindow.MainWindow.repaint();

        System.gc();
    }

    public void removeFromParent() {
        if (parent != null) {
            Node p = parent; // store parent reference
            parent = null;   // prevent infinite recursion
            p.removeChild(this);
        }
    }

    // Recursively destroy this node and all its descendants.
    public void destroy() {
        // Destroy all children safely
        for (Node child : new ArrayList<>(children)) {
            child.destroy();
        }
        children.clear();

        // Detach from parent if still attached
        if (parent != null) {
            Node p = parent;
            parent = null;  // prevent recursion
            p.removeChild(this);
        }

        // Remove Swing components
        if (panel != null) {
            panel.removeAll();
            Container container = panel.getParent();
            if (container != null) container.remove(panel);
            panel = null;
        }
    }

    public void moveNodeToTop() {
        Node parentNode = getParent();
        parentNode.setChildZIndex(this,0);
    }

    public void setChildZIndex(Node child, int zIndex) {
        if (!children.contains(child)) return;

        int maxIndex = panel.getComponentCount() - 1;

        // Clamp the zIndex to a valid range
        zIndex = Math.max(0, Math.min(zIndex, maxIndex));

        // Swing's Z-order: 0 = top, higher = back
        panel.setComponentZOrder(child.getPanel(), zIndex);

        panel.repaint();
        panel.revalidate();
    }

    public int getChildCount() {
        return children.size();
    }

    public List<Node> getAllChildren() {
        return children;
    }

    public void setVisibility(Boolean state) {
        panel.setVisible(state);
    }

    public boolean isVisible() {
        return panel.isVisible();
    }

    public void setPosition(int[] position) {
        if (panel != null) {
            panel.setLocation(position[0], position[1]);
        }
    }

    public int[] getPosition() {
        if (panel == null) {
            return new int[]{-1000, -1000};
        }
        return new int[]{panel.getLocation().x, panel.getLocation().y};
    }

    public int[] getGlobalPosition() {
        int[] pos = getPosition();
        Node parent = getParent();
        while (parent != null) {
            int[] parentPos = parent.getPosition();
            pos[0] += parentPos[0];
            pos[1] += parentPos[1];
            parent = parent.parent;
        }
        return pos;
    }

    public int[] getRelativePosition() {
        return new int[]{panel.getX(), panel.getY()};
    }

    public void setSize(int width, int height) {
        panel.setSize(width, height);
    }

    public int[] getSize() {
        return new int[]{panel.getSize().width, panel.getSize().height};
    }

    public Color getBackgroundColor() {
        return panel.getBackground();
    }

    public void setBackgroundColor(Color color) {
        panel.setBackground(color);
        panel.setOpaque(true);
        panel.repaint();
    }
}
