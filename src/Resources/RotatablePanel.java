package Resources;

import javax.swing.*;
import java.awt.*;


public class RotatablePanel extends JPanel {
    private double rotationDegrees = 0;

    public RotatablePanel() {
        super(null); // Use null layout as before
        setOpaque(false);
    }

    public void setRotation(double degrees) {
        this.rotationDegrees = degrees;
        repaint();
    }

    public double getRotation() {
        return rotationDegrees;
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g.create();

        // Rotate around the center of the panel
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        g2d.rotate(Math.toRadians(rotationDegrees), cx, cy);

        super.paintComponent(g2d);
        g2d.dispose();
    }
}
