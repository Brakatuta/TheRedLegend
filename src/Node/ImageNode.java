package Node;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;


public class ImageNode extends Node {
    public String pathToImage;

    private JLabel imageLabel;
    private BufferedImage originalImage;
    private BufferedImage displayedImage; // the possibly alpha-modified image

    public double currentAlphaLevel = 1.0;

    public ImageNode(String imagePath, int[] size, int[] relativePosition) {
        super(size, relativePosition, Color.white, false);

        pathToImage = imagePath;

        // Load and display image in background thread
        loadImage(imagePath, size);
    }

    // Helper method to load and scale the image
    private void loadImage(String imagePath, int[] size) {
        new Thread(() -> {
            try {
                BufferedImage img = ImageIO.read(new File(imagePath));
                if (img == null) {
                    System.err.println("Failed to load image: " + imagePath);
                    return;
                }

                // Store original image scaled to size
                BufferedImage scaledImage = new BufferedImage(size[0], size[1], BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2d = scaledImage.createGraphics();
                g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
                g2d.drawImage(img, 0, 0, size[0], size[1], null);
                g2d.dispose();

                // Assign the newly loaded and scaled image
                originalImage = scaledImage;

                // Update UI on Swing event thread
                SwingUtilities.invokeLater(() -> {
                    // Re-apply current alpha value to the new image
                    setAlphaValue(currentAlphaLevel);

                    if (imageLabel == null) {
                        // First time setup
                        JLabel label = new JLabel(new ImageIcon(displayedImage));
                        label.setBounds(0, 0, size[0], size[1]);
                        imageLabel = label;
                        panel.add(imageLabel);
                    } else {
                        // Update existing label
                        imageLabel.setIcon(new ImageIcon(displayedImage));
                    }

                    panel.revalidate();
                    panel.repaint();
                });

            } catch (IOException e) {
                System.err.println("Could not load image: " + imagePath);
                e.printStackTrace();
            }
        }).start();
    }

    // --- New Function to Swap Image ---

    /**
     * Loads a new image from the specified path, replaces the current image,
     * and preserves the current size and alpha level.
     *
     * @param newImagePath The path to the new image file.
     */
    public void swapImage(String newImagePath) {
        if (newImagePath == null || newImagePath.isEmpty()) {
            System.err.println("Swap failed: new image path cannot be null or empty.");
            return;
        }

        // 1. Update the image path reference
        this.pathToImage = newImagePath;

        // 2. Load the new image and update the display.
        // We reuse the existing size property.
        loadImage(newImagePath, getSize());

        // Note: The alpha value is automatically reapplied inside the loadImage helper function
        // using the call to setAlphaValue(currentAlphaLevel).
    }
    // ---------------------------------

    // Set opacity: alpha between 0.0 (fully transparent) and 1.0 (fully opaque)
    public void setAlphaValue(double alpha) {
        if (originalImage == null) return;

        // Update the internal tracker for the current alpha level
        currentAlphaLevel = alpha;

        alpha = Math.min(1.0, Math.max(0.0, alpha)); // clamp alpha

        // Create a new image with alpha applied
        BufferedImage transparentImage = new BufferedImage(originalImage.getWidth(), originalImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = transparentImage.createGraphics();

        // Clear background with transparent color
        g2d.setComposite(AlphaComposite.Clear);
        g2d.fillRect(0, 0, transparentImage.getWidth(), transparentImage.getHeight());

        // Draw the original image with specified alpha
        g2d.setComposite(AlphaComposite.SrcOver.derive((float) alpha));
        g2d.drawImage(originalImage, 0, 0, null);

        g2d.dispose();

        displayedImage = transparentImage;

        if (imageLabel != null) {
            SwingUtilities.invokeLater(() -> {
                imageLabel.setIcon(new ImageIcon(displayedImage));
                imageLabel.repaint();
            });
        }
    }

    public double getAlphaValue() {
        return currentAlphaLevel;
    }
}