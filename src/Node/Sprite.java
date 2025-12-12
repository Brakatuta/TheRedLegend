package Node;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;


public class Sprite extends Node {
    private JLabel imageLabel;
    final List<BufferedImage> frames = new ArrayList<>();
    private int currentFrame = 0;
    private Timer animationTimer;

    public Boolean mirrorSprite = true; // true = normal orientation; false = mirrored
    private double rotationAngle = 0.0; // rotation in degrees

    public Sprite(String[] imagePaths, int[] size, int[] offset, int[] relativePosition, int delayInMS, boolean loopAnimation) {
        super(size, relativePosition, Color.white, false);

        switchToAnimation(imagePaths, size, offset, delayInMS, loopAnimation);
    }

    public void switchToAnimation(String[] imagePaths, int[] size, int[] offset, int delayInMS, boolean loopAnimation) {
        // Stop existing timer
        if (animationTimer != null && animationTimer.isRunning()) {
            animationTimer.stop();
        }

        // Reset current frame
        currentFrame = 0;

        // Remove old image label
        if (imageLabel != null && panel != null) {
            panel.remove(imageLabel);
            panel.revalidate();
            panel.repaint();
        }

        // Clear old frames and load new ones
        frames.clear();
        loadFrames(imagePaths, size);

        if (frames.isEmpty()) {
            System.err.println("No frames were loaded.");
            return;
        }

        // Create and add new image label
        imageLabel = new JLabel(new ImageIcon(frames.get(0)));
        imageLabel.setBounds(offset[0], offset[1], size[0], size[1]);

        // Compute total bounds needed
        int totalWidth = Math.max(size[0], size[0] + Math.abs(offset[0]));
        int totalHeight = Math.max(size[1], size[1] + Math.abs(offset[1]));

        // Shift panel position if offset is negative
        int panelX = Math.min(0, offset[0]);
        int panelY = Math.min(0, offset[1]);

        panel.setBounds(panelX, panelY, totalWidth, totalHeight);
        panel.setPreferredSize(new Dimension(totalWidth, totalHeight));

        panel.add(imageLabel);
        panel.setOpaque(false);

        // Setup animation timer
        animationTimer = new Timer(delayInMS, e -> {
            currentFrame++;
            if (currentFrame >= frames.size()) {
                if (loopAnimation) {
                    currentFrame = 0;
                } else {
                    animationTimer.stop();
                    return;
                }
            }

            // check if sprite is null to prevent errors when node gets removed
            if (imageLabel != null && imageLabel.getParent() != null) {
                updateImageAndRotation();
            }
        });
        animationTimer.start();

        panel.revalidate();
        panel.repaint();
    }

    // load Frames threaded
    private void loadFrames(String[] imagePaths, int[] size) {
        BufferedImage[] tempFrames = new BufferedImage[imagePaths.length];
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < imagePaths.length; i++) {
            final int index = i;
            final String path = imagePaths[i];

            Thread t = new Thread(() -> {
                try {
                    BufferedImage img = ImageIO.read(new File(path));
                    if (img != null) {
                        Image scaled = img.getScaledInstance(size[0], size[1], Image.SCALE_SMOOTH);
                        BufferedImage bufferedScaled = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
                        Graphics2D g2d = bufferedScaled.createGraphics();

                        if (mirrorSprite != null && !mirrorSprite) {
                            g2d.drawImage(scaled, size[0], 0, -size[0], size[1], null);
                        } else {
                            g2d.drawImage(scaled, 0, 0, null);
                        }
                        g2d.dispose();

                        tempFrames[index] = bufferedScaled;
                    } else {
                        System.err.println("Image failed to load: " + path);
                    }
                } catch (IOException e) {
                    System.err.println("Error loading image: " + path);
                }
            });

            threads.add(t);
            t.start();
        }

        // Wait for all threads to complete
        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        // Preserve the original order
        frames.clear();
        for (BufferedImage frame : tempFrames) {
            if (frame != null) frames.add(frame);
        }
    }

    // New method to set rotation
    public void setRotation(double angleDegrees) {
        rotationAngle = angleDegrees;
        updateImageAndRotation();
    }

    // Helper method to update imageLabel icon with rotated current frame
    private void updateImageAndRotation() {
        if (frames.isEmpty() || imageLabel == null) return;

        BufferedImage original = frames.get(currentFrame);
        BufferedImage rotated = rotateImage(original, rotationAngle);
        imageLabel.setIcon(new ImageIcon(rotated));
    }

    // Rotates the given BufferedImage by angleDegrees and returns a new BufferedImage
    private BufferedImage rotateImage(BufferedImage src, double angleDegrees) {
        double radians = Math.toRadians(angleDegrees);
        int srcWidth = src.getWidth();
        int srcHeight = src.getHeight();

        // Calculate the size of the rotated image
        double sin = Math.abs(Math.sin(radians));
        double cos = Math.abs(Math.cos(radians));
        int newWidth = (int) Math.floor(srcWidth * cos + srcHeight * sin);
        int newHeight = (int) Math.floor(srcHeight * cos + srcWidth * sin);

        BufferedImage rotated = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = rotated.createGraphics();

        // Enable smooth rendering
        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);

        // Translate and rotate around the center
        g2d.translate((newWidth - srcWidth) / 2, (newHeight - srcHeight) / 2);
        g2d.rotate(radians, srcWidth / 2.0, srcHeight / 2.0);

        // Draw original image
        g2d.drawImage(src, 0, 0, null);
        g2d.dispose();

        return rotated;
    }

    public void mirrorImage(boolean mirror) {
        // Only do work if the state actually changes
        mirror = !mirror;
        if (mirrorSprite != mirror) {
            mirrorSprite = mirror;

            List<BufferedImage> mirroredFrames = new ArrayList<>();

            for (BufferedImage frame : frames) {
                mirroredFrames.add(flipImage(frame));
            }

            frames.clear();
            frames.addAll(mirroredFrames);

            // Update the visible frame immediately
            updateImageAndRotation();
        }
    }

    private BufferedImage flipImage(BufferedImage src) {
        int width = src.getWidth();
        int height = src.getHeight();

        BufferedImage mirrored = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = mirrored.createGraphics();

        // Draw horizontally flipped image
        g2d.drawImage(src, width, 0, -width, height, null);
        g2d.dispose();

        return mirrored;
    }

    private void dispose() {
        if (animationTimer != null) {
            animationTimer.stop();
            animationTimer = null;
        }

        if (imageLabel != null && imageLabel.getParent() != null) {
            panel.remove(imageLabel);
            panel.revalidate();
            panel.repaint();
        }

        imageLabel = null;
        frames.clear();
    }

    public void removeFromParent() {
        dispose();
        if (parent != null) {
            Node p = parent;
            parent = null;
            p.removeChild(this);
        }
    }
}